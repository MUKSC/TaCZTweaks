package me.muksc.tacztweaks.mixin.accessor;

import com.tacz.guns.resource.pojo.data.attachment.Modifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Modifier.class, remap = false)
public interface ModifierAccessor {
    @Accessor
    void setAddend(double addend);

    @Accessor
    void setMultiplier(double multiplier);

    @Accessor
    void setFunction(String function);
}