package skywolf46.diytoml.util

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import skywolf46.diytoml.TomlElement
import skywolf46.diytoml.api.ConverterContainer

fun <T : Any> ConverterContainer.convertRecursive(
    target: T
): Either<Throwable, TomlElement<*>> {
    return convertTailRecursive(target)
}

private tailrec fun <T : Any> ConverterContainer.convertTailRecursive(
    value: T
): Either<Throwable, TomlElement<*>> {
    val converted = convert(value).getOrElse { return it.left() }
    return when (converted) {
        is TomlElement<*> -> converted.right()
        else -> convertTailRecursive(converted)
    }
}
