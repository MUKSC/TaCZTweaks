package me.muksc.tacztweaks.core.codec

//? if >=1.20.5 {
/*import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamDecoder
import net.minecraft.network.codec.StreamEncoder
import java.util.function.IntFunction

fun <B : RegistryFriendlyByteBuf, T : Any, C : MutableCollection<T>> B.readCollection(
    collectionFactory: IntFunction<C>,
    elementReader: StreamDecoder<B, T>
): C {
    val size = readVarInt()
    val collection = collectionFactory.apply(size)
    repeat(size) {
        collection.add(elementReader.decode(this))
    }
    return collection
}

fun <B : RegistryFriendlyByteBuf, T : Any> B.writeCollection(
    collection: Collection<T>,
    elementWriter: StreamEncoder<B, T>
) {
    writeVarInt(collection.size)
    for (element in collection) {
        elementWriter.encode(this, element)
    }
}
*///?}