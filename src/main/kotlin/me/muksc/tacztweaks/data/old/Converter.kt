package me.muksc.tacztweaks.data.old

import me.muksc.tacztweaks.data.BulletInteraction
import me.muksc.tacztweaks.data.Target
import me.muksc.tacztweaks.data.ValueRange
import java.util.*
import me.muksc.tacztweaks.data.old.BulletInteraction as OldBulletInteraction

fun OldBulletInteraction.convert() = BulletInteraction.Block(
    target = if (guns.isEmpty()) emptyList() else listOf(Target.Gun(guns)),
    blocks = blocks,
    blockBreak = when (blockBreak) {
        is OldBulletInteraction.BlockBreak.Never -> BulletInteraction.Block.BlockBreak.Never
        is OldBulletInteraction.BlockBreak.Count -> BulletInteraction.Block.BlockBreak.Count(
            count = blockBreak.count,
            hardness = ValueRange.DEFAULT,
            tier = Optional.empty(),
            drop = drop
        )
        is OldBulletInteraction.BlockBreak.FixedDamage -> BulletInteraction.Block.BlockBreak.FixedDamage(
            damage = blockBreak.damage,
            accumulate = blockBreak.accumulate,
            hardness = ValueRange.DEFAULT,
            tier = Optional.empty(),
            drop = drop
        )
        is OldBulletInteraction.BlockBreak.DynamicDamage -> BulletInteraction.Block.BlockBreak.DynamicDamage(
            modifier = blockBreak.modifier,
            multiplier = blockBreak.multiplier,
            accumulate = blockBreak.accumulate,
            hardness = ValueRange.DEFAULT,
            tier = Optional.empty(),
            drop = drop
        )
    },
    pierce = when (pierce) {
        is OldBulletInteraction.Pierce.Never -> BulletInteraction.Pierce.Never
        is OldBulletInteraction.Pierce.Count -> BulletInteraction.Pierce.Count(
            count = pierce.count,
            conditional = pierce.condition == OldBulletInteraction.Pierce.ECondition.ON_BREAK,
            damageFalloff = pierce.damageFalloff,
            damageMultiplier = pierce.damageMultiplier
        )
        is OldBulletInteraction.Pierce.Damage -> BulletInteraction.Pierce.Damage(
            conditional = pierce.condition == OldBulletInteraction.Pierce.ECondition.ON_BREAK,
            damageFalloff = pierce.damageFalloff,
            damageMultiplier = pierce.damageMultiplier
        )
    },
    gunPierce = BulletInteraction.GunPierce(
        required = when (pierce) {
            is OldBulletInteraction.Pierce.Never -> false
            is OldBulletInteraction.Pierce.Count -> pierce.requireGunPierce
            is OldBulletInteraction.Pierce.Damage -> pierce.requireGunPierce
        },
        consume = false
    ),
    priority = 0
)