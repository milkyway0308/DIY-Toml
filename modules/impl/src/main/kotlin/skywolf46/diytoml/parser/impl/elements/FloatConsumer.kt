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

    override fun consume(tomlContext: TomlContext): Either<Throwable, TomlElement.Float> {
        return parseNumber(tomlContext.current().getOrElse { return Exception("Unexpected end of line").left() }).map { TomlElement.Float(it) }
    }

    private fun parseNumber(current: TomlContext.IndexedString): Either<Throwable, Float> {
        current.mark()
        return Either.catch {
            when (current.consume()) {
                '-' -> {
                    current.consumeAllIfRange(targetChars, true)
                        .getOrElse { return Exception().left() }.toFloat()
                }
                '+' -> {
                    current.consumeAllIfRange(targetChars, true)
                        .getOrElse { return Exception().left() }.toFloat()
                }
                else -> {
                    current.reset()
                    current.consumeAllIfRange(targetChars, true)
                        .getOrElse { return Exception().left() }.toFloat()
                }
            }
        }
    }

    override fun checkCompatible(tomlContext: TomlContext): Boolean {
        return parseNumber(tomlContext.current().getOrElse { return false }).isRight()
    }


    override fun getPriority(): Int {
        return 1000
    }

}