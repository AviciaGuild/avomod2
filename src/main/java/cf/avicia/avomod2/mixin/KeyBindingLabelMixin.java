package cf.avicia.avomod2.mixin;

import cf.avicia.avomod2.client.AvoMod2Client;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.Category.class)
public class KeyBindingLabelMixin {
    @Shadow
    @Final
    private Identifier id;

    @Inject(
            method = "getLabel()Lnet/minecraft/text/Text;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void getNewLabel(CallbackInfoReturnable<Text> cir) {
        try {
            if (id.equals(AvoMod2Client.avomodCategory.id())) {
                cir.setReturnValue(Text.of("Avomod"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
