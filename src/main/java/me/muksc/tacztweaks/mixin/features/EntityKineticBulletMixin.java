package me.muksc.tacztweaks.mixin.features;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.resource.pojo.data.gun.BulletData;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import me.muksc.tacztweaks.mixininterface.features.EntityKineticBulletExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.List;

@Mixin(value = EntityKineticBullet.class, remap = false)
public abstract class EntityKineticBulletMixin implements EntityKineticBulletExtension {
    @Unique
    private ItemStack tacztweaks$gunStack = null;

    @Unique
    private int tacztweaks$blockPierce = 0;

    @Unique
    private int tacztweaks$entityPierce = 0;

    @Unique
    private final List<DamageModifier> tacztweaks$damageModifiers = new LinkedList<>();

    @Unique
    private Vec3 tacztweaks$position = Vec3.ZERO;

    @Override
    public ItemStack tacztweaks$getGunStack() {
        return tacztweaks$gunStack;
    }

    @Override
    public int tacztweaks$getBlockPierce() {
        return tacztweaks$blockPierce;
    }

    @Override
    public void tacztweaks$incrementBlockPierce() {
        tacztweaks$blockPierce++;
    }

    @Override
    public int tacztweaks$getEntityPierce() {
        return tacztweaks$entityPierce;
    }

    @Override
    public void tacztweaks$incrementEntityPierce() {
        tacztweaks$entityPierce++;
    }

    @Override
    public Vec3 tacztweaks$getPosition() {
        return tacztweaks$position;
    }

    @Override
    public void tacztweaks$setPosition(Vec3 position) {
        tacztweaks$position = position;
    }

    @Override
    public void tacztweaks$addDamageModifier(float flat, float multiplier) {
        tacztweaks$damageModifiers.add(new DamageModifier(flat, multiplier));
    }

    @Override
    public void tacztweaks$popDamageModifier() {
        tacztweaks$damageModifiers.remove(tacztweaks$damageModifiers.size() - 1);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;ZLcom/tacz/guns/resource/pojo/data/gun/GunData;Lcom/tacz/guns/resource/pojo/data/gun/BulletData;)V", at = @At("RETURN"))
    private void tacztweaks$init(EntityType<? extends Projectile> type, Level worldIn, LivingEntity throwerIn, ItemStack gunItem, ResourceLocation ammoId, ResourceLocation gunId, ResourceLocation gunDisplayId, boolean isTracerAmmo, GunData gunData, BulletData bulletData, CallbackInfo ci) {
        tacztweaks$gunStack = gunItem;
    }

    @ModifyExpressionValue(method = "getDamage", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/pojo/data/gun/ExtraDamage$DistanceDamagePair;getDamage()F"))
    private float tacztweaks$getDamage$applyDamageModifiers(float original, @Local double playerDistance) {
        float damage = original;
        for (DamageModifier modifier : tacztweaks$damageModifiers) {
            damage = (damage + modifier.flat()) * modifier.multiplier();
        }
        return damage;
    }

    @Inject(method = "onBulletTick", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/util/block/BlockRayTrace;rayTraceBlocks(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;", shift = At.Shift.AFTER), cancellable = true)
    private void tacztweaks$onBulletTick$finishRayTracing(CallbackInfo ci) {
        ci.cancel();
    }
}
