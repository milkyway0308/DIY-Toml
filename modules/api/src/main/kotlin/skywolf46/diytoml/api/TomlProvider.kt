package skywolf46.diytoml.api

import skywolf46.diytoml.api.annotations.InternalAPI
import kotlin.reflect.KClass

@InternalAPI
interface TomlProvider {
    fun createWriter(spec: TomlSpec): TomlWriter

    fun createReader(spec: TomlSpec): TomlReader

    fun <FROM : Any> registerGlobalEncoder(from: KClass<FROM>, encoder: Converter<FROM, TomlElement<*>>)

    fun <FROM : TomlElement<*>, TO : Any> registerGlobalDecoder(
        from: KClass<TO>, to: KClass<TO>, encoder: Converter<FROM, TO>
    )
}