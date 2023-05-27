package skywolf46.diytoml.api

data class TomlSpec(
    val allowNullValue: Boolean = false,
    val allowRecursiveConversion: Boolean = false,
    val autoCreateReflectiveConverter: Boolean = true,
)