package skywolf46.diytoml.api

import arrow.core.Either
import kotlin.reflect.KClass

interface ConverterContainer {
    fun <T : Any> convert(any: T): Either<Throwable, Any>

    fun <T : Any> isConvertible(target: KClass<T>): Boolean
}