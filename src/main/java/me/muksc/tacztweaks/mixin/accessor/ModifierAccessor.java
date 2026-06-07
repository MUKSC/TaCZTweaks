package me.muksc.tacztweaks.mixin.accessor;

import com.tacz.guns.resource.pojo.data.attachment.Modifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Modifier.class, remap = false)
public interface ModifierAccessor {
    @Accessor("addend")
    void tacztweaks$setAddend(double addend);

    @Accessor("multiplier")
    void tacztweaks$setMultiplier(double multiplier);

    @Accessor("function")
    void tacztweaks$setFunction(String function);
}