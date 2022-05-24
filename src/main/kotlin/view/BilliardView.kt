package view

import controller.MoveController
import javafx.scene.paint.Color
import model.BALL_RADIUS
import model.CANVAS_HEIGHT
import model.CANVAS_WIDTH
import model.Position
import model.events.RefreshEvent
import model.events.MoveOrderEvent
import tornadofx.*

class BilliardView: View("BilliardView") {

    private val positionSet = hashSetOf<Position>()
    private val moveController = find(MoveController::class)
    private var whitePosition = moveController.getWhitePosition()
    private var isMoving = false

    override val root = borderpane {
        center = vbox { group {  } }
        bottom = hbox {
            button("Start") {
                action {
                    if (!isMoving) {
                        fire(MoveOrderEvent)
                        isMoving = true
                    }
                }
            }
        }
    }

    init {
        refresh()
        subscribe<RefreshEvent> { refresh() }
    }

    private fun refresh() {
        whitePosition = moveController.getWhitePosition()
        positionSet.removeAll(positionSet)
        positionSet.addAll(moveController.getBallsPositions())
        root.center.getChildList()?.get(0)?.replaceWith(group {
            rectangle(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT) { fill = Color.GREEN }
            circle(whitePosition.x, whitePosition.y, BALL_RADIUS) { fill = Color.WHITE }
            for (position in positionSet) circle(position.x, position.y, BALL_RADIUS) { fill = Color.RED }
        })
    }
}