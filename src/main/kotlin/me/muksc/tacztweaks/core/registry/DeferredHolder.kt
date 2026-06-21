package me.muksc.tacztweaks.core.registry

//? if neoforge {
/*import net.neoforged.neoforge.registries.DeferredHolder as NeoDeferredHolder

typealias DeferredHolder<R, T> = NeoDeferredHolder<R, T>

fun <R, T : R> NeoDeferredHolder<R, T>.wrap(): DeferredHolder<R, T> = this
*///?} else if forge {
import com.mojang.datafixers.util.Either
import net.minecraft.core.Holder
import net.minecraft.core.HolderOwner
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraftforge.registries.RegistryObject
import java.util.Optional
import java.util.function.Predicate
import java.util.stream.Stream
import kotlin.jvm.optionals.getOrNull

class DeferredHolder<R, T : R>(
    private val base: RegistryObject<T>
) : Holder<R> {
    @Suppress("UNCHECKED_CAST")
    private val obj = base as RegistryObject<R>

    override fun get(): T & Any = base.get()

    override fun value(): T & Any = base.get()

    override fun isBound(): Boolean =
        obj.isPresent

    override fun `is`(id: ResourceLocation): Boolean =
        id == obj.key?.location()

    override fun `is`(key: ResourceKey<R>): Boolean =
        key == obj.key

    override fun `is`(filter: Predicate<ResourceKey<R>?>): Boolean =
        filter.test(obj.key)

    override fun `is`(tag: TagKey<R>): Boolean =
        obj.holder.getOrNull()?.`is`(tag) == true

    override fun tags(): Stream<TagKey<R>> =
        obj.holder.getOrNull()?.tags() ?: Stream.empty()

    override fun unwrap(): Either<ResourceKey<R>, R> =
        Either.left(obj.key)

    override fun unwrapKey(): Optional<ResourceKey<R>> =
        Optional.ofNullable(obj.key)

    override fun kind(): Holder.Kind = Holder.Kind.REFERENCE

    override fun canSerializeIn(owner: HolderOwner<R>): Boolean =
        obj.holder.getOrNull()?.canSerializeIn(owner) == true
}

fun <R, T : R> RegistryObject<T>.wrap(): DeferredHolder<R, T> = DeferredHolder(this)
//?} else if fabric {
/*import com.mojang.datafixers.util.Either
import net.minecraft.core.Holder
import net.minecraft.core.HolderOwner
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import java.util.Optional
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.stream.Stream

class DeferredHolder<R, T : R>(
    private val holder: Holder<R>
) : Holder<R>, Supplier<T> {
    @Suppress("UNCHECKED_CAST")
    override fun get(): T = holder.value() as T

    @Suppress("UNCHECKED_CAST")
    override fun value(): T = holder.value() as T

    override fun isBound(): Boolean = holder.isBound

    override fun `is`(location: ResourceLocation): Boolean = holder.`is`(location)

    override fun `is`(resourceKey: ResourceKey<R>): Boolean = holder.`is`(resourceKey)

    override fun `is`(predicate: Predicate<ResourceKey<R>>): Boolean = holder.`is`(predicate)

    override fun `is`(tagKey: TagKey<R>): Boolean = holder.`is`(tagKey)

    //? if >=1.20.5 {
    /*@Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun `is`(holder: Holder<R>): Boolean = holder.`is`(holder)
    *///?}

    override fun tags(): Stream<TagKey<R>> = holder.tags()

    override fun unwrap(): Either<ResourceKey<R>, R> = holder.unwrap()

    override fun unwrapKey(): Optional<ResourceKey<R>> = holder.unwrapKey()

    override fun kind(): Holder.Kind = holder.kind()

    override fun canSerializeIn(owner: HolderOwner<R>): Boolean = holder.canSerializeIn(owner)

    fun getDelegate(): Holder<R> = holder
}

fun <R, T : R> DeferredHolder<R, T>.wrap() = this
*///?}