package skywolf46.diytoml

import skywolf46.diytoml.api.TomlProvider
import skywolf46.diytoml.api.TomlReader
import skywolf46.diytoml.api.TomlSpec
import skywolf46.diytoml.api.TomlWriter
import skywolf46.diytoml.api.annotations.InternalAPI

@OptIn(InternalAPI::class)
object DiyToml {
    // TODO : Replace with reflection to avoid direct dependency to implementation
    @InternalAPI
    private lateinit var provider: TomlProvider

    fun createWriter(spec: TomlSpec): TomlWriter {
        return provider.createWriter(spec)
    }

    fun createReader(spec: TomlSpec): TomlReader {
        return provider.createReader(spec)
    }
}