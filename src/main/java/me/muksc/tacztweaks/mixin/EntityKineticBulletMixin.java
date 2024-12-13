package me.muksc.tacztweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.resource.pojo.data.gun.BulletData;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import me.muksc.tacztweaks.Context;
import me.muksc.tacztweaks.EntityKineticBulletExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityKineticBullet.class, remap = false)
public abstract class EntityKineticBulletMixin implements EntityKineticBulletExtension {
    @Unique
    private int tacztweaks$blockPierce = 0;

    @Unique
    private float tacztweaks$flatDamageModifier = 0.0F;

    @Unique
    private float tacztweaks$damageMultiplier = 1.0F;

    @Override
    public int tacztweaks$getBlockPierce() {
        return tacztweaks$blockPierce;
    }

    @Override
    public void tacztweaks$setBlockPierce(int blockPierce) {
        tacztweaks$blockPierce = blockPierce;
    }

    @Override
    public float tacztweaks$getFlatDamageModifier() {
        return tacztweaks$flatDamageModifier;
    }

    @Override
    public void tacztweaks$setFlatDamageModifier(float flatDamageModifier) {
        tacztweaks$flatDamageModifier = flatDamageModifier;
    }

    @Override
    public float tacztweaks$getDamageMultiplier() {
        return tacztweaks$damageMultiplier;
    }

    @Override
    public void tacztweaks$setDamageMultiplier(float damageMultiplier) {
        tacztweaks$damageMultiplier = damageMultiplier;
    }

    @ModifyExpressionValue(method = "getDamage", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/ExtraDamage$DistanceDamagePair;getDamage()F"))
    private float applyDamageModifier(float original) {
        return (original + tacztweaks$flatDamageModifier) * tacztweaks$damageMultiplier;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;ZLcom/tacz/guns/resource/pojo/data/gun/GunData;Lcom/tacz/guns/resource/pojo/data/gun/BulletData;)V", at = @At("RETURN"))
    private void setGunStack(EntityType<EntityKineticBullet> type, Level worldIn, LivingEntity throwerIn, ItemStack gunItem, ResourceLocation ammoId, ResourceLocation gunId, boolean isTracerAmmo, GunData gunData, BulletData bulletData, CallbackInfo ci) {
        Context.Gun.INSTANCE.setStack(gunItem);
    }

    @Inject(method = "onBulletTick", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/util/block/BlockRayTrace;rayTraceBlocks(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;"))
    private void setInstance(CallbackInfo ci) {
        Context.ammo = (EntityKineticBullet)(Object) this;
    }
}
