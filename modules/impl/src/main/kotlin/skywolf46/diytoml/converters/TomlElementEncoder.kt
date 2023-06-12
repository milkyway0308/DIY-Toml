package skywolf46.diytoml.converters

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import skywolf46.diytoml.TomlElement
import skywolf46.diytoml.api.Converter
import skywolf46.diytoml.api.ConverterContainer
import skywolf46.diytoml.util.convertRecursive
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

sealed interface TomlElementEncoder<FROM : Any, TO : TomlElement<*>> : Converter<FROM, TO> {
    object String : TomlElementEncoder<kotlin.String, TomlElement.String> {
        override fun convert(
            container: ConverterContainer, from: kotlin.String
        ): Either<Throwable, TomlElement.String> {
            return TomlElement.String(from).right()
        }
    }

    object Long : TomlElementEncoder<kotlin.Long, TomlElement.Long> {
        override fun convert(container: ConverterContainer, from: kotlin.Long): Either<Throwable, TomlElement.Long> {
            return TomlElement.Long(from).right()
        }
    }

    object Float : TomlElementEncoder<kotlin.Float, TomlElement.Float> {
        override fun convert(container: ConverterContainer, from: kotlin.Float): Either<Throwable, TomlElement.Float> {
            return TomlElement.Float(from).right()
        }
    }

    object Double : TomlElementEncoder<kotlin.Double, TomlElement.Float> {
        override fun convert(container: ConverterContainer, from: kotlin.Double): Either<Throwable, TomlElement.Float> {
            return TomlElement.Float(from.toFloat()).right()
        }
    }

    object Date : TomlElementEncoder<LocalDate, TomlElement.Date> {
        override fun convert(container: ConverterContainer, from: LocalDate): Either<Throwable, TomlElement.Date> {
            return TomlElement.Date(from).right()
        }
    }

    object Time : TomlElementEncoder<LocalTime, TomlElement.Time> {
        override fun convert(container: ConverterContainer, from: LocalTime): Either<Throwable, TomlElement.Time> {
            return TomlElement.Time(from).right()
        }
    }

    object DateTime : TomlElementEncoder<LocalDateTime, TomlElement.DateTime> {
        override fun convert(
            container: ConverterContainer, from: LocalDateTime
        ): Either<Throwable, TomlElement.DateTime> {
            return TomlElement.DateTime(from).right()
        }
    }

    object ZonedDateTime : TomlElementEncoder<java.time.ZonedDateTime, TomlElement.ZonedDateTime> {
        override fun convert(
            container: ConverterContainer, from: java.time.ZonedDateTime
        ): Either<Throwable, TomlElement.ZonedDateTime> {
            return TomlElement.ZonedDateTime(from).right()
        }
    }

    object Array : TomlElementEncoder<kotlin.Array<*>, TomlElement.Array<*>> {
        override fun convert(
            container: ConverterContainer, from: kotlin.Array<*>
        ): Either<Throwable, TomlElement.Array<*>> {
            return TomlElement.Array(from.map { target ->
                container.convertRecursive(target!!).getOrElse { return it.left() }
            }).right()
        }
    }

    object Table : TomlElementEncoder<Map<kotlin.String, *>, TomlElement.Table<TomlElement<*>>> {
        override fun convert(
            container: ConverterContainer, from: Map<kotlin.String, *>
        ): Either<Throwable, TomlElement.Table<TomlElement<*>>> {
            return TomlElement.Table(from.mapValues { value ->
                container.convertRecursive(value).getOrElse { return it.left() }
            }).right()
        }
    }
}