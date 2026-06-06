package me.muksc.tacztweaks.mixin.feature.datapack;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.resource.pojo.data.gun.BulletData;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import com.tacz.guns.util.TacHitResult;
import me.muksc.tacztweaks.mixininterface.feature.datapack.TaCZTweaksBullet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.List;

@Mixin(value = EntityKineticBullet.class, remap = false)
public abstract class EntityKineticBulletMixin implements TaCZTweaksBullet {
    @Unique
    private ItemStack tacztweaks$gunStack = ItemStack.EMPTY;

    @Unique
    private int tacztweaks$burstIndex = 0;

    @Unique
    private int tacztweaks$pelletIndex = 0;

    @Unique
    private int tacztweaks$blockPierce = 0;

    @Unique
    private int tacztweaks$entityPierce = 0;

    @Unique
    private final List<DamageModifier> tacztweaks$damageModifiers = new LinkedList<>();

    @Unique
    private @Nullable DamageModifier tacztweaks$entityHitDamageModifier = null;

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;ZLcom/tacz/guns/resource/pojo/data/gun/GunData;Lcom/tacz/guns/resource/pojo/data/gun/BulletData;)V", at = @At("TAIL"))
    private void tacztweaks$init(EntityType<? extends Projectile> type, Level worldIn, LivingEntity throwerIn, ItemStack gunItem, ResourceLocation ammoId, ResourceLocation gunId, ResourceLocation gunDisplayId, boolean isTracerAmmo, GunData gunData, BulletData bulletData, CallbackInfo ci) {
        tacztweaks$gunStack = gunItem;
    }

    @Override
    public ItemStack tacztweaks$getGunStack() {
        return tacztweaks$gunStack;
    }

    @Override
    public int tacztweaks$getBurstIndex() {
        return tacztweaks$burstIndex;
    }

    @Override
    public void tacztweaks$setBurstIndex(int index) {
        tacztweaks$burstIndex = index;
    }

    @Override
    public int tacztweaks$getPelletIndex() {
        return tacztweaks$pelletIndex;
    }

    @Override
    public void tacztweaks$setPelletIndex(int index) {
        tacztweaks$pelletIndex = index;
    }

    @Override
    public int tacztweaks$getBlockPierce() {
        return tacztweaks$blockPierce;
    }

    @Override
    public void tacztweaks$incrementBlockPierce() {
        tacztweaks$blockPierce += 1;
    }

    @Override
    public int tacztweaks$getEntityPierce() {
        return tacztweaks$entityPierce;
    }

    @Override
    public void tacztweaks$incrementEntityPierce() {
        tacztweaks$entityPierce += 1;
    }

    @Override
    public void tacztweaks$modifyDamage(float flat, float multiplier) {
        tacztweaks$damageModifiers.add(new DamageModifier(flat, multiplier));
    }

    @ModifyReturnValue(method = "getDamage", at = @At("RETURN"))
    private float tacztweaks$getDamage$modifyDamage(float original) {
        float damage = original;
        for (DamageModifier modifier : tacztweaks$damageModifiers) {
            damage = (damage + modifier.flat()) * modifier.multiplier();
        }
        return damage;
    }

    @Override
    public void tacztweaks$modifyEntityHitDamage(float flat, float multiplier) {
        tacztweaks$entityHitDamageModifier = new DamageModifier(flat, multiplier);
    }

    @ModifyExpressionValue(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/entity/EntityKineticBullet;getDamage(Lnet/minecraft/world/phys/Vec3;)F"))
    private float tacztweaks$onHitEntity$modifyEntityHitDamage(float original) {
        if (tacztweaks$entityHitDamageModifier == null) return original;
        return (original + tacztweaks$entityHitDamageModifier.flat()) * tacztweaks$entityHitDamageModifier.multiplier();
    }

    @WrapMethod(method = "onHitEntity")
    private void tacztweaks$onHitEntity$modifyEntityHitDamage$reset(TacHitResult result, Vec3 startVec, Vec3 endVec, Operation<Void> original) {
        try {
            original.call(result, startVec, endVec);
        } finally {
            tacztweaks$entityHitDamageModifier = null;
        }
    }
}