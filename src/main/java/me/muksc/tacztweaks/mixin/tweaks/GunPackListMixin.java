package me.muksc.tacztweaks.mixin.tweaks;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.tacz.guns.client.gui.components.GunPackList;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GunPackList.class, remap = false)
public abstract class GunPackListMixin {
    @WrapWithCondition(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/client/gui/components/GunPackList;addEntry(Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;)I", ordinal = 1, remap = true))
    private <E extends AbstractSelectionList.Entry<E>> boolean tacztweaks$init$hideByHandFilter(GunPackList instance, AbstractSelectionList.Entry<E> entry) {
        return !Config.Tweaks.INSTANCE.alwaysFilterByHand();
    }

    @ModifyReturnValue(method = "isByHandSelected", at = @At("RETURN"))
    private boolean tacztweaks$isByHandSelected$alwaysActive(boolean original) {
        if (!Config.Tweaks.INSTANCE.alwaysFilterByHand()) return original;
        return true;
    }
}
