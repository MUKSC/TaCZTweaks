package me.muksc.tacztweaks.feature.audio_and_visuals.system

import com.google.common.cache.CacheBuilder
import com.google.common.hash.Hashing
import me.muksc.tacztweaks.TaCZTweaks
import me.muksc.tacztweaks.core.GAME_DIR
import net.minecraft.resources.ResourceLocation
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import javax.sound.sampled.AudioFormat
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.io.path.readBytes
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

object StereoToMonoMixer {
    private val memoryCache = CacheBuilder.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(10.minutes.toJavaDuration())
        .build<ResourceLocation, ByteBuffer>()
    private val CACHE_DIR = GAME_DIR.resolve(".cache/${TaCZTweaks.MOD_ID}")

    fun computeHash(buffer: ByteBuffer): ByteArray =
        Hashing.murmur3_128().hashBytes(buffer.sliceOrdered()).asBytes()

    fun getCachePath(buffer: ByteBuffer): Path {
        val hash = computeHash(buffer)
        return CACHE_DIR.resolve(hash.toHexString())
    }

    fun writeToCache(path: Path, buffer: ByteBuffer) {
        val buffer = buffer.duplicateOrdered()
        FileChannel.open(
            path.createParentDirectories(),
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE
        ).use { channel ->
            while (buffer.hasRemaining()) {
                channel.write(buffer)
            }
        }
    }

    fun readFromCache(path: Path, order: ByteOrder): ByteBuffer {
        val bytes = path.readBytes()
        return ByteBuffer.allocateDirect(bytes.size).apply {
            order(order)
            put(bytes)
            flip()
        }
    }

    fun computeIfAbsent(id: ResourceLocation, buffer: ByteBuffer, block: () -> ByteBuffer): ByteBuffer {
        memoryCache.getIfPresent(id)?.let { return it.duplicateOrdered() }
        val path = getCachePath(buffer)
        if (path.exists()) return readFromCache(path, buffer.order()).also {
            memoryCache.put(id, it.duplicateOrdered())
        }

        return block().also {
            writeToCache(path, it.duplicateOrdered())
            memoryCache.put(id, it.duplicateOrdered())
        }
    }

    @JvmStatic
    fun process(id: ResourceLocation, buffer: ByteBuffer, format: AudioFormat): ByteBuffer? {
        if (format.channels == 1) return buffer
        val buffer = buffer.sliceOrdered()
        return when (format.sampleSizeInBits) {
            16 -> computeIfAbsent(id, buffer) { monoBuffer(buffer) {
                val shortBuffer = buffer.asShortBuffer()
                while (shortBuffer.hasRemaining()) {
                    val left = shortBuffer.get()
                    val right = shortBuffer.get()
                    val mixed = (left + right) / 2
                    putShort(mixed.toShort())
                }
            } }
            8 -> computeIfAbsent(id, buffer) { monoBuffer(buffer) {
                while (buffer.hasRemaining()) {
                    val left = buffer.get().toUByte()
                    val right = buffer.get().toUByte()
                    val mixed = (left + right) / 2U
                    put(mixed.toByte())
                }
            } }
            else -> null
        }
    }

    private fun monoBuffer(buffer: ByteBuffer, block: ByteBuffer.() -> Unit): ByteBuffer =
        ByteBuffer.allocateDirect(buffer.remaining() / 2).apply {
            order(buffer.order())
            block()
            flip()
        }

    private fun ByteBuffer.sliceOrdered(): ByteBuffer = slice().order(order())

    private fun ByteBuffer.duplicateOrdered(): ByteBuffer = duplicate().order(order())
}