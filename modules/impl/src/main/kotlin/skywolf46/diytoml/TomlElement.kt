package skywolf46.diytoml

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

sealed interface TomlElement<ORIGIN : Any> {
    fun asKotlinObject(): ORIGIN

    override fun toString(): kotlin.String

    data class Boolean(private val origin: kotlin.Boolean) : TomlElement<kotlin.Boolean> {
        override fun asKotlinObject(): kotlin.Boolean {
            return origin
        }

        override fun toString(): kotlin.String {
            return origin.toString()
        }
    }

    data class String(private val origin: kotlin.String) : TomlElement<kotlin.String> {
        override fun asKotlinObject(): kotlin.String {
            return origin
        }

        override fun toString(): kotlin.String {
            return "\"${origin}\""
        }
    }

    data class Long(private val origin: kotlin.Long) : TomlElement<kotlin.Long> {
        override fun asKotlinObject(): kotlin.Long {
            return origin
        }

        override fun toString(): kotlin.String {
            return origin.toString()
        }
    }

    data class Float(private val origin: kotlin.Float) : TomlElement<kotlin.Float> {
        override fun asKotlinObject(): kotlin.Float {
            return origin
        }

        override fun toString(): kotlin.String {
            return origin.toString()
        }
    }

    data class DateTime(private val localDateTime: LocalDateTime) : TomlElement<LocalDateTime> {
        override fun asKotlinObject(): LocalDateTime {
            return localDateTime
        }

        override fun toString(): kotlin.String {
            return localDateTime.format(DateTimeFormatter.ISO_DATE_TIME)
        }
    }


    data class ZonedDateTime(private val origin: java.time.ZonedDateTime) : TomlElement<java.time.ZonedDateTime> {
        override fun asKotlinObject(): java.time.ZonedDateTime {
            return origin
        }

        override fun toString(): kotlin.String {
            return origin.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
        }
    }

    data class Time(private val origin: LocalTime) : TomlElement<LocalTime> {
        override fun asKotlinObject(): LocalTime {
            return origin
        }

        override fun toString(): kotlin.String {
            return origin.format(DateTimeFormatter.ISO_TIME)
        }
    }

    data class Date(private val origin: LocalDate) : TomlElement<LocalDate> {
        override fun asKotlinObject(): LocalDate {
            return origin
        }

        override fun toString(): kotlin.String {
            return origin.format(DateTimeFormatter.ISO_DATE)
        }
    }

    class Array<T : TomlElement<*>>(origin: List<T>) : TomlElement<List<T>> {
        private val origin = ArrayList(origin)
        override fun asKotlinObject(): List<T> {
            return origin
        }

        override fun toString(): kotlin.String {
            return "[${origin.joinToString(", ") { it.toString() }}]"
        }

        fun addValue(data: T) {
            origin.add(data)
        }
    }


    open class Table<T : TomlElement<*>>(private val origin: Map<kotlin.String, T>) :
        TomlElement<Map<kotlin.String, T>> {
        override fun asKotlinObject(): Map<kotlin.String, T> {
            return origin
        }

        override fun toString(): kotlin.String {
            return "{${origin.entries.joinToString(",") { "${it.key}: ${it.value}" }}}"
        }
    }

    class ConstructedTable<T : TomlElement<*>>(val tableInfo: TableInfo, origin: Map<kotlin.String, T>) :
        Table<T>(origin)

    data class Row(val rowName: kotlin.String, val rowValue: TomlElement<*>) : TomlElement<TomlElement<*>> {
        override fun asKotlinObject(): TomlElement<*> {
            return this
        }

        override fun toString(): kotlin.String {
            return "${rowName}: $rowValue"
        }
    }

    data class TableInfo(val isArray: kotlin.Boolean, val tableName: kotlin.String) : TomlElement<kotlin.String> {
        override fun asKotlinObject(): kotlin.String {
            return tableName
        }

    }

}