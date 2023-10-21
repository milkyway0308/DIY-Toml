package skywolf46.diytoml.parser.impl.elements

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import skywolf46.diytoml.TomlElement
import skywolf46.diytoml.parser.ContextConsumer
import skywolf46.diytoml.parser.TomlContext
import skywolf46.diytoml.parser.impl.TableKeyConsumer

object InlineTableConsumer : ContextConsumer<TomlElement.Table<*>>() {
    init {
        registerConsumer(StringConsumer())
        registerConsumer(FloatConsumer())
        registerConsumer(NumberConsumer())
        registerConsumer(this)
    }

    override fun consume(tomlContext: TomlContext, endChar: Array<Char>): Either<Throwable, TomlElement.Table<*>> {
        tomlContext.current().getOrElse {
            return Exception("Impossible error occured").left()
        }.consumeUntil('{') {}
        val data = mutableMapOf<String, TomlElement<*>>()
        while (tomlContext.current().isSome()) {
            val current = tomlContext.current().getOrNull() ?: break
            current.mark()
            if (current.consume(true).map { it == '}' }.getOrElse { false }) {
                current.reset()
                break
            }
            current.reset()
            val key = TableKeyConsumer().consume(tomlContext).getOrElse {
                throw it
            }.asKotlinObject()
            if (key in data) {
                throw Exception("Duplicated key")
            }
            val value = findConsumer(tomlContext, arrayOf(',', '}')).getOrElse {
                throw IllegalStateException("Unexpected token")
            }.consume(tomlContext, arrayOf(',', '}')).getOrElse {
                throw Exception("Illegal value format")
            }
            data[key] = value
            var isEscaped = false
            current.consumeIfRange(emptyArray(), true) {}
            while (!current.isEndOfLine()) {
                when (val next = current.consume()) {
                    ',' -> {
                        if (current.peekAll().isBlank()) {
                            throw IllegalStateException("Unexpected EOL after inline table row end")
                        }
                        isEscaped = true
                        break
                    }

                    '}' -> {
                        isEscaped = true
                        break
                    }

                    else -> {
                        throw IllegalStateException("Unexpected character '$next' after inline table row value")
                    }
                }
            }
            if (!isEscaped) {
                throw IllegalStateException("No comma or end after inline table row value")
            }
        }
        return TomlElement.Table(data).right()
    }


    override fun getAllowedStartingChars(): Array<Char> {
        return arrayOf('{')
    }


}