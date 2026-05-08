package me.muksc.tacztweaks.mixin.tweaks;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.resource.modifier.custom.RpmModifier;
import me.muksc.tacztweaks.config.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RpmModifier.class, remap = false)
public abstract class RPMModifierMixin {
    @OnlyIn(Dist.CLIENT)
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunData;getRoundsPerMinute(Lcom/tacz/guns/api/item/gun/FireMode;)I"))
    private int tacztweaks$getPropertyDiagramsData$rps$original(int original) {
        return Config.Tweaks.INSTANCE.rps() ? original / 60 : original;
    }

    @OnlyIn(Dist.CLIENT)
    @Definition(id = "Integer", type = Integer.class)
    @Definition(id = "getCache", method = "Lcom/tacz/guns/resource/modifier/AttachmentCacheProperty;getCache(Ljava/lang/String;)Ljava/lang/Object;")
    @Expression("(Integer) ?.getCache(?)")
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At("MIXINEXTRAS:EXPRESSION"))
    private <T> Integer tacztweaks$getPropertyDiagramsData$rps$modified(Integer original) {
        return Config.Tweaks.INSTANCE.rps() ? original / 60 : original;
    }

    @OnlyIn(Dist.CLIENT)
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At(value = "CONSTANT", args = "doubleValue=1200.0"))
    private double tacztweaks$getPropertyDiagramsData$divisor(double original) {
        return Config.Tweaks.INSTANCE.rps() ? original / 60 : original;
    }

    @OnlyIn(Dist.CLIENT)
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At(value = "CONSTANT", args = "stringValue=gui.tacz.gun_refit.property_diagrams.rpm"))
    private String tacztweaks$getPropertyDiagramsData$rps$diagram(String original) {
        return Config.Tweaks.INSTANCE.rps() ? "tacztweaks.property_diagrams.rps" : original;
    }

    @OnlyIn(Dist.CLIENT)
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At(value = "CONSTANT", args = "stringValue=%drpm §a(+%d)"))
    private String tacztweaks$getPropertyDiagramsData$rps$positive(String original) {
        return Config.Tweaks.INSTANCE.rps() ? "%drps §a(+%d)" : original;
    }

    @OnlyIn(Dist.CLIENT)
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At(value = "CONSTANT", args = "stringValue=%drpm §c(%d)"))
    private String tacztweaks$getPropertyDiagramsData$rps$negative(String original) {
        return Config.Tweaks.INSTANCE.rps() ? "%drps §c(%d)" : original;
    }

    @OnlyIn(Dist.CLIENT)
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At(value = "CONSTANT", args = "stringValue=%drpm"))
    private String tacztweaks$getPropertyDiagramsData$rps$default(String original) {
        return Config.Tweaks.INSTANCE.rps() ? "%drps" : original;
    }
}