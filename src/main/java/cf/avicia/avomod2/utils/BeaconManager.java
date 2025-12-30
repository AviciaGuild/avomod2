package cf.avicia.avomod2.utils;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.awt.*;

public class BeaconManager {
    public static Coordinates compassLocation = null;
    public static String compassTerritory = null;

    public static Coordinates soonestTerritoryLocation = null;
    public static String soonestTerritory = null;

    public static void drawBeamWithTitle(WorldRenderContext ctx, Coordinates loc, Color color, String title) {
        MatrixStack matrices = ctx.matrices();
        Camera camera = ctx.gameRenderer().getCamera();
        VertexConsumerProvider vertexConsumerProvider = ctx.consumers();
        if (camera == null || vertexConsumerProvider == null || MinecraftClient.getInstance().player == null) return;

        double titleDeltaX = loc.x() - camera.getCameraPos().x, beaconDeltaX = loc.x() - camera.getCameraPos().x;
        double titleDeltaY = 0, beaconDeltaY = -300;
        double titleDeltaZ = loc.z() - camera.getCameraPos().z, beaconDeltaZ = loc.z() - camera.getCameraPos().z;
        double distSq = beaconDeltaX * titleDeltaX + beaconDeltaY * titleDeltaY + beaconDeltaZ * titleDeltaZ;
        double dist = Math.sqrt(distSq);
        int maxDistance = MinecraftClient.getInstance().options.getClampedViewDistance() * 15;
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
        BeaconBlockEntityRenderer.renderBeam(matrices, ctx.commandQueue(), BeaconBlockEntityRenderer.BEAM_TEXTURE, camera.getLastTickProgress(), 1.0F, camera.getFocusedEntity().age, 0, BeaconBlockEntityRenderer.MAX_BEAM_HEIGHT, color.getRGB(), 0.2F);
        matrices.pop();

        matrices.push();
        matrices.translate(titleDeltaX, titleDeltaY, titleDeltaZ);
        matrices.multiply(camera.getRotation());
        matrices.scale(-0.025F, -0.025F, 0.025F);

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        int backgroundColor = 500_000_000;
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        float titleX = (float) (-textRenderer.getWidth(title) / 2);
        textRenderer.draw(title, titleX, 0f, 0xffffff, false, matrix4f, ctx.consumers(), TextRenderer.TextLayerType.SEE_THROUGH, backgroundColor, 255);
        String distance = "§e" + Math.round(dist) + "m";
        float distanceX = (float) (-textRenderer.getWidth(distance) / 2);
        textRenderer.draw(distance, distanceX, -10f, 0xffffff, false, matrix4f, ctx.consumers(), TextRenderer.TextLayerType.SEE_THROUGH, backgroundColor, 255);
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
