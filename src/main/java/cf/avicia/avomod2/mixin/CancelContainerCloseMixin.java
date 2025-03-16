package cf.avicia.avomod2.mixin;

import cf.avicia.avomod2.client.AvoMod2Client;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class CancelContainerCloseMixin {

    @Inject(method = "close()V", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfo ci) {
        if (AvoMod2Client.cancelContainerClose) {
            ci.cancel();
            AvoMod2Client.cancelContainerClose = false;
        }
    }
}
