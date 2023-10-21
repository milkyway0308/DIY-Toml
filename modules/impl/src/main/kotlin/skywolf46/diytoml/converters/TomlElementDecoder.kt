package skywolf46.diytoml.converters

import skywolf46.diytoml.TomlElement
import skywolf46.diytoml.api.Converter

sealed interface TomlElementDecoder<FROM : TomlElement<*>, TO : Any> : Converter<FROM, TO> {
}