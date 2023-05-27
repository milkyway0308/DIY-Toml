package skywolf46.diytoml.api.annotations

/**
 * Yeah, I stole it from 4koma.
 *
 * https://github.com/valderman/4koma/blob/main/src/main/kotlin/cc/ekblad/toml/util/InternalAPI.kt
 */
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "INTERNAL API - DO NOT USE IT IF NOT NECESSARY"
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class InternalAPI
