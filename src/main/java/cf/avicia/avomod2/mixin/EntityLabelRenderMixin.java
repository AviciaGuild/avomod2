package cf.avicia.avomod2.mixin;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.eventhandlers.hudevents.ReadableMobHealth;
import cf.avicia.avomod2.client.eventhandlers.hudevents.SeasonRatingLeaderboardHelper;
import cf.avicia.avomod2.utils.Utils;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.regex.Pattern;


@Mixin(EntityRenderer.class)
public class EntityLabelRenderMixin {
    @Inject(
            method = "getDisplayName",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifyDisplayName(Entity entity, CallbackInfoReturnable<Text> cir) {
        try {
            cir.setReturnValue(getNewLabel(entity.getDisplayName()));
        } catch (Exception e) {
            e.printStackTrace();
            cir.setReturnValue(entity.getDisplayName());
        }
    }


//    @ModifyArg(method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I"),
//            index = 0)
//    private StringVisitable getWidth(StringVisitable label) {
//        try {
//            return getNewLabel((Text) label);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return label;
//    }

    private Text getNewLabel(Text label) {
        if (!ConfigsHandler.getConfigBoolean("disableAll")) {
            String unformattedLabel = Utils.getUnformattedString(label.getString());
            if (ConfigsHandler.getConfigBoolean("readableHealth") &&
                    unformattedLabel != null && unformattedLabel.contains("[|||||")) {
                return ReadableMobHealth.onRenderEntityLabel(label);
            }
            Pattern leaderboardPattern = Pattern.compile("\\d+ - .+ \\(.+ SR\\)");
            if (leaderboardPattern.matcher(unformattedLabel).find()) {
                return SeasonRatingLeaderboardHelper.onRenderEntityLabel(label);
            }
        }
        return label;
    }
}
