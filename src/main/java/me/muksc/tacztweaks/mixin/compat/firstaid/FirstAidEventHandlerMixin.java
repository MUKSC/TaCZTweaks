package me.muksc.tacztweaks.mixin.compat.firstaid;

import com.tacz.guns.entity.EntityKineticBullet;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.distribution.IDamageDistributionAlgorithm;
import ichttt.mods.firstaid.common.EventHandler;
import me.muksc.tacztweaks.compat.firstaid.TacZDamageDistribution;
import me.muksc.tacztweaks.config.Config;
import me.muksc.tacztweaks.mixininterface.features.EntityKineticBulletExtension;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts FirstAid's damage handling to provide precise bodypart targeting for TacZ weapons.
 * This replaces FirstAid's default random/height-based distribution with our 3D hit detection.
 */
@Mixin(value = EventHandler.class, remap = false)
public class FirstAidEventHandlerMixin {

    private static final ThreadLocal<IDamageDistributionAlgorithm> tacztweaks$customDistribution = new ThreadLocal<>();

    /**
     * Intercepts damage events to detect TacZ bullets.
     * Sets up custom distribution algorithms before FirstAid processes the damage.
     */
    @Inject(
        method = "onLivingHurt",
        at = @At(
            value = "INVOKE",
            target = "Lichttt/mods/firstaid/common/util/CommonUtils;getDamageModel(Lnet/minecraft/world/entity/player/Player;)Lichttt/mods/firstaid/api/damagesystem/AbstractPlayerDamageModel;",
            shift = At.Shift.AFTER
        )
    )
    private static void tacztweaks$detectTacZDamage(LivingHurtEvent event, CallbackInfo ci) {
        if (!Config.Compat.INSTANCE.firstAidCompat()) return;
        if (!(event.getEntity() instanceof Player)) return;

        DamageSource source = event.getSource();
        Entity directEntity = source.getDirectEntity();

        // Check if damage is from a TacZ bullet
        if (directEntity instanceof EntityKineticBullet) {
            Vec3 hitLocation = ((EntityKineticBulletExtension) directEntity).tacztweaks$getLastHitLocation();

            if (hitLocation != null) {
                // Store custom distribution for this bullet
                tacztweaks$customDistribution.set(new TacZDamageDistribution(hitLocation));
                return;
            }
        }

        // Clear any previous custom distribution
        tacztweaks$customDistribution.remove();
    }

    /**
     * Redirects the handleDamageTaken call to use our custom distribution if available.
     */
    @Redirect(
        method = "onLivingHurt",
        at = @At(
            value = "INVOKE",
            target = "Lichttt/mods/firstaid/common/damagesystem/distribution/DamageDistribution;handleDamageTaken(Lichttt/mods/firstaid/api/distribution/IDamageDistributionAlgorithm;Lichttt/mods/firstaid/api/damagesystem/AbstractPlayerDamageModel;FLnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/damagesource/DamageSource;ZZ)F"
        )
    )
    private static float tacztweaks$replaceDistribution(
            IDamageDistributionAlgorithm original,
            AbstractPlayerDamageModel damageModel,
            float damage,
            Player player,
            DamageSource source,
            boolean addStat,
            boolean redistributeIfLeft
    ) {
        try {
            IDamageDistributionAlgorithm custom = tacztweaks$customDistribution.get();
            if (custom != null) {
                // Call handleDamageTaken with our custom distribution
                return ichttt.mods.firstaid.common.damagesystem.distribution.DamageDistribution.handleDamageTaken(
                    custom, damageModel, damage, player, source, addStat, redistributeIfLeft
                );
            }

            // Use the original distribution
            return ichttt.mods.firstaid.common.damagesystem.distribution.DamageDistribution.handleDamageTaken(
                original, damageModel, damage, player, source, addStat, redistributeIfLeft
            );
        } finally {
            tacztweaks$customDistribution.remove();
        }
    }
}
