package skywolf46.diytoml.api

import arrow.core.Either
import arrow.core.Option
import skywolf46.diytoml.api.elements.TomlMap
import java.io.File
import java.io.OutputStream

interface TomlWriter {
    fun write(map: Map<String, Any>): Either<Throwable, String>

    fun write(stream: OutputStream, map: Map<String, Any>): Option<Throwable>

    fun write(file: File, map: Map<String, Any>): Option<Throwable>

    fun writeRaw(map: TomlMap): Either<Throwable, String>

    fun writeRaw(stream: OutputStream, map: TomlMap): Option<Throwable>

    fun writeRaw(file: File, map: TomlMap): Option<Throwable>
}