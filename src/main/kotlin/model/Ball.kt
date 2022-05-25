package model

import kotlinx.coroutines.delay

class Ball(val id: Int, var x: Double, var y: Double) {

    var vx: Double = 0.0
    var vy: Double = 0.0

    suspend fun move() {
        while (true) {
            delay(100)

            vx -= D * vx / MASS
            vy -= D * vy / MASS

            x += vx
            y += vy
        }
    }

    fun changeVelocity(newVx: Double, newVy: Double) {
        vx = newVx
        vy = newVy
    }

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other?.javaClass && id == (other as Ball).id
    }

    override fun hashCode(): Int {
        return id
    }
}