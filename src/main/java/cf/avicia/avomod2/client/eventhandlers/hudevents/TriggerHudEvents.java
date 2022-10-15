package cf.avicia.avomod2.client.eventhandlers.hudevents;

import cf.avicia.avomod2.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class TriggerHudEvents {
    public static void trigger(MatrixStack matrixStack, float tickDelta) {
        WorldInfoOnTab.render(matrixStack);
    }
}
