package model.events

import tornadofx.*

object MoveOrderEvent: FXEvent(EventBus.RunOn.BackgroundThread)
