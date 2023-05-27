package skywolf46.diytoml.api

interface ConverterContainer {
    fun <T : Any> convert(any: T): Any
}