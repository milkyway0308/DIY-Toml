package skywolf46.diytoml.parser.impl

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.right
import skywolf46.diytoml.TomlElement
import skywolf46.diytoml.parser.ContextConsumer
import skywolf46.diytoml.parser.TomlContext

class TableKeyConsumer : ContextConsumer<TomlElement.String>() {
    override fun consume(tomlContext: TomlContext, endChar: Array<Char>): Either<Throwable, TomlElement.String> {
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
                }.run { TomlElement.String(this) }.right()
            }

            else -> {
                current.reset()
                while (!current.isEndOfLine()) {
                    current.mark()
                    val next = current.consume()
                    if (next != ' ' && next != '\t') {
                        current.reset()
                        break
                    }
                }
                var isWhitespaceMarked = false
                while (!current.isEndOfLine()) {
                    when (val next = current.consume()) {
                        ' ', '\t' -> isWhitespaceMarked = true
                        '=' -> break
                        else -> {
                            if (isWhitespaceMarked)
                                throw IllegalStateException("Row key cannot contain whitespace when not quoted")
                            keyBuilder.append(next)
                        }
                    }
                }
                if (current.isEndOfLine())
                    throw IllegalStateException("Row key net ended")
                return TomlElement.String(keyBuilder.toString()).right()
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