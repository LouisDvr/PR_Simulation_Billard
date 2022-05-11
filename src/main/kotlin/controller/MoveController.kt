package controller

import model.*
import tornadofx.*

class MoveController: Controller() {

    private val ballSet = hashSetOf<Ball>()
    private val whiteBall: Ball =
        Ball(0, Position(CANVAS_WIDTH / 4, CANVAS_HEIGHT / 2), Position(CANVAS_WIDTH, CANVAS_HEIGHT))

    init {
        val initialPositions = listOf(
            computeInitialPosition(0, true, 0.75 * CANVAS_WIDTH),
            computeInitialPosition(1, true, 0.75 * CANVAS_WIDTH),
            computeInitialPosition(1, false, 0.75 * CANVAS_WIDTH),
            computeInitialPosition(2, true, 0.75 * CANVAS_WIDTH),
            computeInitialPosition(2, false, 0.75 * CANVAS_WIDTH),
            computeInitialPosition(3, true, 0.75 * CANVAS_WIDTH),
            computeInitialPosition(3, false, 0.75 * CANVAS_WIDTH),
            computeInitialPosition(4, true, 0.75 * CANVAS_WIDTH),
            computeInitialPosition(4, false, 0.75 * CANVAS_WIDTH),
            computeInitialPosition(0, true, 0.75 * CANVAS_WIDTH + 0.87 * 2 * BALL_RADIUS),
            computeInitialPosition(1, true, 0.75 * CANVAS_WIDTH + 0.87 * 2 * BALL_RADIUS),
            computeInitialPosition(1, false, 0.75 * CANVAS_WIDTH + 0.87 * 2 * BALL_RADIUS),
            computeInitialPosition(2, true, 0.75 * CANVAS_WIDTH + 0.87 * 2 * BALL_RADIUS),
            computeInitialPosition(2, false, 0.75 * CANVAS_WIDTH + 0.87 * 2 * BALL_RADIUS),
            Position(0.75 * CANVAS_WIDTH + 4 * 0.87 * 2 * BALL_RADIUS, 0.5 * CANVAS_HEIGHT)
        )
        for (i in initialPositions.indices) ballSet.add(Ball(i+1, initialPositions[i], Position(CANVAS_WIDTH, CANVAS_HEIGHT)))

    }

    /**
     * The initial position of the balls are chosen regarding the rules of eight-ball pool
     * distanceFromLeft: the number of ball between the considered ball and the leftest one of the triangle
     */
    private fun computeInitialPosition(distanceFromLeft: Int, top: Boolean, xLeft: Double): Position {
        return Position(
            xLeft + 0.87 * distanceFromLeft * 2 * BALL_RADIUS,
            if (top) 0.5 * CANVAS_HEIGHT - 0.5 * distanceFromLeft * BALL_RADIUS
            else 0.5 * CANVAS_HEIGHT + 0.5 * distanceFromLeft * BALL_RADIUS
        )
    }

    fun getBallsPositions(): HashSet<Position> {
        return HashSet(ballSet.map { ball -> ball.position.copy() })
    }

    fun getWhitePosition(): Position {
        return whiteBall.position.copy()
    }
}