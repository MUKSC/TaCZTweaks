package me.muksc.tacztweaks.mixin.feature.general.fixes.third_person_gun_rendering_fix;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tacz.guns.client.model.functional.MuzzleFlashRender;
import com.tacz.guns.client.model.functional.ShellRender;
import com.tacz.guns.client.renderer.other.HumanoidOffhandRender;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemInHandLayer.class, priority = 1500, remap = false)
public abstract class ItemInHandLayerMixinMixin {
    @TargetHandler(
        mixin = "com.tacz.guns.mixin.client.ItemInHandLayerMixin",
        name = "render"
    )
    @WrapMethod(method = "@MixinSquared:Handler")
    private void tacztweaks$render$thirdPersonGunRenderingFix(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo ci, Operation<Void> original) {
        if (Config.General.Fixes.thirdPersonGunRenderingFix()) return;
        original.call(matrixStack, buffer, packedLight, livingEntity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, pNetHeadYaw, pHeadPitch, ci);
    }

    @WrapMethod(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", remap = true)
    private void tacztweaks$render$thirdPersonGunRenderingFix(PoseStack poseStack, MultiBufferSource buffer, int packedLight, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, Operation<Void> original) {
        original.call(poseStack, buffer, packedLight, livingEntity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        if (!Config.General.Fixes.thirdPersonGunRenderingFix()) return;
        MuzzleFlashRender.isSelf = false;
        ShellRender.isSelf = false;
        HumanoidOffhandRender.renderGun(livingEntity, poseStack, buffer, packedLight);
    }
}