package dk.biscon.redux

import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

actual fun log(message: String) {
    if(!Redux.debugMode) {
        return
    }
    println(message)
}

/**
 * Function uses kotlin reflection to lookup all properties, comparing value of each
 * and logging a human readable diff. SingleEvents are peeked for their values.
 * it has to call Log for each properties inorder to be in control of the default
 * logcat linebreaking. Lines which are too long get split into several log messages
 * where subsequent ones are indented. This improves readability A LOT
 *
 * The catch all exception handler is there because a lot of stuff can go wrong with reflection
 * for instance if the state is private in a companion object you'll get an IllegalAccessException
 *
 * Since debugging output is not crucial we don't allow it to crash, instead we log that we
 * couldn't log and why.
 */
@Suppress("UNCHECKED_CAST")
actual fun logStateDiff(state1: Any, state2: Any) {
    if(!Redux.debugMode) {
        return
    }
    log("State update for ${state1::class.simpleName}:")
    // I cannot get rid of the unchecked cast warning but it shouldn't matter since we're getting
    // the class object from the same instance we pass to get(property)
    try {
        (state1::class as KClass<Any>).memberProperties.forEach { member ->
            StringBuilder().apply {
                val value1 = member.get(state1)
                val value2 = member.get(state2)
                if (value1 != value2) {
                    append("\t${member.name}: ")
                    append(formatValue(value1))
                    append(" -> ")
                    append("${formatValue(value2)}\n")
                }
            }.also {
                log(it.toString())
            }
        }
    } catch (t : Throwable) {
        log("State diff could not be logged. Exception occurred while accessing state members trough reflection:\n\t${t.message}")
    }
}

private fun formatValue(value: Any?) : String =
    when(value) {
        is String -> "\"$value\""
        else -> "$value"
    }

/**
 * Converts actions (or any class really) to a human readable string suitable for the debugging
 * output. Data classes are formatted as $Superclass.$Dataclass::toString() to use data classes
 * built in toString method. Other classes are formatted as $Superclass.$Class
 */
actual fun getActionDescription(action : Any) : String {
    val cls = action::class
    val name = cls.qualifiedName ?: "AnonymousClass"
    var almostQualifiedName = name
    var index = name.lastIndexOf('.', name.lastIndex)
    if(index != -1 && index-1 > 0) {
        index = name.lastIndexOf('.', index-1)
        almostQualifiedName = name.substring(index+1)
    }
    return if(!cls.isData) {
        almostQualifiedName
    } else {
        "${almostQualifiedName.substringBefore('.')}.$action"
    }

}