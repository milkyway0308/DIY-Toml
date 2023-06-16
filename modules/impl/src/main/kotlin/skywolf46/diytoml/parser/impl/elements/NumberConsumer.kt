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
    override fun consume(tomlContext: TomlContext): Either<Throwable, TomlElement.Long> {
        val current = tomlContext.current().getOrElse { return IllegalStateException().left() }
        current.mark()
        return when (current.consume()) {
            '-' -> {
                parseNumber(current).map { -it }
            }

            '+' -> {
                parseNumber(current)
            }

            else -> {
                current.reset()
                parseNumber(current)
            }
        }.map { TomlElement.Long(it) }
    }

    private fun parseNumber(current: TomlContext.IndexedString): Either<Throwable, Long> {
        return try {
            current.consumeAllIfRange(targetChars, true).apply { println(this) }.map { it.toLong() }
        } catch (e: Exception) {
            e.left()
        }
    }


    override fun getAllowedStartingChars(): Array<Char> {
        return collect(arrayOf('-', '+'), '0'..'9')
    }

    override fun getPriority(): Int {
        return 500
    }

    override fun checkCompatible(tomlContext: TomlContext): Boolean {
        return consume(tomlContext).isRight()
    }
}