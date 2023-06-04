package skywolf46.diytoml.parser.impl.elements

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.right
import skywolf46.diytoml.TomlElement
import skywolf46.diytoml.parser.ContextConsumer
import skywolf46.diytoml.parser.TomlContext

class StringConsumer : ContextConsumer<TomlElement.String>() {
    override fun consume(tomlContext: TomlContext): Either<Throwable, TomlElement.String> {
        val data = tomlContext.current().getOrElse { throw IllegalStateException("Unexpected string EOL") }
        val builder = StringBuilder()
        data.consume()
        if (!data.consumeUntil(data.consume(true).getOrElse { throw IllegalStateException("Unexpected EOL") }, true) {
                builder.append(it)
            }) {
            throw IllegalStateException("Quote not closed")
        }
        return TomlElement.String(builder.toString()).right()
    }


    override fun getAllowedStartingChars(): Array<Char> {
        return arrayOf('"', '\'')
    }
}