package me.muksc.tacztweaks.mixin.accessor;

import com.tacz.guns.resource.pojo.data.gun.InaccuracyType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = InaccuracyType.class, remap = false)
public interface InaccuracyTypeAccessor {
    @Invoker
    static boolean invokeIsMove(LivingEntity livingEntity) {
        throw new AssertionError();
    }
}