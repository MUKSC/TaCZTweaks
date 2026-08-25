package me.muksc.tacztweaks.mixin.feature.general.compatibility.sable;

//? if 1.21.1 {
/*import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.muksc.tacztweaks.mixininterface.feature.raytracer.IgnoringClipContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(value = BlockGetter.class, priority = 1500, remap = false)
public interface BlockGetterMixinMixin {
    @TargetHandler(
        mixin = "dev.ryanhcode.sable.mixin.clip_overwrite.BlockGetterMixin",
        name = "clip"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At("HEAD"))
    private void tacztweaks$clip$storeIgnores(
        ClipContext clipContext, CallbackInfoReturnable<BlockHitResult> cir,
        @Share("ignores") LocalRef<Set<BlockPos>> ignoresRef
    ) {
        ignoresRef.set(IgnoringClipContext.of(clipContext).tacztweaks$getIgnores());
    }

    @TargetHandler(
        mixin = "dev.ryanhcode.sable.mixin.clip_overwrite.BlockGetterMixin",
        name = "clip"
    )
    @ModifyExpressionValue(method = "@MixinSquared:Handler", at = @At(value = "NEW", target = "(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/ClipContext$Block;Lnet/minecraft/world/level/ClipContext$Fluid;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/level/ClipContext;"))
    private ClipContext tacztweaks$clip$copyIgnores(
        ClipContext original,
        @Share("ignores") LocalRef<Set<BlockPos>> ignoresRef
    ) {
        IgnoringClipContext.of(original).tacztweaks$setIgnores(ignoresRef.get());
        return original;
    }
}
*///?} else {
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Minecraft.class)
public abstract class BlockGetterMixinMixin { }
//?}