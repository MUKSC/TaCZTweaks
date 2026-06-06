package me.muksc.tacztweaks.mixin.feature.gameplay.behaviour.realistic_inaccuracy;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.feature.gameplay.behaviour.RealisticInaccuracy;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public abstract class ModernKineticGunScriptAPIMixin {
    @Shadow private LivingEntity shooter;

    @Definition(id = "Map", type = Map.class)
    @Definition(id = "getCache", method = "Lcom/tacz/guns/resource/modifier/AttachmentCacheProperty;getCache(Lcom/tacz/guns/api/GunProperty;)Ljava/lang/Object;")
    @Definition(id = "INACCURACY", field = "Lcom/tacz/guns/api/GunProperties;INACCURACY:Lcom/tacz/guns/api/GunProperty;")
    @Definition(id = "get", method = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;")
    @Expression("((Map) ?.getCache(INACCURACY)).get(?)")
    @WrapOperation(method = "shootOnce", at = @At("MIXINEXTRAS:EXPRESSION"))
    private Object tacztweaks$shootOnce$realisticInaccuracy(Map<InaccuracyType, Float> instance, Object o, Operation<Float> original) {
        return Config.Gameplay.Behaviour.realisticInaccuracy()
            ? RealisticInaccuracy.getRealisticInaccuracy(instance, shooter)
            : original.call(instance, o);
    }
}