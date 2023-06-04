package skywolf46.diytoml.parser.impl

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.getOrElse
import arrow.core.left
import skywolf46.diytoml.TomlElement
import skywolf46.diytoml.parser.ContextConsumer
import skywolf46.diytoml.parser.TomlContext
import skywolf46.diytoml.util.collect

class TableConsumer : ContextConsumer<EitherNel<TomlElement.Table<*>, TomlElement.Table<*>>>() {


    override fun consume(tomlContext: TomlContext): Either<Throwable, EitherNel<TomlElement.Table<*>, TomlElement.Table<*>>> {
        val info = TableInfoConsumer().consume(tomlContext).getOrElse { return it.left() }
        val data = RowConsumer().consume(tomlContext).getOrElse { throw it }
        println("Table info: $info")
        println("Key name: ${data.first} ")
        println("Value: ${data.second}(${data.second.javaClass.name} ")
        return Exception().left()
    }


    override fun getAllowedStartingChars(): Array<Char> {
        return arrayOf('[')
    }


}