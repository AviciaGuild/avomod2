package cf.avicia.avomod2.utils;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import java.awt.*;

public class BeaconManager {
    public static Coordinates compassLocation = null;
    public static String compassTerritory = null;

    public static Coordinates soonestTerritoryLocation = null;
    public static String soonestTerritory = null;

    public static void drawBeamWithTitle(WorldRenderContext ctx, Coordinates loc, Color color, String title) {
        MatrixStack matrices = ctx.matrixStack();
        Camera camera = ctx.camera();
        VertexConsumerProvider vertexConsumerProvider = ctx.consumers();
        if (camera == null || vertexConsumerProvider == null || MinecraftClient.getInstance().player == null) return;

        double titleDeltaX = loc.x() - camera.getPos().x, beaconDeltaX = loc.x() - camera.getPos().x;
        double titleDeltaY = 0, beaconDeltaY = -300;
        double titleDeltaZ = loc.z() - camera.getPos().z, beaconDeltaZ = loc.z() - camera.getPos().z;
        double distSq = beaconDeltaX * titleDeltaX + beaconDeltaY * titleDeltaY + beaconDeltaZ * titleDeltaZ;
        double dist = Math.sqrt(distSq);
        int maxDistance = MinecraftClient.getInstance().options.viewDistance * 15;
        if (dist > maxDistance) {
            double scale = maxDistance / dist;

            beaconDeltaX *= scale;
            beaconDeltaZ *= scale;
        }
        if (distSq > 144) {
            titleDeltaX *= 12 / dist;
            titleDeltaZ *= 12 / dist;
        }

        matrices.push();
        matrices.translate(beaconDeltaX, beaconDeltaY, beaconDeltaZ);
        BeaconBlockEntityRenderer.renderBeam(matrices, vertexConsumerProvider, BeaconBlockEntityRenderer.BEAM_TEXTURE, ctx.tickDelta(), 1.0F, ctx.world().getTime(), 0, BeaconBlockEntityRenderer.MAX_BEAM_HEIGHT, color.getRGBComponents(new float[4]), 0.2F, 0.25F);
        matrices.pop();

        matrices.push();
        matrices.translate(titleDeltaX, titleDeltaY, titleDeltaZ);
        matrices.multiply(camera.getRotation());
        matrices.scale(-0.025F, -0.025F, 0.025F);

        Matrix4f matrix4f = matrices.peek().getPositionMatrix().copy();
        int backgroundColor = 500_000_000;
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        float territoryNameWidth = (float) (-textRenderer.getWidth(title) / 2);
        textRenderer.draw(title, territoryNameWidth, 0f, 0xffffff, false, matrix4f, ctx.consumers(), true, backgroundColor, 255);
        String distance = "§e" + Math.round(dist) + "m";
        float distanceWidth = (float) (-textRenderer.getWidth(distance) / 2);
        textRenderer.draw(distance, distanceWidth, -10f, 0xffffff, false, matrix4f, ctx.consumers(), true, backgroundColor, 255);
        matrices.pop();
    }

    public static void onWorldRender(WorldRenderContext ctx) {
        if (ConfigsHandler.getConfigBoolean("disableAll")) return;
        if (compassLocation != null) {
            drawBeamWithTitle(ctx, compassLocation, new Color(0, 50, 150, 255), compassTerritory);
        }

        if (soonestTerritoryLocation != null && ConfigsHandler.getConfigBoolean("greenBeacon")) {
            drawBeamWithTitle(ctx, soonestTerritoryLocation, new Color(50, 150, 0, 255), soonestTerritory);
        }
    }
}
