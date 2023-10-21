package skywolf46.diytoml.test

import org.junit.jupiter.api.Test
import skywolf46.diytoml.ConverterContainerImpl
import skywolf46.diytoml.StringListProvider
import skywolf46.diytoml.api.TomlSpec
import skywolf46.diytoml.parser.TomlContext
import java.io.File


class ParseTest {
    val file = File("src/test/resources/test.toml")

    @Test
    fun test() {
        TomlContext(
            StringListProvider(file.bufferedReader().readLines().iterator()),
            ConverterContainerImpl(TomlSpec())
        ).use {
            while (it.current().isSome()) {
                println(it.current().getOrNull()?.consumeAll())
            }
            println("EOF")
        }
    }

    @Test
    fun test2() {
        val data = file.bufferedReader().readLines()
        val start = System.currentTimeMillis()
        for (x in 0 until 100_000) {
            TomlContext(StringListProvider(data.iterator()), ConverterContainerImpl(TomlSpec())).use {
                it.read()
            }
        }
        println("Took ${System.currentTimeMillis() - start}ms")
    }

}