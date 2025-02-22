package me.muksc.tacztweaks.network.message;

import com.tacz.guns.api.item.IGun;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import me.muksc.tacztweaks.Config;
import me.muksc.tacztweaks.ShooterDataHolderProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientMessagePlayerUnload {
    public ClientMessagePlayerUnload() { /* Nothing */ }

    public static void encode(ClientMessagePlayerUnload message, FriendlyByteBuf buf) { /* Nothing */ }

    public static ClientMessagePlayerUnload decode(FriendlyByteBuf buf) {
        return new ClientMessagePlayerUnload();
    }

    public static void handle(ClientMessagePlayerUnload message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (!context.getDirection().getReceptionSide().isServer() || !Config.Gun.INSTANCE.allowUnload()) {
            context.setPacketHandled(true);
            return;
        }

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || player.isCreative()) return;
            ShooterDataHolder data = ((ShooterDataHolderProvider) player).tacztweaks$getShooterDataHolder();
            if (data.currentGunItem == null) return;
            ItemStack gunStack = data.currentGunItem.get();
            IGun gun = IGun.getIGunOrNull(gunStack);
            if (gun == null) return;
            gun.dropAllAmmo(player, gunStack);
        });
        context.setPacketHandled(true);
    }
}
