package me.muksc.tacztweaks.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import me.muksc.tacztweaks.TaCZTweaks;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

@OnlyIn(Dist.CLIENT)
public class ReduceSensitivityKey {
    public static KeyMapping KEY = new KeyMapping(
        TaCZTweaks.translatable("key.reduceSensitivity").getString(),
        KeyConflictContext.IN_GAME,
        KeyModifier.NONE,
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        "key.category.tacz"
    );
}
