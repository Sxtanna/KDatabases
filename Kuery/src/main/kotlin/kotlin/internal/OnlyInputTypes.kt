package kotlin.internal

/**
 * Basically, this fixes strict typing for extension functions...
 */
@Target(AnnotationTarget.TYPE_PARAMETER)
@Retention(AnnotationRetention.BINARY)
internal annotation class OnlyInputTypes