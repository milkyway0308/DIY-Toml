package skywolf46.diytoml.api

interface TomlElement<ORIGIN: Any> {
    fun asKotlinObject() : ORIGIN

    fun toString(prefix: String)
}