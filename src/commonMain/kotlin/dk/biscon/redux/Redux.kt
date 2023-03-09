package dk.biscon.redux

object Redux {
    var debugMode = false
        private set

    private var isInitialized = false

    fun init(debugMode: Boolean) {
        if(isInitialized) {
            throw IllegalStateException("You can only call Redux.init once")
        }
        Redux.debugMode = debugMode
        isInitialized = true
    }
}