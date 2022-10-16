package cf.avicia.avomod2.mixin;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.eventhandlers.hudevents.ReadableMobHealth;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(EntityRenderer.class)
public class EntityLabelRenderMixin {
    @ModifyArg(method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"),
            index = 0)
    private Text renderText(Text label) {
        try {
            return getNewLabel(label);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return label;
    }

    @ModifyArg(method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Lnet/minecraft/text/StringVisitable;)I"),
            index = 0)
    private StringVisitable getWidth(StringVisitable label) {
        try {
            return getNewLabel((Text) label);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return label;
    }

    private Text getNewLabel(Text label) {
        if (ConfigsHandler.getConfigBoolean("readableHealth") && !ConfigsHandler.getConfigBoolean("disableAll")) {
            return ReadableMobHealth.onRenderEntityLabel(label);
        }
        return label;
    }
}
