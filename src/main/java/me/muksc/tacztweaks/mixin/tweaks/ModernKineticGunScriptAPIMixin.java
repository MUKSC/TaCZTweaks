package me.muksc.tacztweaks.mixin.tweaks;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import com.tacz.guns.sound.SoundManager;
import me.muksc.tacztweaks.TaCZTweaks;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.world.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public abstract class ModernKineticGunScriptAPIMixin {
    @Shadow private LivingEntity shooter;

    @ModifyExpressionValue(method = "lambda$shootOnce$2", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lcom/tacz/guns/sound/SoundManager;SILENCE_3P_SOUND:Ljava/lang/String;"))
    private String tacztweaks$shootOnce$forceFirstPersonShootSound$silenced(String original) {
        if (!Config.Tweaks.INSTANCE.forceFirstPersonShootingSound()) return original;
        return SoundManager.SILENCE_SOUND;
    }

    @ModifyExpressionValue(method = "lambda$shootOnce$2", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lcom/tacz/guns/sound/SoundManager;SHOOT_3P_SOUND:Ljava/lang/String;"))
    private String tacztweaks$shootOnce$forceFirstPersonShootSound$normal(String original) {
        if (!Config.Tweaks.INSTANCE.forceFirstPersonShootingSound()) return original;
        return SoundManager.SHOOT_SOUND;
    }

    @Definition(id = "Map", type = Map.class)
    @Definition(id = "getCache", method = "Lcom/tacz/guns/resource/modifier/AttachmentCacheProperty;getCache(Lcom/tacz/guns/api/GunProperty;)Ljava/lang/Object;")
    @Definition(id = "INACCURACY", field = "Lcom/tacz/guns/api/GunProperties;INACCURACY:Lcom/tacz/guns/api/GunProperty;")
    @Definition(id = "get", method = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;")
    @Expression("((Map) ?.getCache(INACCURACY)).get(?)")
    @WrapOperation(method = "shootOnce", at = @At("MIXINEXTRAS:EXPRESSION"))
    private Object tacztweaks$shootOnce$betterInaccuracy(Map<InaccuracyType, Float> instance, Object o, Operation<Float> original) {
        if (!Config.Tweaks.INSTANCE.betterInaccuracy()) return original.call(instance, o);
        return TaCZTweaks.getBetterInaccuracy(instance, shooter);
    }
}
