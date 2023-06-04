package skywolf46.diytoml.parser.impl

import arrow.core.Either
import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.right
import skywolf46.diytoml.parser.ContextConsumer
import skywolf46.diytoml.parser.TomlContext

class KeyConsumer : ContextConsumer<String>() {
    override fun consume(tomlContext: TomlContext): Either<Throwable, String> {
        val keyBuilder = StringBuilder()
        val current = tomlContext.current().getOrElse { throw IllegalStateException("wtf") }
        current.mark()
        val data = current.consume(true).getOrElse { throw IllegalStateException("Unexpected EOL at row name") }
        when (data) {
            '\'', '\"' -> {
                return readQuotedString(data, tomlContext).apply {
                    if (current.consume(true).getOrElse {
                            throw IllegalStateException("No value after key")
                        } != '=')
                        throw IllegalStateException("Unexpected token after key name")
                }.right()
            }
            else -> {
                current.reset()
                if (!current.consumeUntil('=', true) {
                        keyBuilder.append(it)
                    }) {
                    throw IllegalStateException("Row key net ended")
                }
                println("Left char: ${current.peekAll()}")
                println("Read ${keyBuilder}")
                return keyBuilder.toString().right()
            }
        }

    }

    private fun readQuotedString(delimiter: Char, tomlContext: TomlContext): String {
        val builder = StringBuilder()
        val data = tomlContext.current().getOrElse { throw IllegalStateException("Unexpected EOL in text") }
        if (!data.consumeUntil(delimiter, true) {
                builder.append(it)
            }) {
            throw IllegalStateException("Quote not closed")
        }
        return builder.toString()
    }

}