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

    private fun isCompatible(tomlContext: TomlContext): Boolean {
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
            checkCompatible(tomlContext)
        }
    }

    protected open fun checkCompatible(tomlContext: TomlContext): Boolean {
        return true
    }

    abstract fun consume(tomlContext: TomlContext): Either<Throwable, T>


    fun findConsumer(context: TomlContext): Option<ContextConsumer<*>> {
        println("Consumers: ${registry}")
        return registry.find { it.isCompatible(context).apply {
            if(!this) {
                println("Incompatible at ${it.javaClass.name}")
            } else {
                println("Compatible at ${it.javaClass.name}")
            }
        } }.toOption()
    }

    fun registerConsumer(consumer: ContextConsumer<*>): ContextConsumer<T> {
        registry += consumer
        registry.sortWith(Comparator.comparingInt { it.getPriority() })
        return this
    }
}