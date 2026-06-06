package me.muksc.tacztweaks.mixin.feature.attribute.stats.damage;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.resource.pojo.data.gun.ExtraDamage;
import me.muksc.tacztweaks.core.extension.DeferredHolderExt;
import me.muksc.tacztweaks.registry.ModAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.LinkedList;
import java.util.stream.Collectors;

@Mixin(value = EntityKineticBullet.class, remap = false)
public abstract class EntityKineticBulletMixin {
    @Definition(id = "damageAmount", field = "Lcom/tacz/guns/entity/EntityKineticBullet;damageAmount:Ljava/util/LinkedList;")
    @Definition(id = "LinkedList", type = LinkedList.class)
    @Definition(id = "getCache", method = "Lcom/tacz/guns/resource/modifier/AttachmentCacheProperty;getCache(Ljava/lang/String;)Ljava/lang/Object;")
    @Definition(id = "ID", field = "Lcom/tacz/guns/resource/modifier/custom/DamageModifier;ID:Ljava/lang/String;")
    @Expression("this.damageAmount = @((LinkedList) ?.getCache(ID))")
    @ModifyExpressionValue(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;ZLcom/tacz/guns/resource/pojo/data/gun/GunData;Lcom/tacz/guns/resource/pojo/data/gun/BulletData;)V", at = @At("MIXINEXTRAS:EXPRESSION"))
    private LinkedList<ExtraDamage.DistanceDamagePair> tacztweaks$init$attribute$stats$damage(
        LinkedList<ExtraDamage.DistanceDamagePair> original,
        @Local(argsOnly = true) LivingEntity throwerIn
    ) {
        AttributeInstance attribute = throwerIn.getAttribute(DeferredHolderExt.valueOrDelegate(ModAttributes.DAMAGE));
        if (attribute == null || attribute.getModifiers().isEmpty()) return original;
        double originalBaseValue = attribute.getBaseValue();
        try {
            return original.stream()
                .map(pair -> {
                    attribute.setBaseValue(pair.getDamage());
                    float modifiedDamage = (float) attribute.getValue();
                    return new ExtraDamage.DistanceDamagePair(pair.getDistance(), modifiedDamage);
                }).collect(Collectors.toCollection(LinkedList::new));
        } finally {
            attribute.setBaseValue(originalBaseValue);
        }
    }
}