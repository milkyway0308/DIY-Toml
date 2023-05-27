package skywolf46.diytoml.api

import arrow.core.Either

interface Converter<FROM : Any, TO : Any> {
    fun convert(from: FROM): Either<Throwable, TO>
}