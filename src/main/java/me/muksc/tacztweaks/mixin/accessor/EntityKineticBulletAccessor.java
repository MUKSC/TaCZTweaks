package me.muksc.tacztweaks.mixin.accessor;

import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.util.TacHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = EntityKineticBullet.class, remap = false)
public interface EntityKineticBulletAccessor {
    @Accessor("pierce")
    int tacztweaks$getPierce();

    @Accessor("pierce")
    void tacztweaks$setPierce(int pierce);

    @Accessor("explosion")
    boolean tacztweaks$getExplosion();

    @Invoker("onHitEntity")
    void tacztweaks$invokeOnHitEntity(TacHitResult result, Vec3 startVec, Vec3 endVec);

    @Invoker("onHitBlock")
    void tacztweaks$invokeOnHitBlock(BlockHitResult result, Vec3 startVec, Vec3 endVec);

}