package me.muksc.tacztweaks;

import com.tacz.guns.entity.EntityKineticBullet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(TaCZTweaks.MOD_ID)
public class TaCZTweaks {
    public static final String MOD_ID = "tacztweaks";

    public static EntityKineticBullet ammoInstance;

    public static TagKey<Block> BULLET_BREAK_BLOCKS;
    public static TagKey<Block> BULLET_BREAK_WITH_DROP_BLOCKS;
    
    public ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
    
    public TaCZTweaks() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        BULLET_BREAK_BLOCKS = BlockTags.create(id("bullet_break"));
        BULLET_BREAK_WITH_DROP_BLOCKS = BlockTags.create(id("bullet_break_with_drop"));
    }
}
