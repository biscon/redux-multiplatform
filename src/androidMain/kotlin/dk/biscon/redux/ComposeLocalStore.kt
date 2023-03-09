package dk.biscon.redux

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineDispatcher

@Composable
actual inline fun <LocalValue, LocalAction, GlobalValue, reified GlobalAction, GlobalEnvironment> rememberLocalStore(
    globalStore: GlobalStore<GlobalValue, GlobalAction, GlobalEnvironment>,
    dispatcher: CoroutineDispatcher,
    crossinline getLocalCopy: @DisallowComposableCalls (GlobalValue) -> LocalValue
): ComposeLocalStore<LocalValue, LocalAction> {
    var prevLocalValue: LocalValue = remember { getLocalCopy(globalStore.value) }

    val state = produceState(getLocalCopy(globalStore.value)) {
        val stateChanger: ((GlobalValue) -> Unit) = { globalValue: GlobalValue ->
            val newLocalValue = getLocalCopy(globalValue)
            // Only update value if the local state have changed.
            if (prevLocalValue != newLocalValue) {
                logStateDiff(prevLocalValue!!, newLocalValue!!)
                value = newLocalValue
            }
            prevLocalValue = newLocalValue
        }

        globalStore.subscribe(stateChanger)

        awaitDispose {
            globalStore.desubscribe(stateChanger)
        }
    }

    val scope = rememberCoroutineScope { dispatcher }
    val localStore: ComposeLocalStore<LocalValue, LocalAction> = remember {
        object : ComposeLocalStore<LocalValue, LocalAction> {
            override fun send(action: LocalAction) {
                if (action is GlobalAction) {
                    globalStore.sendAction(action as GlobalAction, scope)
                }
            }
            override val state: State<LocalValue> = state
        }
    }

    return localStore
}