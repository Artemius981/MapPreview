package com.autfly.mappreview.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ContainerScreen.class)
public class MixinContainerScreen {
    private final Minecraft mc = Minecraft.getInstance();
    @Redirect(method = "moveItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItemAndEffectIntoGUI(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;II)V"))
    public void onMoveItems(ItemRenderer itemRenderer, LivingEntity entityIn, ItemStack stack, int x, int y){
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
