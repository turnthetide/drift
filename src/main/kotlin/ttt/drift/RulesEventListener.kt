package ttt.drift

import java.util.*

abstract class RulesEventListener : EventListener {

    abstract fun event(event: Event)

}
