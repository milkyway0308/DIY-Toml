package skywolf46.diytoml.api

import arrow.core.Either

interface Converter<FROM : Any, TO : Any> {
    fun convert(container: ConverterContainer, from: FROM): Either<Throwable, TO>
}