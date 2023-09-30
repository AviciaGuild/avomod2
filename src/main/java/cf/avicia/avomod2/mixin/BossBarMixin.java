package cf.avicia.avomod2.mixin;

import cf.avicia.avomod2.client.customevents.RenderBossBarCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class BossBarMixin {
    @Inject(method = "renderBossBar(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/entity/boss/BossBar;)V",
            at = @At("HEAD"), cancellable = true)
    private void onRender(DrawContext drawContext, int x, int y, BossBar bossBar, CallbackInfo ci) {
        try {
            ActionResult result = RenderBossBarCallback.EVENT.invoker().onRender(drawContext, x, y, bossBar);
            if (result == ActionResult.FAIL) {
                ci.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}