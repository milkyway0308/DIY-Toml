package skywolf46.diytoml.api

import skywolf46.diytoml.api.annotations.InternalAPI
import kotlin.reflect.KClass

@InternalAPI
interface TomlProvider {
    fun createWriter(spec: TomlSpec): TomlWriter

    fun createReader(spec: TomlSpec): TomlReader

    fun getConverterContainer() : ConverterContainer
}