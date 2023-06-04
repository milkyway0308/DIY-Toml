package skywolf46.diytoml.parser

import arrow.core.Either
import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.toOption


abstract class ContextConsumer<T : Any> {
    private val registry = mutableListOf<ContextConsumer<*>>()

    open fun getAllowedStartingChars(): Array<Char> {
        return emptyArray()
    }

    open fun getPriority(): Int {
        return 0
    }

    fun isCompatible(tomlContext: TomlContext): Boolean {
        val current = tomlContext.current().getOrElse { throw IllegalStateException("Unexpected EOL at delimiter") }
        if (current.isEndOfLine())
            throw IllegalStateException("Unexpected EOL at delimiter")
        current.mark()
        return (current.consume(true).getOrElse {
            throw IllegalStateException("Unexpected EOL at delimiter")
        } in getAllowedStartingChars()).apply {
            current.reset()
        } && checkCompatible(tomlContext)
    }

    protected open fun checkCompatible(tomlContext: TomlContext): Boolean {
        return true
    }

    abstract fun consume(tomlContext: TomlContext): Either<Throwable, T>


    fun findConsumer(context: TomlContext): Option<ContextConsumer<*>> {
        return registry.find { it.isCompatible(context) }.toOption()
    }

    fun registerConsumer(consumer: ContextConsumer<*>): ContextConsumer<T> {
        registry += consumer
        return this
    }
}