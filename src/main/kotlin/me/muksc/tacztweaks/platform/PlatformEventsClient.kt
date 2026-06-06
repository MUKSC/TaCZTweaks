package me.muksc.tacztweaks.platform

interface PlatformEventsClient {
    fun onClientLoggingOut(callback: () -> Unit)

    fun onEndClientTick(callback: () -> Unit)
}