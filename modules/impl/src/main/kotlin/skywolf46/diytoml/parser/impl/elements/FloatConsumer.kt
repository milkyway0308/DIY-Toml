package skywolf46.diytoml.parser.impl.elements

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import skywolf46.diytoml.TomlElement
import skywolf46.diytoml.parser.ContextConsumer
import skywolf46.diytoml.parser.TomlContext
import skywolf46.diytoml.util.collect

class FloatConsumer : ContextConsumer<TomlElement.Float>() {
    private val targetChars = collect(arrayOf('e', '.'), '0'..'9')

    override fun getAllowedStartingChars(): Array<Char> {
        return collect(arrayOf('-', '+'), '0'..'9')
    }

    override fun consume(tomlContext: TomlContext, endChar: Array<Char>): Either<Throwable, TomlElement.Float> {
        return parseNumber(
            tomlContext.current().getOrElse { return Exception("Unexpected end of line").left() },
            endChar
        ).map { TomlElement.Float(it) }
    }

    private fun parseNumber(current: TomlContext.IndexedString, endChar: Array<Char>): Either<Throwable, Float> {
        current.mark()
        return when (current.consume()) {
            '-' -> {
                current.parseFloat(endChar).map { -it }
            }

            '+' -> {
                current.parseFloat(endChar)
            }

            else -> {
                current.reset()
                current.parseFloat(endChar)
            }
        }

    }

    private fun TomlContext.IndexedString.parseFloat(endChar: Array<Char>): Either<Throwable, Float> {
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
                return IllegalStateException("Unexpected character '$next' in float").left()
            builder.append(next)
        }
        return Either.catch {
            builder.toString().toFloat()
        }
    }

    override fun checkCompatible(tomlContext: TomlContext, endChar: Array<Char>): Boolean {
        return parseNumber(tomlContext.current().getOrElse { return false }, endChar).isRight()
    }


    override fun getPriority(): Int {
        return 1000
    }
}