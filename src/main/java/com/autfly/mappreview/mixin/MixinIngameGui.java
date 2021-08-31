package com.autfly.mappreview.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IngameGui.class)
public class MixinIngameGui {
    private final Minecraft mc = Minecraft.getInstance();
    @Redirect(method = "renderHotbarItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItemAndEffectIntoGUI(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;II)V"))
    public void onRenderHotbarItem(ItemRenderer itemRenderer, LivingEntity entityIn, ItemStack stack, int x, int y){
        if (this.mc.world == null) return;
        if (stack.getItem() == Items.FILLED_MAP && FilledMapItem.getData(stack, this.mc.world) != null){
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float) x, (float) y, 1.0F);
            RenderSystem.scalef(0.125F, 0.125F, 1.0F);
            IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
            mc.gameRenderer.getMapItemRenderer().renderMap(new MatrixStack(), buffer, FilledMapItem.getData(stack, this.mc.world), true, 15728880);
            buffer.finish();
            RenderSystem.popMatrix();
            return;
        }
        itemRenderer.renderItemAndEffectIntoGUI(entityIn, stack, x, y);
    }
}
