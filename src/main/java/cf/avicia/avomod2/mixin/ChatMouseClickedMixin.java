package cf.avicia.avomod2.mixin;

import cf.avicia.avomod2.client.customevents.ChatMessageClickedCallback;
import cf.avicia.avomod2.client.customevents.ChatMouseClickedCallback;
import cf.avicia.avomod2.utils.Utils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatMouseClickedMixin {
    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;IIIZZ)V",
            at = @At("HEAD"))
    private void mouseClicked(DrawContext context, TextRenderer textRenderer, int currentTick, int mouseX, int mouseY, boolean interactable, boolean bl, CallbackInfo ci) {
        try {
            if (interactable && Utils.isMouseDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                List<ActionResult> results = new ArrayList<>();
                Text clickedMessage = Utils.getChatMessageAt(mouseX, mouseY);
                if (clickedMessage != null) {
                    results.add(ChatMessageClickedCallback.EVENT.invoker().messageClicked(clickedMessage));
                }
                results.add(ChatMouseClickedCallback.EVENT.invoker().mouseClicked(mouseX, mouseY));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}