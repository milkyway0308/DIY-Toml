package skywolf46.diytoml.api.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class TomlKey(vararg val acceptableKeyName: String)
