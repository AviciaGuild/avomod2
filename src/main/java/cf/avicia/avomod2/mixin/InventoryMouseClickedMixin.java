package cf.avicia.avomod2.mixin;

import cf.avicia.avomod2.client.customevents.InventoryMouseClickedCallback;
import net.minecraft.client.gui.Click;
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

    @Inject(method = "mouseClicked(Lnet/minecraft/client/gui/Click;Z)Z",
            at = @At("HEAD"), cancellable = true)
    private void mouseClicked(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        try {
            ActionResult result = InventoryMouseClickedCallback.EVENT.invoker().mouseClicked(click.x(), click.y(), click.button(), this.getSlotAt(click.x(), click.y()), this.getScreenHandler());
            if (result == ActionResult.FAIL) {
                cir.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}