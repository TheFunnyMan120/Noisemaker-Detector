package com.precdev.noisemakerdetector.render;

import com.precdev.noisemakerdetector.NoisemakerDetectorClient;
import com.precdev.noisemakerdetector.config.ConfigManager;
import com.precdev.noisemakerdetector.config.ModConfig;
import com.precdev.noisemakerdetector.detection.Contraption;
import com.precdev.noisemakerdetector.detection.ScanCache;
import com.precdev.noisemakerdetector.detection.ScanResult;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.*;

public class ContraptionRenderer {

    private static final VoxelShape BLOCK_SHAPE = Shapes.block();

    // Pipeline registered eagerly during class loading — must happen before shader compilation.
    // Uses LINES_SNIPPET as base (shaders, vertex format, uniforms) and overrides depth test.
    // Same pattern as Meteor Client / ChestESP.
    private static final RenderPipeline ESP_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                    .withLocation(Identifier.parse("noisemakerdetector:esp_lines"))
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .withDepthWrite(false)
                    .withCull(false)
                    .build()
    );

    // RenderType wrapping the pipeline — matches vanilla LINES setup
    // (VIEW_OFFSET_Z_LAYERING + ITEM_ENTITY_TARGET) but with our no-depth-test pipeline.
    private static final RenderType ESP_LINES = RenderType.create(
            "noisemakerdetector_esp",
            RenderSetup.builder(ESP_PIPELINE)
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .createRenderSetup()
    );

    /**
     * Forces class loading so the pipeline is registered before shader compilation.
     * Call from client init.
     */
    public static void init() {
        // Static fields above are initialized when this class loads.
    }

    public static void render(PoseStack poseStack, float partialTick) {
        if (!NoisemakerDetectorClient.isEnabled()) return;

        ModConfig config = ConfigManager.getConfig();
        if (!config.espEnabled && !config.beaconBeamsEnabled && !config.labelsEnabled) return;

        ScanCache cache = NoisemakerDetectorClient.getScanScheduler().getCache();
        ScanResult result = cache.getLastResult();
        if (result == null || result.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Vec3 cam = mc.gameRenderer.getMainCamera().position();

        List<Contraption> sorted = new ArrayList<>(result.getContraptions());
        sorted.sort(Comparator.comparingDouble(c -> c.getCentroid().distanceToSqr(cam)));

        int maxRender = Math.min(config.maxContraptionsRendered, sorted.size());

        try (ByteBufferBuilder byteBuffer = new ByteBufferBuilder(131072)) {
            MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(byteBuffer);

            for (int i = 0; i < maxRender; i++) {
                Contraption contraption = sorted.get(i);

                if (config.espEnabled) {
                    renderESPBoxes(poseStack, cam, contraption, config, bufferSource);
                }

                if (config.highlightRedstone) {
                    renderRedstoneBoxes(poseStack, cam, contraption, config, bufferSource);
                }

                if (config.beaconBeamsEnabled) {
                    renderBeaconBeam(poseStack, cam, contraption, config, bufferSource);
                }

                if (config.labelsEnabled) {
                    renderLabel(poseStack, cam, contraption, bufferSource);
                }
            }

            bufferSource.endBatch();
        }
    }

    private static void renderESPBoxes(PoseStack poseStack, Vec3 cam,
                                        Contraption contraption, ModConfig config,
                                        MultiBufferSource bufferSource) {
        VertexConsumer consumer = bufferSource.getBuffer(ESP_LINES);

        for (Map.Entry<BlockPos, String> entry : contraption.getNoisemakerTypes().entrySet()) {
            BlockPos pos = entry.getKey();
            String type = entry.getValue();
            int color = config.getColorForBlockType(type);

            double x = pos.getX() - cam.x;
            double y = pos.getY() - cam.y;
            double z = pos.getZ() - cam.z;

            ShapeRenderer.renderShape(poseStack, consumer, BLOCK_SHAPE, x, y, z, color, config.lineWidth);
        }
    }

    private static void renderRedstoneBoxes(PoseStack poseStack, Vec3 cam,
                                             Contraption contraption, ModConfig config,
                                             MultiBufferSource bufferSource) {
        VertexConsumer consumer = bufferSource.getBuffer(ESP_LINES);
        int color = config.redstoneColor;

        for (BlockPos pos : contraption.getRedstonePositions()) {
            double x = pos.getX() - cam.x;
            double y = pos.getY() - cam.y;
            double z = pos.getZ() - cam.z;

            ShapeRenderer.renderShape(poseStack, consumer, BLOCK_SHAPE, x, y, z, color, config.lineWidth);
        }
    }

    private static void renderBeaconBeam(PoseStack poseStack, Vec3 cam,
                                          Contraption contraption, ModConfig config,
                                          MultiBufferSource bufferSource) {
        Vec3 centroid = contraption.getCentroid();
        // Beam uses vanilla depth-tested lines — renders behind walls
        VertexConsumer consumer = bufferSource.getBuffer(RenderTypes.lines());

        double x = centroid.x - cam.x;
        double y = centroid.y - cam.y;
        double z = centroid.z - cam.z;

        int color = config.beamColor;

        poseStack.pushPose();
        poseStack.translate(x, y, z);

        consumer.addVertex(poseStack.last(), 0, 0, 0)
                .setColor(color)
                .setNormal(poseStack.last(), 0, 1, 0)
                .setLineWidth(config.lineWidth * 6);
        consumer.addVertex(poseStack.last(), 0, (float) RenderConstants.BEAM_HEIGHT, 0)
                .setColor(color)
                .setNormal(poseStack.last(), 0, 1, 0)
                .setLineWidth(config.lineWidth * 6);

        poseStack.popPose();
    }

    private static void renderLabel(PoseStack poseStack, Vec3 cam,
                                     Contraption contraption,
                                     MultiBufferSource bufferSource) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        Vec3 centroid = contraption.getCentroid();

        double x = centroid.x - cam.x;
        double y = centroid.y - cam.y;
        double z = centroid.z - cam.z;

        // Don't render labels too far away
        double distSq = x * x + y * y + z * z;
        if (distSq > 128 * 128) return;

        // Build label text: "Contraption: 3 door, 2 bell (7 redstone)"
        String label = buildLabel(contraption);

        poseStack.pushPose();
        poseStack.translate(x, y + 1.5, z); // slightly above centroid
        poseStack.mulPose(mc.gameRenderer.getMainCamera().rotation()); // billboard — face camera
        poseStack.scale(-0.025f, -0.025f, 0.025f); // scale to world size, flip Y

        Component text = Component.literal(label);
        float textWidth = font.width(text);

        // Draw text with SEE_THROUGH (no depth test) and a dark background
        font.drawInBatch(text, -textWidth / 2f, 0, 0xFFFFFFFF, false,
                poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH,
                0x80000000, 0xF000F0);

        poseStack.popPose();
    }

    private static String buildLabel(Contraption contraption) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String type : contraption.getNoisemakerTypes().values()) {
            counts.merge(type, 1, Integer::sum);
        }

        StringJoiner joiner = new StringJoiner(", ");
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            joiner.add(entry.getValue() + " " + entry.getKey());
        }

        int redstoneCount = contraption.getRedstonePositions().size();
        if (redstoneCount > 0) {
            return joiner + " (" + redstoneCount + " redstone)";
        }
        return joiner.toString();
    }
}
