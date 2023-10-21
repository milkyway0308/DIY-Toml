package skywolf46.diytoml.parser

import arrow.core.Either
import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.toOption
import skywolf46.diytoml.TomlElement


abstract class ContextConsumer<T : TomlElement<*>> {
    private val registry = mutableListOf<ContextConsumer<*>>()

    open fun getAllowedStartingChars(): Array<Char> {
        return emptyArray()
    }

    open fun getPriority(): Int {
        return 0
    }

    private fun isCompatible(tomlContext: TomlContext, endChar: Array<Char>): Boolean {
        val current = tomlContext.current().getOrElse { throw IllegalStateException("Unexpected EOL at delimiter") }
        if (current.isEndOfLine())
            throw IllegalStateException("Unexpected EOL at delimiter")
        current.mark()
        return (current.consume(true).getOrElse {
            throw IllegalStateException("Unexpected EOL at delimiter")
        } in getAllowedStartingChars()).apply {
            current.reset()
        } && current.withMarkPosition {
            current.markLast()
            checkCompatible(tomlContext, endChar)
        }
    }

    protected open fun checkCompatible(tomlContext: TomlContext, endChar: Array<Char>): Boolean {
        return true
    }

    abstract fun consume(tomlContext: TomlContext, endChar: Array<Char> = emptyArray()): Either<Throwable, T>


    fun findConsumer(context: TomlContext, endChar: Array<Char> = emptyArray()): Option<ContextConsumer<*>> {
        return registry.find { it.isCompatible(context, endChar) }.toOption()
    }

    fun registerConsumer(consumer: ContextConsumer<*>): ContextConsumer<T> {
        registry += consumer
        registry.sortWith(Comparator.comparingInt { it.getPriority() })
        return this
    }
}