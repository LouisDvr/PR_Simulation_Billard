package controller

import model.*
import tornadofx.*

class MoveController: Controller() {

    private val ballSet = hashSetOf<Ball>()
    private val ballPairs = mutableListOf<Pair<Ball, Ball>>()
    private val whiteBall: Ball =
        Ball(0,CANVAS_WIDTH / 4, CANVAS_HEIGHT / 2)

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
            computeInitialPosition(0, true, 0.75 * CANVAS_WIDTH + 0.87 * 4 * BALL_RADIUS),
            computeInitialPosition(1, true, 0.75 * CANVAS_WIDTH + 0.87 * 4 * BALL_RADIUS),
            computeInitialPosition(1, false, 0.75 * CANVAS_WIDTH + 0.87 * 4 * BALL_RADIUS),
            computeInitialPosition(2, true, 0.75 * CANVAS_WIDTH + 0.87 * 4 * BALL_RADIUS),
            computeInitialPosition(2, false, 0.75 * CANVAS_WIDTH + 0.87 * 4 * BALL_RADIUS),
            Position(0.75 * CANVAS_WIDTH + 4 * 0.87 * 2 * BALL_RADIUS, 0.5 * CANVAS_HEIGHT)
        )
        for (i in initialPositions.indices) ballSet.add(Ball(i+1, initialPositions[i].x, initialPositions[i].y))
        for (b1 in ballSet) {
            for (b2 in ballSet) {
                if (b1 != b2 && !ballPairs.contains(Pair(b2, b1))) {
                    ballPairs.add(Pair(b1, b2))
                }
            }
        }
    }

    /**
     * The initial position of the balls are chosen regarding the rules of eight-ball pool
     * distanceFromLeft: the number of ball between the considered ball and the leftest one of the triangle
     */
    private fun computeInitialPosition(distanceFromLeft: Int, top: Boolean, xLeft: Double): Position {
        return Position(
            xLeft + 0.87 * distanceFromLeft * 2 * BALL_RADIUS,
            if (top) 0.5 * CANVAS_HEIGHT - distanceFromLeft * BALL_RADIUS
            else 0.5 * CANVAS_HEIGHT + distanceFromLeft * BALL_RADIUS
        )
    }

    fun getBallsPositions(): HashSet<Position> {
        return HashSet(ballSet.map { ball -> Position(ball.x, ball.y) })
    }

    fun getWhitePosition(): Position {
        return Position(whiteBall.x, whiteBall.y)
    }

    fun checkCollisions() {
        for (ball in ballSet) {
            if (ball.y - BALL_RADIUS <= 0 || ball.y + BALL_RADIUS >= CANVAS_HEIGHT) {
                ball.changeVelocity(ball.vx, - ball.vy)
            }
            if (ball.x - BALL_RADIUS <= 0 || ball.x + BALL_RADIUS >= CANVAS_WIDTH) {
                ball.changeVelocity(- ball.vx, ball.vy)
            }
        }
        for ((b1, b2) in ballPairs) {
            if (b1.distanceTo(b2) < 2 * BALL_RADIUS) {
                b1.changeVelocity(b1.vx, b1.vy) // TODO calculate based on slide 39
                b2.changeVelocity(b2.vx, b1.vy)
            }
        }
    }
}