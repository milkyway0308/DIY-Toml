package skywolf46.diytoml.api

import kotlin.reflect.KClass

data class TomlSpec(
    val allowNullValue: Boolean = false,
    val allowRecursiveConversion: Boolean,
    val customConverter: Map<KClass<*>, Converter<*, *>>
)