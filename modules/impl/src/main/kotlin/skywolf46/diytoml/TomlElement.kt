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

    data class Array<T : TomlElement<*>>(private val origin: kotlin.Array<T>) : TomlElement<kotlin.Array<T>> {
        override fun asKotlinObject(): kotlin.Array<T> {
            return origin
        }

        override fun toString(): kotlin.String {
            return "[${origin.joinToString(", ") { it.toString() }}]"
        }

        override fun equals(other: Any?): kotlin.Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Array<*>

            return origin.contentEquals(other.origin)
        }

        override fun hashCode(): Int {
            return origin.contentHashCode()
        }
    }

    data class Table<T : TomlElement<*>>(private val origin: Map<kotlin.String, T>) :
        TomlElement<Map<kotlin.String, T>> {
        override fun asKotlinObject(): Map<kotlin.String, T> {
            return origin
        }

        override fun toString(): kotlin.String {
            return "{${origin.entries.joinToString(",") { "${it.key}: ${it.value}" }}}"
        }
    }

}