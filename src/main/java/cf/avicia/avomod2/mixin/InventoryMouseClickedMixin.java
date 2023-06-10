package cf.avicia.avomod2.mixin;

import cf.avicia.avomod2.client.customevents.InventoryMouseClickedCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class InventoryMouseClickedMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
    @Shadow @Nullable protected abstract Slot getSlotAt(double x, double y);

    @Shadow public abstract T getScreenHandler();

    protected InventoryMouseClickedMixin(Text title) {
        super(title);
    }

    @Inject(method = "mouseClicked(DDI)Z",
            at = @At("HEAD"), cancellable = true)
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        try {
            ActionResult result = InventoryMouseClickedCallback.EVENT.invoker().mouseClicked(mouseX, mouseY, button, this.getSlotAt(mouseX, mouseY), this.getScreenHandler());
            if (result == ActionResult.FAIL) {
                cir.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}