package skywolf46.diytoml.parser.impl

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.right
import skywolf46.diytoml.TomlElement
import skywolf46.diytoml.parser.ContextConsumer
import skywolf46.diytoml.parser.TomlContext
import skywolf46.diytoml.parser.impl.elements.FloatConsumer
import skywolf46.diytoml.parser.impl.elements.NumberConsumer
import skywolf46.diytoml.parser.impl.elements.StringConsumer

class RowConsumer : ContextConsumer<TomlElement.Row>() {
    init {
        registerConsumer(StringConsumer())
        registerConsumer(FloatConsumer())
        registerConsumer(NumberConsumer())
    }

    override fun consume(tomlContext: TomlContext): Either<Throwable, TomlElement.Row> {
        val key = KeyConsumer().consume(tomlContext).getOrElse {
            throw IllegalStateException("Illegal key")
        }
        println("Key parsed, left: ${tomlContext.current().getOrNull()?.peekAll()}")
        val value =
            findConsumer(tomlContext).getOrElse {
                throw IllegalStateException("Unexpected token")
            }.consume(tomlContext).getOrElse {
                throw IllegalStateException("Illegal value format", it)
            }
        return TomlElement.Row(key.asKotlinObject(), value).right()
    }

}