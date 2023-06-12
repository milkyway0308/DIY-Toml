package skywolf46.diytoml.parser.impl

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import skywolf46.diytoml.TomlElement
import skywolf46.diytoml.parser.ContextConsumer
import skywolf46.diytoml.parser.TomlContext

class TableConsumer : ContextConsumer<TomlElement.ConstructedTable<*>>() {

    override fun consume(tomlContext: TomlContext): Either<Throwable, TomlElement.ConstructedTable<*>> {
        val info = TableInfoConsumer().consume(tomlContext).getOrElse { return it.left() }
        val dataList = mutableMapOf<String, TomlElement<*>>()
        while (tomlContext.current().isSome()) {
            val current = tomlContext.current().getOrElse { return Exception("Impossible operation occurred").left() }
            current.mark()
            if (current.consume() == '[') {
                current.reset()
                break
            }
            current.reset()
            val data = RowConsumer().consume(tomlContext)
            if (data.isLeft()) {
                break
            }
            data.onRight {
                val name = "${info.tableName}.${it.rowName}"
                if (name in dataList) {
                    return Exception().left()
                }
                dataList[name] = it.rowValue
            }
        }

        return TomlElement.ConstructedTable(info, dataList).right()
    }


    override fun getAllowedStartingChars(): Array<Char> {
        return arrayOf('[')
    }


}