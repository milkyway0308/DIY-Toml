package skywolf46.diytoml.api

import arrow.core.Either
import kotlin.reflect.KClass

interface ConverterContainer {
    fun <T : Any> convert(any: T): Either<Throwable, Any>

    fun <FROM: Any, TO : Any> convertTo(target: KClass<TO>, any: FROM): Either<Throwable, TO>

    fun <T : Any> isConvertible(target: KClass<T>): Boolean

    fun <FROM : Any> registerConsequentialConverter(from: KClass<FROM>, converter: Converter<FROM, Any>)

    fun <FROM : Any, TO : Any> registerConverter(from: KClass<FROM>, to: KClass<TO>, converter: Converter<FROM, TO>)
}