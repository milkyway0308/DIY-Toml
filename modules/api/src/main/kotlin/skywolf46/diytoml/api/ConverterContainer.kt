package skywolf46.diytoml.api

import arrow.core.Either

interface ConverterContainer {
    fun <T : Any> convert(any: T): Either<Throwable, Any>
}