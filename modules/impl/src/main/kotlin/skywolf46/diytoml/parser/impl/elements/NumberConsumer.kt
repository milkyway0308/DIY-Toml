package skywolf46.diytoml.parser.impl.elements

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import skywolf46.diytoml.TomlElement
import skywolf46.diytoml.parser.ContextConsumer
import skywolf46.diytoml.parser.TomlContext
import skywolf46.diytoml.util.collect

class NumberConsumer : ContextConsumer<TomlElement.Long>() {
    private val targetChars = collect(arrayOf('e', '.'), '0'..'9')
    override fun consume(tomlContext: TomlContext, endChar: Array<Char>): Either<Throwable, TomlElement.Long> {
        val current = tomlContext.current().getOrElse { return IllegalStateException().left() }
        current.mark()
        return when (current.consume()) {
            '-' -> {
                current.parseNumber(endChar)
            }

            '+' -> {
                current.parseNumber(endChar)
            }

            else -> {
                current.reset()
                current.parseNumber(endChar)
            }
        }.map { TomlElement.Long(it) }
    }


    private fun TomlContext.IndexedString.parseNumber(endChar: Array<Char>): Either<Throwable, Long> {
        val builder = StringBuilder()
        consumeAllIfRange(emptyArray(), true)
        while (!isEndOfLine()) {
            mark()
            val next = consume()
            if (next in endChar) {
                reset()
                break
            }
            if (next !in targetChars)
                return IllegalStateException("Unexpected character '$next' in long").left()
            builder.append(next)
        }
        return Either.catch {
            builder.toString().toLong()
        }
    }


    override fun getAllowedStartingChars(): Array<Char> {
        return collect(arrayOf('-', '+'), '0'..'9')
    }

    override fun getPriority(): Int {
        return 500
    }

    override fun checkCompatible(tomlContext: TomlContext, endChar: Array<Char>): Boolean {
        return consume(tomlContext, endChar).isRight()
    }
}