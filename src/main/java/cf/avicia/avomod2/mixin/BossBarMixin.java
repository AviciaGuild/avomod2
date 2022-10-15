package cf.avicia.avomod2.mixin;

import cf.avicia.avomod2.client.customevents.RenderBossBarCallback;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class BossBarMixin {
    @Inject(method = "renderBossBar(Lnet/minecraft/client/util/math/MatrixStack;IILnet/minecraft/entity/boss/BossBar;)V",
            at = @At("HEAD"), cancellable = true)
    private void onRender(MatrixStack matrices, int x, int y, BossBar bossBar, CallbackInfo ci) {
        try {
            ActionResult result = RenderBossBarCallback.EVENT.invoker().onRender(matrices, x, y, bossBar);
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}