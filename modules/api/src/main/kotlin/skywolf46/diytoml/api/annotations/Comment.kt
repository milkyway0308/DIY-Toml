package skywolf46.diytoml.api.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Comment(vararg val value: String)
