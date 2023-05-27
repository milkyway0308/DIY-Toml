package skywolf46.diytoml.api

import arrow.core.EitherNel
import java.io.File
import java.io.InputStream
import kotlin.reflect.KClass

interface TomlReader {
    fun <T : Any> read(remapTo: KClass<T>, inputStream: InputStream): EitherNel<Throwable, T>

    fun <T : Any> read(remapTo: KClass<T>, string: String): EitherNel<Throwable, T>

    fun <T : Any> read(remapTo: KClass<T>, file: File): EitherNel<Throwable, T>
}