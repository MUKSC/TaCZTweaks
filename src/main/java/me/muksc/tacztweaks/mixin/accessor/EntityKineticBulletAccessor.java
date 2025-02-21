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
    @Accessor
    int getPierce();

    @Accessor
    void setPierce(int pierce);

    @Accessor
    boolean getExplosion();

    @Invoker
    void invokeOnHitEntity(TacHitResult result, Vec3 startVec, Vec3 endVec);

    @Invoker
    void invokeOnHitBlock(BlockHitResult result, Vec3 startVec, Vec3 endVec);
}
