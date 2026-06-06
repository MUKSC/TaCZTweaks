package me.muksc.tacztweaks.core.codec

//? if >=1.20.5 {
/*import net.minecraft.network.codec.ByteBufCodecs

typealias ByteBufCodecs = ByteBufCodecs
*///?} else {
import io.netty.buffer.ByteBuf
import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.EncoderException
import net.minecraft.network.FriendlyByteBuf
import java.util.function.IntFunction
import java.util.function.ToIntFunction

object ByteBufCodecs {
    @JvmField val BOOL = StreamCodec.of(ByteBuf::writeBoolean, ByteBuf::readBoolean)
    //@JvmField val BYTE = StreamCodec.of()
    //@JvmField val SHORT = StreamCodec.of()
    //@JvmField val UNSIGNED_SHORT = StreamCodec.of()
    @JvmField val INT = StreamCodec.of(ByteBuf::writeInt, ByteBuf::readInt)
    //@JvmField val VAR_INT = StreamCodec.of()
    //@JvmField val VAR_LONG = StreamCodec.of()
    @JvmField val FLOAT = StreamCodec.of(ByteBuf::writeFloat, ByteBuf::readFloat)
    @JvmField val DOUBLE = StreamCodec.of(ByteBuf::writeDouble, ByteBuf::readDouble)
    //@JvmField val BYTE_ARRAY = StreamCodec.of()
    @JvmField val STRING_UTF8 = StreamCodec.of<ByteBuf, String>(
        { buf, value ->  FriendlyByteBuf(buf).writeUtf(value, 32767) },
        { buf -> FriendlyByteBuf(buf).readUtf(32767) }
    )
    //@JvmField val TAG = StreamCodec.of()
    //@JvmField val TRUSTED_TAG = StreamCodec.of()
    //@JvmField val COMPOUND_TAG = StreamCodec.of()
    //@JvmField val TRUSTED_COMPOUND_TAG = StreamCodec.of()
    //@JvmField val OPTIONAL_COMPOUND_TAG = StreamCodec.of()
    //@JvmField val VECTOR3F = StreamCodec.of()
    //@JvmField val QUATERNIONF = StreamCodec.of()
    //@JvmField val GAME_PROFILE_PROPERTIES = StreamCodec.of()
    //@JvmField val GAME_PROFILE = StreamCodec.of()

    @JvmStatic
    fun <B : ByteBuf, V, C : Collection<V>> collection(factory: IntFunction<C>, codec: StreamCodec<in ByteBuf, V>): StreamCodec<B, C> =
        collection(factory, codec, Int.MAX_VALUE)

    @JvmStatic
    fun <B : ByteBuf, V, C : Collection<V>> collection(factory: IntFunction<C>, codec: StreamCodec<in ByteBuf, V>, maxSize: Int): StreamCodec<B, C> =
        StreamCodec.of({ buf, value ->
            if (value.size > maxSize) throw EncoderException("${value.size} elements exceeded max size of: $maxSize")
            FriendlyByteBuf(buf).writeCollection(value, codec::encode)
       }, { buf ->
           FriendlyByteBuf(buf).readCollection(factory, codec::decode).apply {
               if (size > maxSize) throw DecoderException("$maxSize elements exceeded max size of: $maxSize")
           }
       })

    @JvmStatic
    fun <B : ByteBuf, V, C : Collection<V>> collection(factory: IntFunction<C>): StreamCodec.CodecOperation<in ByteBuf, V, C> =
        StreamCodec.CodecOperation { codec -> collection(factory, codec) }

    @JvmStatic
    fun <B : ByteBuf, V> list(): StreamCodec.CodecOperation<in ByteBuf, V, List<V>> =
        StreamCodec.CodecOperation { codec -> collection(::ArrayList, codec) }

    @JvmStatic
    fun <B : ByteBuf, V> list(maxSize: Int): StreamCodec.CodecOperation<in ByteBuf, V, List<V>> =
        StreamCodec.CodecOperation { codec -> collection(::ArrayList, codec, maxSize) }

    @JvmStatic
    fun <T> idMapper(idLookup: IntFunction<T>, idGetter: ToIntFunction<T>): StreamCodec<ByteBuf, T> =
        StreamCodec.of({ buf, value ->
            val id = idGetter.applyAsInt(value)
            FriendlyByteBuf(buf).writeVarInt(id)
        }, { buf ->
            val id = FriendlyByteBuf(buf).readVarInt()
            idLookup.apply(id)
        })
}
//?}