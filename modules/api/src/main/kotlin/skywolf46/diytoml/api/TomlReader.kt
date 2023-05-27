package skywolf46.diytoml.api

import arrow.core.Either
import arrow.core.NonEmptyList
import skywolf46.diytoml.api.elements.TomlMap
import java.io.File
import java.io.InputStream
import kotlin.reflect.KClass

interface TomlReader {
    fun read(inputStream: InputStream): Either<NonEmptyList<Throwable>, Map<String, Any>>

    fun read(string: String): Either<NonEmptyList<Throwable>, Map<String, Any>>

    fun read(file: File): Either<NonEmptyList<Throwable>, Map<String, Any>>

    fun <T : Any> read(remapTo: KClass<T>, inputStream: InputStream): Either<NonEmptyList<Throwable>, T>

    fun <T : Any> read(remapTo: KClass<T>, string: String): Either<NonEmptyList<Throwable>, T>

    fun <T : Any> read(remapTo: KClass<T>, file: File): Either<NonEmptyList<Throwable>, T>

    fun readRaw(inputStream: InputStream): Either<NonEmptyList<Throwable>, TomlMap>

    fun readRaw(string: String): Either<NonEmptyList<Throwable>, TomlMap>

    fun readRaw(file: File): Either<NonEmptyList<Throwable>, TomlMap>
}