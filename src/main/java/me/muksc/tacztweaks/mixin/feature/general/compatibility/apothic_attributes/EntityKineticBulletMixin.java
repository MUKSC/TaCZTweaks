package me.muksc.tacztweaks.mixin.feature.general.compatibility.apothic_attributes;

//? if forge || neoforge {
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.entity.EntityKineticBullet;
import me.muksc.tacztweaks.config.Config;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EntityKineticBullet.class, remap = false)
public abstract class EntityKineticBulletMixin {
    @ModifyExpressionValue(method = "tacAttackEntity", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lcom/tacz/guns/entity/EntityKineticBullet;armorIgnore:F"))
    private float tacztweaks$tacAttackEntity$disableArmorIgnore(float original) {
        if (!Config.General.Compatibility.apothicAttributesCompat()) return original;
        return 0.0F;
    }
}
//?} else {
/*import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Minecraft.class)
public abstract class EntityKineticBulletMixin { }
*///?}