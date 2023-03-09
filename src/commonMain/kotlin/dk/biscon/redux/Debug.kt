package dk.biscon.redux

expect fun log(message: String)
expect fun logStateDiff(state1: Any, state2: Any)
expect fun getActionDescription(action : Any) : String