package skywolf46.diytoml.parser

import arrow.core.*
import skywolf46.diytoml.TomlElement
import skywolf46.diytoml.api.ConverterContainer
import skywolf46.diytoml.parser.impl.TableConsumer
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.reflect.KClass

class TomlContext(inputStream: InputStream, private val container: ConverterContainer) : AutoCloseable {
    private val dataStream = BufferedReader(InputStreamReader(inputStream))
    private val currentContext = mutableMapOf<String, EitherNel<TomlElement.Table<*>, TomlElement.Table<*>>>()
    private var currentLine: IndexedString? = null

    fun read(): Either<Throwable, TomlElement.Table<*>> {
        while (current().isSome()) {
            TableConsumer().consume(this)
                .onLeft { return it.left() }
                .onRight {
                    Either.catch {
                        if (it.tableInfo.isArray)
                            addValue(it.tableInfo.tableName, it)
                        else
                            setValue(it.tableInfo.tableName, it)
                    }.onLeft { return it.left() }
                }
        }
        return TomlElement.Table(currentContext.mapValues { entry ->
            entry.value.fold({
                TomlElement.Array(it)
            }, {
                it
            })
        }).right()
    }

    fun current(): Option<IndexedString> {
        if (currentLine == null || currentLine?.isEndOfLine() == true) {
            println("Next line!")
            return parseNextLine().apply { currentLine = this }?.run { if (isEndOfLine()) null else this }.toOption()
        }
        return currentLine.toOption()
    }

    private tailrec fun parseNextLine(): IndexedString? {
        val data = dataStream.readLine()?.asIndexed() ?: run {
            return null
        }
        return if (data.isEndOfLine()) {
            parseNextLine()
        } else {
            data
        }
    }

    internal fun setValue(key: String, value: TomlElement.Table<*>) {
        if (key in currentContext) {
            throw IllegalStateException("Cannot set value to $key : Key already defined")
        }
        currentContext[key] = value.right()
    }

    private fun addValue(key: String, value: TomlElement.Table<*>) {
        val parsed = currentContext[key]
        if (parsed == null) {
            currentContext[key] = value.leftNel()
        } else {
            currentContext[key] = parsed.fold(::identity) {
                throw RuntimeException("Cannot set value to $key : Try to add array-table but already table defined")
            }.plus(value).left()
        }
    }

    private fun convert(kClass: KClass<*>) {
        container
    }

    fun String.asIndexed(): IndexedString {
        return IndexedString(this)
    }

    override fun close() {
        dataStream.close()
    }

    class IndexedString(target: String) {
        val target = filterComment(target).getOrElse { "" }
        private var index = 0
        private var flag = 0

        fun consume(): Char {
            return target[index++]
        }

        fun consume(skipWhitespace: Boolean): Option<Char> {
            if (skipWhitespace) {
                while (!isEndOfLine()) {
                    val char = consume()
                    if (char == ' ' || char == '\t')
                        continue
                    return char.toOption()
                }
                return null.toOption()
            }
            return consume().toOption()
        }

        fun consumeUntil(target: Char, skipWhitespace: Boolean = false, unit: (Char) -> Unit): Boolean {
            while (!isEndOfLine()) {
                val next = consume()
                if (next == target)
                    return true
                if (skipWhitespace && (next == ' ' || next == '\t'))
                    continue
                unit(next)
            }
            return false
        }


        fun consumeIfRange(target: Array<Char>, skipWhitespace: Boolean = false, unit: (Char) -> Unit): Boolean {
            while (!isEndOfLine()) {
                val next = consume()
                if (skipWhitespace && (next == ' ' || next == '\t'))
                    continue
                if (next !in target) {
                    return false
                }
                unit(next)
            }
            return true
        }


        fun consumeAllUntil(
            target: Char,
            skipWhitespace: Boolean = false,
        ): Either<Exception, String> {
            val builder = StringBuilder()
            if (!consumeUntil(target, skipWhitespace) {
                    builder.append(it)
                }) {
                return Exception().left()
            }
            return builder.toString().right()
        }

        fun consumeAllIfRange(
            target: Array<Char>,
            skipWhitespace: Boolean = false,
        ): Either<Exception, String> {
            val builder = StringBuilder()
            if (!consumeIfRange(target, skipWhitespace) {
                    builder.append(it)
                }) {
                return Exception().left()
            }
            return builder.toString().right()
        }


        fun consumeAll(): String {
            return target.substring(index, target.length).apply {
                index = target.length
            }
        }

        fun peekAll(): String {
            println("Peek from index ${index}")
            return target.substring(index, target.length)
        }

        fun mark() {
            flag = index
        }

        fun reset() {
            index = flag
        }

        fun isEndOfLine(): Boolean {
            return index >= target.length
        }

        fun leftLength(): Int {
            return target.length - index
        }

        private fun filterComment(string: String): Option<String> {
            val paddingRemoved = filterPadding(string).getOrElse { return null.toOption() }
            if (paddingRemoved[0] == '#')
                return null.toOption()
            for (x in string.indices.reversed()) {
                if (string[x] == '"')
                    break
                if (string[x] == '#') {
                    return paddingRemoved.substring(0, x).trimEnd().toOption()
                }
            }
            return paddingRemoved.toOption()
        }

        private fun filterPadding(string: String): Option<String> {
            val start = findStart(string)
            if (start == -1)
                return null.toOption()
            val end = findEnd(string)
            if (end == -1)
                return null.toOption()
            return string.substring(start, end).toOption()
        }

        private fun findStart(string: String): Int {
            for (x in string.indices) {
                val char = string[x]
                if (char != ' ' && char != '\t')
                    return x
            }
            return -1
        }

        private fun findEnd(string: String): Int {
            for (x in string.indices.reversed()) {
                val char = string[x]
                if (char != ' ' && char != '\t')
                    return x + 1
            }
            return -1
        }

        fun origin(): String {
            return target
        }
    }

}