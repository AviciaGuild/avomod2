package cf.avicia.avomod2.client.renderer;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.configs.locations.LocationsHandler;
import cf.avicia.avomod2.client.locationselements.Element;
import cf.avicia.avomod2.client.locationselements.ElementGroup;
import cf.avicia.avomod2.client.locationselements.RectangleElement;
import cf.avicia.avomod2.client.locationselements.TextElement;
import cf.avicia.avomod2.utils.BeaconManager;
import cf.avicia.avomod2.utils.TerritoryData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import oshi.util.tuples.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;

public class TerritoryOutlineRenderer {
    private static KeyBinding keyBinding;
    private static boolean showOutline = false;

    public static void initKeybind() {
        keyBinding = new KeyBinding("Toggle territory highlights", InputUtil.GLFW_KEY_COMMA, "Avomod");
        KeyBindingRegistryImpl.registerKeyBinding(keyBinding);
    }

    public static void onTick() {
        if (MinecraftClient.getInstance().player == null) return;

        if (keyBinding.wasPressed()) {
            showOutline = !showOutline;
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(showOutline ? "§7Territory outlines §aEnabled" : "§7Territory outlines §cDisabled"));
        }
    }

    public static void renderOutline(WorldRenderContext context) {
        if (!showOutline && BeaconManager.soonestTerritory == null) {
            return;
        }
        if (MinecraftClient.getInstance().player != null && context.matrixStack() != null && TerritoryData.territoryData != null) {
            Camera camera = context.camera();
            Vec3d playerPos = MinecraftClient.getInstance().player.getPos();
            int xPos = MinecraftClient.getInstance().player.getBlockPos().getX();
            int yPos = MinecraftClient.getInstance().player.getBlockPos().getY();
            int zPos = MinecraftClient.getInstance().player.getBlockPos().getZ();
            String currentTerritory = TerritoryData.territoryAtCoordinates(new Pair<>(xPos, zPos));
            Iterable<Map.Entry<String, JsonElement>> entriesToLoop;
            if (showOutline) {
                entriesToLoop = TerritoryData.territoryData.entrySet();
            } else if (ConfigsHandler.getConfigBoolean("outlineSoonestWarTerr") && BeaconManager.soonestTerritory != null && TerritoryData.territoryData.has(BeaconManager.soonestTerritory)) {
                JsonElement territoryElement = TerritoryData.territoryData.get(BeaconManager.soonestTerritory);
                Map.Entry<String, JsonElement> singleEntry =
                        new AbstractMap.SimpleEntry<>(BeaconManager.soonestTerritory, territoryElement);
                entriesToLoop = Collections.singletonList(singleEntry);
            } else {
                return;
            }

            for (Map.Entry<String, JsonElement> territory : entriesToLoop) {
                JsonObject locationObject = territory.getValue().getAsJsonObject().getAsJsonObject("location");
                int apiStartX = locationObject.get("start").getAsJsonArray().get(0).getAsInt();
                int apiStartZ = locationObject.get("start").getAsJsonArray().get(1).getAsInt();
                int apiEndX = locationObject.get("end").getAsJsonArray().get(0).getAsInt();
                int apiEndZ = locationObject.get("end").getAsJsonArray().get(1).getAsInt();
                int startX = Math.min(apiStartX, apiEndX);
                int startZ = Math.min(apiStartZ, apiEndZ);
                int endX = Math.max(apiStartX, apiEndX);
                int endZ = Math.max(apiStartZ, apiEndZ);

                double dist = Math.min(Math.abs(playerPos.x - startX), Math.abs(playerPos.x - endX)) + Math.min(Math.abs(playerPos.z - startZ), Math.abs(playerPos.z - endZ));
                int maxDistance = Math.min(MinecraftClient.getInstance().options.getClampedViewDistance(), 16) * 15;
                boolean inTerr = currentTerritory != null && currentTerritory.equals(territory.getKey());
                if (!inTerr && dist > maxDistance) {
                    continue;
                }

                for (int level = yPos / 4 - 20; level < yPos / 4 + 20; level++) {
                    VertexRendering.drawBox(
                        context.matrixStack(),
                        context.consumers().getBuffer(RenderLayer.LINES),
                        startX - camera.getPos().x,
                        4 * level - camera.getPos().y,
                        startZ - camera.getPos().z,
                        endX - camera.getPos().x,
                        4 * level - camera.getPos().y,
                        endZ - camera.getPos().z,
                        inTerr ? 0 : 1,
                        inTerr ? 1 : 0,
                        0,
                        .5f
                    );
                }

                if (!ConfigsHandler.getConfigBoolean("highlightFloor")) {
                    continue;
                }
                // Render "Carpet" or "Floor" at block surface, in patches at a time
                MatrixStack matrixStack = context.matrixStack();
                World world = context.world();
                MinecraftClient client = MinecraftClient.getInstance();
                double camX = client.gameRenderer.getCamera().getPos().x;
                double camY = client.gameRenderer.getCamera().getPos().y;
                double camZ = client.gameRenderer.getCamera().getPos().z;

                int width = endX - startX;
                int depth = endZ - startZ;

                double[][] topY = new double[width][depth];
                boolean[][] visited = new boolean[width][depth];
                BlockPos.Mutable pos = new BlockPos.Mutable();

                double maxDistanceCarpet = 64.0;
                double maxDistSq = maxDistanceCarpet * maxDistanceCarpet;

                // Step 1: Precompute surface heights only for nearby blocks
                for (int dx = 0; dx < width; dx++) {
                    int x = startX + dx;
                    double dxSq = (x + 0.5 - camX) * (x + 0.5 - camX);

                    for (int dz = 0; dz < depth; dz++) {
                        int z = startZ + dz;
                        double dzSq = (z + 0.5 - camZ) * (z + 0.5 - camZ);

                        if (dxSq + dzSq > maxDistSq) {
                            topY[dx][dz] = Integer.MIN_VALUE;
                            visited[dx][dz] = true;
                            continue;
                        }

                        for (int y = (int) camY - 1; y > camY - 10; y--) {
                            pos.set(x, y + 1, z);
                            if (world.getBlockState(pos).isOpaque() || !world.getBlockState(pos).getFluidState().isOf(Fluids.EMPTY)) {
                                topY[dx][dz] = Integer.MIN_VALUE;
                                break;
                            }
                            pos.set(x, y, z);
                            if (world.getBlockState(pos).isOpaque() || !world.getBlockState(pos).getFluidState().isOf(Fluids.EMPTY)) {
                                double heightModifier = 0;
                                String itemName = world.getBlockState(pos).getBlock().getName().getString();
                                if (itemName.contains("Slab")) {
                                    if (world.getBlockState(pos).get(Properties.SLAB_TYPE) == SlabType.BOTTOM) {
                                        heightModifier = 0.49;
                                    }
                                } else if (itemName.contains("Carpet")) {
                                    heightModifier = 0.94;
                                } else if (!world.getBlockState(pos).getFluidState().isOf(Fluids.EMPTY)) {
                                    heightModifier = 0.2;
                                }
                                topY[dx][dz] = y - heightModifier;
                                break;
                            }
                        }
                    }
                }

                // Step 2: Greedy 2D merging
                for (int dx = 0; dx < width; dx++) {
                    for (int dz = 0; dz < depth; dz++) {
                        if (visited[dx][dz]) continue;

                        double y = topY[dx][dz];
                        if (y == 0) continue;

                        int maxDx = dx;
                        while (maxDx < width && !visited[maxDx][dz] && topY[maxDx][dz] == y) maxDx++;

                        int maxDz = dz;
                        outer:
                        while (maxDz < depth) {
                            for (int x = dx; x < maxDx; x++) {
                                if (visited[x][maxDz] || topY[x][maxDz] != y) break outer;
                            }
                            maxDz++;
                        }

                        for (int x = dx; x < maxDx; x++) {
                            for (int z = dz; z < maxDz; z++) {
                                visited[x][z] = true;
                            }
                        }

                        int x1 = startX + dx;
                        int z1 = startZ + dz;
                        int x2 = startX + maxDx;
                        int z2 = startZ + maxDz;

                        Box box = new Box(x1, y + 1, z1, x2, y + 1.01, z2);

                        if (!context.frustum().isVisible(box)) continue;

                        DebugRenderer.drawBox(
                            matrixStack,
                            client.getBufferBuilders().getEntityVertexConsumers(),
                            x1 - camX, y + 1 - camY, z1 - camZ,
                            x2 - camX, y + 1.01 - camY, z2 - camZ,
                            inTerr ? 0 : 1,
                            inTerr ? 1 : 0,
                            0,
                            0.5f
                        );
                    }
                }

            }
        }
    }

    public static void renderText(DrawContext drawContext) {
        getElementsToDraw().draw(drawContext);
    }

    public static ElementGroup getElementsToDraw() {
        List<Element> elementsList = new ArrayList<>();
        float scale = 1F;
        if (!showOutline) {
            return new ElementGroup("territoryName", scale, elementsList);
        }
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int rectangleHeight = 12;
        float startY = LocationsHandler.getStartY("territoryName", scale);
        if (MinecraftClient.getInstance().player != null) {
            int xPos = MinecraftClient.getInstance().player.getBlockPos().getX();
            int zPos = MinecraftClient.getInstance().player.getBlockPos().getZ();
            String currentTerritory = TerritoryData.territoryAtCoordinates(new Pair<>(xPos, zPos));
            if (currentTerritory != null) {
                int rectangleWidth = textRenderer.getWidth(currentTerritory) + 4;
                float yourWorldStartX = LocationsHandler.getStartX("territoryName", rectangleWidth, scale);
                elementsList.add(new RectangleElement(yourWorldStartX, startY + rectangleHeight, rectangleWidth, rectangleHeight, scale, new Color(0, 0, 255, 100)));
                elementsList.add(new TextElement(currentTerritory, yourWorldStartX + 2, startY + 2 + rectangleHeight, scale, Color.WHITE));
            }
        }

        return new ElementGroup("territoryName", scale, elementsList);
    }

    public static ElementGroup getElementsToDraw(String placeholder) {
        List<Element> elementsList = new ArrayList<>();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int rectangleHeight = 12;
        float scale = 1F;
        float startY = LocationsHandler.getStartY("territoryName", scale);
        int rectangleWidth = textRenderer.getWidth(placeholder) + 4;
        float yourWorldStartX = LocationsHandler.getStartX("territoryName", rectangleWidth, scale);
        elementsList.add(new RectangleElement(yourWorldStartX, startY + rectangleHeight, rectangleWidth, rectangleHeight, scale, new Color(0, 0, 255, 100)));
        elementsList.add(new TextElement(placeholder, yourWorldStartX + 2, startY + 2 + rectangleHeight, scale, Color.WHITE));


        return new ElementGroup("territoryName", scale, elementsList);
    }
}
