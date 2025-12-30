package cf.avicia.avomod2.mixin;

import cf.avicia.avomod2.client.customevents.RenderItemCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public class ItemRenderMixin {
    @Inject(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;III)V"
            , at = @At("HEAD"))
    private void renderItem(LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, CallbackInfo ci) {
        try {
            RenderItemCallback.EVENT.invoker().onRenderItem(stack);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
