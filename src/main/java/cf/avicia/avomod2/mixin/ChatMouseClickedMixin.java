package cf.avicia.avomod2.mixin;

import cf.avicia.avomod2.client.customevents.ChatMessageClickedCallback;
import cf.avicia.avomod2.client.customevents.ChatMouseClickedCallback;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ChatHud.class)
public abstract class ChatMouseClickedMixin {
    @Shadow protected abstract int getMessageLineIndex(double chatLineX, double chatLineY);

    @Shadow protected abstract double toChatLineX(double x);

    @Shadow protected abstract double toChatLineY(double y);

    @Shadow @Final public List<ChatHudLine> messages;

    @Shadow @Final public List<ChatHudLine.Visible> visibleMessages;

    @Inject(method = "mouseClicked(DD)Z",
            at = @At("HEAD"), cancellable = true)
    private void mouseClicked(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        try {
            List<ActionResult> results = new ArrayList<>();
            int index = this.getMessageLineIndex(this.toChatLineX(mouseX), this.toChatLineY(mouseY));
            if (index != -1) {
                Map<Integer, Integer> visibleToMessageIndex = new HashMap<>();
                int messageIndex = -1;
                for (int i = 0; i < visibleMessages.size(); i++) {
                    if (visibleMessages.get(i).endOfEntry()) {
                        ++messageIndex;
                    }
                    visibleToMessageIndex.put(i, messageIndex);
                }
                int clickedMessageIndex = visibleToMessageIndex.getOrDefault(index, -1);
                if (clickedMessageIndex != -1 && clickedMessageIndex < messages.size()) {
                    results.add(ChatMessageClickedCallback.EVENT.invoker().messageClicked(messages.get(clickedMessageIndex).content()));
                }
            }
            results.add(ChatMouseClickedCallback.EVENT.invoker().mouseClicked(mouseX, mouseY));
            if (results.contains(ActionResult.FAIL)) {
                // Canceling this doesn't seem to actually do anything, but I'll keep it in for now
                cir.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}