package me.muksc.tacztweaks.mixin.compat.mts;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.util.EntityUtil;
import me.muksc.tacztweaks.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(value = EntityUtil.class, remap = false)
public abstract class EntityUtilMixin {
    @Unique
    private static final ResourceLocation tacztweaks$mts = new ResourceLocation("mts", "builder_existing");

    @ModifyExpressionValue(method = "getHitResult", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/util/HitboxHelper;getFixedBoundingBox(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/AABB;"))
    private static AABB tacztweaks$getHitResult$mtsFix(AABB original, @Local(argsOnly = true) Entity entity) {
        if (!Config.Compat.INSTANCE.mtsFix()) return original;
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (!Objects.equals(id, tacztweaks$mts)) return original;
        return entity.getBoundingBox().inflate(0.3);
    }
}
