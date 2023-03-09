package dk.biscon.redux

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.State
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface ComposeLocalStore<Value, Action> {
    val state: State<Value>
    fun send(action: Action)
}

@Composable
expect inline fun <LocalValue, LocalAction, GlobalValue, reified GlobalAction, GlobalEnvironment> rememberLocalStore(
    globalStore: GlobalStore<GlobalValue, GlobalAction, GlobalEnvironment>,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    crossinline getLocalCopy: @DisallowComposableCalls (GlobalValue) -> LocalValue
): ComposeLocalStore<LocalValue, LocalAction>