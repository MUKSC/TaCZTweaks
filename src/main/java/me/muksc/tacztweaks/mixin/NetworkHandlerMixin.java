package me.muksc.tacztweaks.mixin;

import com.tacz.guns.network.NetworkHandler;
import me.muksc.tacztweaks.network.message.ClientMessagePlayerUnload;
import net.minecraftforge.network.simple.SimpleChannel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(value = NetworkHandler.class, remap = false)
public abstract class NetworkHandlerMixin {
    @Shadow @Final public static SimpleChannel CHANNEL;
    @Shadow @Final private static AtomicInteger ID_COUNT;

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/network/NetworkHandler;registerAcknowledge()V"))
    private static void init(CallbackInfo ci) {
        CHANNEL.registerMessage(ID_COUNT.getAndIncrement(), ClientMessagePlayerUnload.class, ClientMessagePlayerUnload::encode, ClientMessagePlayerUnload::decode, ClientMessagePlayerUnload::handle);
    }
}
