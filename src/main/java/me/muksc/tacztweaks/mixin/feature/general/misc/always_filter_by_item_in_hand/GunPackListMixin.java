package me.muksc.tacztweaks.mixin.feature.general.misc.always_filter_by_item_in_hand;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.client.gui.components.GunPackList;
import me.muksc.tacztweaks.config.Config;
import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GunPackList.class, remap = false)
public abstract class GunPackListMixin {
    @Definition(id = "addEntry", method = "Lcom/tacz/guns/client/gui/components/GunPackList;addEntry(Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;)I")
    @Definition(id = "Entry", type = GunPackList.Entry.class)
    @Definition(id = "byHandCheckbox", field = "Lcom/tacz/guns/client/gui/components/GunPackList;byHandCheckbox:Lcom/tacz/guns/client/gui/components/GunPackList$Checkbox;")
    @Expression("this.addEntry(new Entry(this.byHandCheckbox))")
    @WrapOperation(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int tacztweaks$init$alwaysFilterByItemInHand$skipEntry(GunPackList instance, AbstractSelectionList.Entry<?> entry, Operation<Integer> original) {
        return Config.General.Miscellaneous.alwaysFilterByItemInHand() ? 0 : original.call(instance, entry);
    }

    @ModifyReturnValue(method = "isByHandSelected", at = @At("RETURN"))
    private boolean tacztweaks$isByHandSelected$alwaysFilterByItemInHand(boolean original) {
        return Config.General.Miscellaneous.alwaysFilterByItemInHand() || original;
    }
}