package me.muksc.tacztweaks.mixin.feature.general.compatibility.sable.lrtactical;

//? if 1.21.1 && neoforge {
/*import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.muksc.tacztweaks.config.Config;
import me.xjqsh.lrtactical.util.CustomExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mixin(value = CustomExplosion.class, remap = false)
public abstract class CustomExplosionMixin extends Explosion {
    public CustomExplosionMixin(Level level, @Nullable Entity source, double x, double y, double z, float radius, List<BlockPos> toBlow, BlockInteraction blockInteraction, ParticleOptions smallExplosionParticles, ParticleOptions largeExplosionParticles, Holder<SoundEvent> explosionSound) {
        super(level, source, x, y, z, radius, toBlow, blockInteraction, smallExplosionParticles, largeExplosionParticles, explosionSound);
    }

    @Shadow @Final private Level level;
    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;
    @Shadow @Final private Entity source;
    @Shadow @Final private ExplosionDamageCalculator damageCalculator;

    @Inject(method = "explode", at = @At("HEAD"), remap = true)
    private void tacztweaks$explode$sableCompat$preExplode(
        final CallbackInfo ci,
        @Share("explodedSet") final LocalRef<Set<BlockPos>> explodedSet
    ) {
        explodedSet.set(new ObjectOpenHashSet<>());
    }

    @Inject(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ExplosionDamageCalculator;getBlockExplosionResistance(Lnet/minecraft/world/level/Explosion;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)Ljava/util/Optional;"), remap = true)
    private void tacztweaks$explode$sableCompat$redirectBlockExplosionResistance(
        CallbackInfo ci,
        @Local(name = "set") Set<BlockPos> set,
        @Local(name = "blockpos") BlockPos pos,
        @Local(name = "blockstate") BlockState blockState,
        @Local(name = "f") LocalFloatRef fRef,
        @Share("explodedSet") final LocalRef<Set<BlockPos>> explodedSet
    ) {
        if (!Config.General.Compatibility.sableCompat()) return;
        // https://github.com/ryanhcode/sable/blob/mc1.21.1-1.2.2-neoforge/common/src/main/java/dev/ryanhcode/sable/mixin/explosion/ExplosionMixin.java
        if (!blockState.isAir()) return;

        float f = fRef.get();
        final BoundingBox3d globalBounds = new BoundingBox3d(pos);
        final Iterable<SubLevel> subLevels = Sable.HELPER.getAllIntersecting(this.level, globalBounds);
        final SubLevelContainer container = SubLevelContainer.getContainer(this.level);

        for (final SubLevel subLevel : subLevels) {
            final Pose3d pose = subLevel.logicalPose();

            final BoundingBox3d localBounds = new BoundingBox3d();
            globalBounds.transformInverse(pose, localBounds);

            final BoundingBox3i blockBounds = new BoundingBox3i(
                Mth.floor(localBounds.minX()),
                Mth.floor(localBounds.minY()),
                Mth.floor(localBounds.minZ()),
                Mth.floor(localBounds.maxX()),
                Mth.floor(localBounds.maxY()),
                Mth.floor(localBounds.maxZ())
            );

            final Vec3 localExplosionPosition = pose.transformPositionInverse(new Vec3(this.x, this.y, this.z));

            for (int x = blockBounds.minX(); x <= blockBounds.maxX(); x++) {
                for (int z = blockBounds.minZ(); z <= blockBounds.maxZ(); z++) {
                    for (int y = blockBounds.minY(); y <= blockBounds.maxY(); y++) {
                        pos = new BlockPos(x, y, z);
                        blockState = this.level.getBlockState(pos);
                        FluidState fluidState = this.level.getFluidState(pos);

                        final boolean canExplodeBefore = f > 0.0;

                        final Optional<Float> optional = this.damageCalculator.getBlockExplosionResistance(this, this.level, pos, blockState, fluidState);
                        if (optional.isPresent()) {
                            f -= (optional.get() + 0.3F) * 0.3F;
                        }

                        if (f > 0.0F && this.damageCalculator.shouldBlockExplode(this, this.level, pos, blockState, f)) {
                            set.add(pos);
                        }

                        final boolean wind = this.source instanceof WindCharge && !blockState.isAir();
                        if (canExplodeBefore && (f < 0.0f || wind) && explodedSet.get().add(pos)) {
                            explodedSet.get().add(pos);

                            if (subLevel instanceof final ServerSubLevel serverSubLevel) {
                                final SubLevelPhysicsSystem physicsSystem = ((ServerSubLevelContainer) container).physicsSystem();
                                final RigidBodyHandle handle = physicsSystem.getPhysicsHandle(serverSubLevel);

                                final Vec3 centerPos = pos.getCenter();
                                final Vec3 force = centerPos.subtract(localExplosionPosition).normalize().scale(5.0);
                                handle.applyImpulseAtPoint(centerPos, force);
                            }
                        }
                    }
                }
            }
        }

        fRef.set(f);
    }
}
*///?} else {
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Minecraft.class)
public abstract class CustomExplosionMixin { }
//?}