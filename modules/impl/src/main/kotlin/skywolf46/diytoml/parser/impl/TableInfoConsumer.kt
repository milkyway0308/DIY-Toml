package skywolf46.diytoml.parser.impl

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.right
import skywolf46.diytoml.parser.ContextConsumer
import skywolf46.diytoml.parser.TomlContext
import skywolf46.diytoml.util.collect

class TableInfoConsumer : ContextConsumer<TableInfoConsumer.TableInfo>(){
    //A-Za-z0-9_-
    private val tableParameter = collect(arrayOf('_', '-'), 'A'..'Z', 'a'..'z', '0'..'9')


    override fun consume(tomlContext: TomlContext): Either<Throwable, TableInfo> {
        val current = tomlContext.current().getOrElse { throw IllegalStateException() }
        current.mark()
        if (current.consume() != '[') throw IllegalStateException("Table name not starts with '['")
        var stage = ParseStage.CHECK_TABLE

        while (!current.isEndOfLine()) {
            when (stage) {
                ParseStage.CHECK_TABLE -> {
                    current.mark()
                    stage = if (current.consume() == '[') {
                        ParseStage.READ_ARRAY_CONTENTS
                    } else {
                        current.reset()
                        ParseStage.READ_CONTENTS
                    }
                }

                ParseStage.READ_CONTENTS -> {
                    val builder = StringBuilder()
                    while (!current.isEndOfLine()) {
                        val data = current.consume()
                        if (data == ']') {
                            if (!current.isEndOfLine()) {
                                throw IllegalStateException("Something is after table end")
                            }
                            return TableInfo(false, builder.toString()).right()
                        }
                        if (data !in tableParameter) {
                            throw IllegalStateException("Illegal character found : $data")
                        }
                        builder.append(data)
                    }
                }

                ParseStage.READ_ARRAY_CONTENTS -> {
                    val builder = StringBuilder()
                    var isDotted = false
                    while (!current.isEndOfLine()) {
                        val data = current.consume()
                        if (data == ']') {
                            if (isDotted) {
                                throw IllegalStateException("Dot must have to place before some text")
                            }
                            if (current.isEndOfLine()) {
                                throw IllegalStateException("Table is not ended")
                            }
                            if (current.consume() != ']') {
                                throw IllegalStateException("Array table name not closed")
                            }
                            if (!current.isEndOfLine()) {
                                throw IllegalStateException("Something is after table end")
                            }
                            if (builder.isEmpty()) {
                                throw IllegalStateException("Empty table key not allowed when it's not quoted")
                            }
                            return TableInfo(true, builder.toString()).right()
                        }
                        isDotted = when (data) {
                            '.' -> {
                                if (isDotted) {
                                    throw IllegalStateException("Detected double-dotted text")
                                }
                                if (builder.isEmpty()) {
                                    throw IllegalStateException("Dot must have to place after some text")
                                }
                                builder.append(data)
                                true
                            }


                            '"' -> {
                                builder.append(parseQuotedString('"', current))
                                false
                            }

                            '\'' -> {
                                builder.append(parseQuotedString('\'', current))
                                false
                            }
                            !in tableParameter -> {
                                throw IllegalStateException("Illegal character found : $data")
                            }

                            else -> {
                                builder.append(data)
                                false
                            }
                        }
                    }
                }
            }
        }
        throw IllegalStateException("Line is empty : $stage")
    }

    private fun parseQuotedString(delimeter: Char, indexedString: TomlContext.IndexedString): String {
        val builder = StringBuilder()
        if (!indexedString.consumeUntil(delimeter) {
                builder.append(it)
            }) {
            throw IllegalStateException("Quote not closed")
        }
        return builder.toString()
    }

    private enum class ParseStage {
        CHECK_TABLE, READ_ARRAY_CONTENTS, READ_CONTENTS
    }

    data class TableInfo(val isArray: Boolean, val tableName: String)
}