package me.muksc.tacztweaks.mixin.feature.balancing.vertical_recoil;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.resource.modifier.custom.RecoilModifier;
import me.muksc.tacztweaks.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RecoilModifier.class, remap = false)
public abstract class RecoilModifierMixin {
    @Definition(id = "getPitch", method = "Lcom/tacz/guns/resource/pojo/data/gun/GunRecoil;getPitch()[Lcom/tacz/guns/resource/pojo/data/gun/GunRecoilKeyFrame;")
    @Definition(id = "getMaxInGunRecoilKeyFrame", method = "Lcom/tacz/guns/resource/modifier/custom/RecoilModifier;getMaxInGunRecoilKeyFrame([Lcom/tacz/guns/resource/pojo/data/gun/GunRecoilKeyFrame;)F")
    @Expression("getMaxInGunRecoilKeyFrame(?.getPitch())")
    @ModifyExpressionValue(method = "initCache", at = @At("MIXINEXTRAS:EXPRESSION"))
    private float tacztweaks$initCache$verticalRecoilModifier(float original) {
        return (float) Config.Balancing.VerticalRecoil.eval(original);
    }

    //~ environment environment_client
    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    @Definition(id = "getPitch", method = "Lcom/tacz/guns/resource/pojo/data/gun/GunRecoil;getPitch()[Lcom/tacz/guns/resource/pojo/data/gun/GunRecoilKeyFrame;")
    @Definition(id = "getMaxInGunRecoilKeyFrame", method = "Lcom/tacz/guns/resource/modifier/custom/RecoilModifier;getMaxInGunRecoilKeyFrame([Lcom/tacz/guns/resource/pojo/data/gun/GunRecoilKeyFrame;)F")
    @Expression("getMaxInGunRecoilKeyFrame(?.getPitch())")
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At("MIXINEXTRAS:EXPRESSION"))
    private float tacztweaks$getPropertyDiagramsData$verticalRecoilModifier(float original) {
        return (float) Config.Balancing.VerticalRecoil.eval(original);
    }
}