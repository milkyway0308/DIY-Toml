package skywolf46.diytoml.api

import arrow.core.Either
import arrow.core.Option
import java.io.File
import java.io.OutputStream

interface TomlWriter {
    fun write(map: Map<String, Any>): Either<Throwable, String>

    fun write(stream: OutputStream, map: Map<String, Any>): Option<Throwable>

    fun write(file: File, map: Map<String, Any>): Option<Throwable>
}