package skywolf46.diytoml.parser.impl

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.right
import skywolf46.diytoml.TomlElement
import skywolf46.diytoml.parser.ContextConsumer
import skywolf46.diytoml.parser.TomlContext
import skywolf46.diytoml.parser.impl.elements.FloatConsumer
import skywolf46.diytoml.parser.impl.elements.InlineTableConsumer
import skywolf46.diytoml.parser.impl.elements.NumberConsumer
import skywolf46.diytoml.parser.impl.elements.StringConsumer

class RowConsumer : ContextConsumer<TomlElement.Row>() {
    init {
        registerConsumer(StringConsumer())
        registerConsumer(FloatConsumer())
        registerConsumer(NumberConsumer())
        registerConsumer(TableConsumer())
        registerConsumer(InlineTableConsumer)
    }

    override fun consume(tomlContext: TomlContext, endChar: Array<Char>): Either<Throwable, TomlElement.Row> {
        val key = TableKeyConsumer().consume(tomlContext,).getOrElse {
            throw IllegalStateException("Illegal key")
        }
        val value =
            findConsumer(tomlContext,).getOrElse {
                throw IllegalStateException("Unexpected token")
            }.consume(tomlContext,).getOrElse {
                throw IllegalStateException("Illegal value format", it)
            }
        tomlContext.current(false).onSome {
            if (it.peekAll().isNotBlank()) {
                throw IllegalStateException("Unexpected character '${it.peekAll()}' after row value")
            }
        }
        return TomlElement.Row(key.asKotlinObject(), value).right()
    }

}