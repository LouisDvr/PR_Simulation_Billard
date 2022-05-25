package controller

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.*
import model.events.MouseClickedEvent
import model.events.MoveOrderEvent
import model.events.RefreshEvent
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
            ballPairs.add(Pair(whiteBall, b1))
            for (b2 in ballSet) {
                if (b1 != b2 && !ballPairs.contains(Pair(b2, b1))) {
                    ballPairs.add(Pair(b1, b2))
                }
            }
        }

        subscribe<MoveOrderEvent> { startMoving() }
        subscribe<MouseClickedEvent> { event ->
            handleClick(event.xMouse, event.yMouse)
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

    private fun startMoving() {
        runBlocking {
            launch {
                while (true) {
                    delay(20)
                    checkCollisions()
                }
            }
            launch {
                while (true) {
                    delay(100)
                    fire(RefreshEvent)
                }
            }
            launch {
                whiteBall.move()
            }
            for (ball in ballSet) {
                launch {
                    ball.move()
                }
            }
        }
    }

    private fun handleClick(x: Double, y: Double) {
        if (whiteBall.x - BALL_RADIUS < x && x < whiteBall.x + BALL_RADIUS
            && whiteBall.y - BALL_RADIUS < y && y < whiteBall.y + BALL_RADIUS) {
            val uvx = (whiteBall.x - x) * WHITE_BOOST
            val uvy = (whiteBall.y - y) * WHITE_BOOST
            whiteBall.changeVelocity(whiteBall.vx + uvx, whiteBall.vy + uvy)
        }
    }

    fun getBallsPositions(): HashSet<Position> {
        return HashSet(ballSet.map { ball -> Position(ball.x, ball.y) })
    }

    fun getWhitePosition(): Position {
        return Position(whiteBall.x, whiteBall.y)
    }

    private fun checkCollisions() {
        checkWallCollision(whiteBall)
        for (ball in ballSet) {
            checkWallCollision(ball)
        }
        for ((b1, b2) in ballPairs) {
            if (areColliding(b1, b2)) {
                collide(b1, b2)
            }
        }
    }

    private fun checkWallCollision(ball: Ball) {
        if (ball.y - BALL_RADIUS <= 0 && ball.vy <=0
            || ball.y + BALL_RADIUS >= CANVAS_HEIGHT && ball.vy >= 0) {
            ball.changeVelocity(ball.vx, - ball.vy)
        }
        if (ball.x - BALL_RADIUS <= 0 && ball.vx <= 0
            || ball.x + BALL_RADIUS >= CANVAS_WIDTH && ball.vx >= 0) {
            ball.changeVelocity(- ball.vx, ball.vy)
        }
    }

    private fun areColliding(b1: Ball, b2: Ball): Boolean {
        val dx = b1.x - b2.x
        val dy = b1.y - b2.y
        // check the distance less than 2*r
        // check the distance less than 2*r
        if (dx * dx + dy * dy < 4 * BALL_RADIUS * BALL_RADIUS) {
            val dvx = b1.vx - b2.vx
            val dvy = b1.vy - b2.vy
            // check the balls are moving towards
            if (dx * dvx + dy * dvy <= 0) return true
        }
        return false
    }
    private fun collide(b1: Ball, b2: Ball) {
        val dx = b1.x - b2.x
        val dy = b1.y - b2.y
        val dvx = b1.vx - b2.vx
        val dvy = b1.vy - b2.vy
        val k = (dvx * dx + dvy * dy) / (dx * dx + dy * dy)
        val uvx: Double = dx * k * IMPACT_ENERGY_KEPT
        val uvy: Double = dy * k * IMPACT_ENERGY_KEPT
        b1.vx -= uvx
        b1.vy -= uvy
        b2.vx += uvx
        b2.vy += uvy
    }
}