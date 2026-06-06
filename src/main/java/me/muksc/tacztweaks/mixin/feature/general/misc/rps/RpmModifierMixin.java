package me.muksc.tacztweaks.mixin.feature.general.misc.rps;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.resource.modifier.custom.RpmModifier;
import me.muksc.tacztweaks.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RpmModifier.class, remap = false)
public abstract class RpmModifierMixin {
    //~ environment environment_client
    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/GunData;getRoundsPerMinute(Lcom/tacz/guns/api/item/gun/FireMode;)I"))
    private int tacztweaks$getPropertyDiagramsData$rps$original(int original) {
        return Config.General.Miscellaneous.rps() ? original / 60 : original;
    }

    //~ environment environment_client
    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    @Definition(id = "Integer", type = Integer.class)
    @Definition(id = "getCache", method = "Lcom/tacz/guns/resource/modifier/AttachmentCacheProperty;getCache(Ljava/lang/String;)Ljava/lang/Object;")
    @Expression("(Integer) ?.getCache(?)")
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At("MIXINEXTRAS:EXPRESSION"))
    private <T> Integer tacztweaks$getPropertyDiagramsData$rps$modified(Integer original) {
        return Config.General.Miscellaneous.rps() ? original / 60 : original;
    }

    //~ environment environment_client
    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At(value = "CONSTANT", args = "doubleValue=1200.0"))
    private double tacztweaks$getPropertyDiagramsData$divisor(double original) {
        return Config.General.Miscellaneous.rps() ? original / 60 : original;
    }

    //~ environment environment_client
    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At(value = "CONSTANT", args = "stringValue=gui.tacz.gun_refit.property_diagrams.rpm"))
    private String tacztweaks$getPropertyDiagramsData$rps$diagram(String original) {
        return Config.General.Miscellaneous.rps() ? "tacztweaks.property_diagrams.rps" : original;
    }

    //~ environment environment_client
    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At(value = "CONSTANT", args = "stringValue=%drpm §a(+%d)"))
    private String tacztweaks$getPropertyDiagramsData$rps$positive(String original) {
        return Config.General.Miscellaneous.rps() ? "%drps §a(+%d)" : original;
    }

    //~ environment environment_client
    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At(value = "CONSTANT", args = "stringValue=%drpm §c(%d)"))
    private String tacztweaks$getPropertyDiagramsData$rps$negative(String original) {
        return Config.General.Miscellaneous.rps() ? "%drps §c(%d)" : original;
    }

    //~ environment environment_client
    @net.minecraftforge.api.distmarker.OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
    @ModifyExpressionValue(method = "getPropertyDiagramsData", at = @At(value = "CONSTANT", args = "stringValue=%drpm"))
    private String tacztweaks$getPropertyDiagramsData$rps$default(String original) {
        return Config.General.Miscellaneous.rps() ? "%drps" : original;
    }
}