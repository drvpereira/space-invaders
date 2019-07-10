package tech.davidpereira.si

import java.awt.Color.GREEN
import java.awt.Color.RED
import java.awt.Graphics

class ShipBomb(private val x: Int, private var y: Int) {

    private val width = 3
    private val height = 12
    private val dy = 5

    var disposable = false

    fun show(g: Graphics) {
        g.color = RED
        g.fillRect(x - width / 2, y - height, width, height)
    }

    fun move() {
        y -= dy
    }

    fun checkMissed() {
        if (y < 0) disposable = true
    }

    fun hit(troop: AlienTroop): Boolean {
        val result = troop.aliens.any { it.wasHit(x, y) }
        if (result) disposable = true
        return result
    }

    fun hit(bomb: AlienBomb): Boolean {
        val result = bomb.wasHit(x, y)
        if (result) disposable = true
        return result
    }

}

class AlienBomb(private val x: Int, private var y: Int) {

    private val diameter = 10
    private val dy = 5
    private val hitBox = 3

    var disposable = false

    fun show(g: Graphics) {
        g.color = GREEN
        g.fillOval(x - diameter / 2, y, diameter, diameter)
    }

    fun move() {
        y += dy
    }

    fun checkMissed() {
        if (y >= GAME_AREA_HEIGHT - HUD_HEIGHT - HUD_BAR_HEIGHT) disposable = true
    }

    fun hit(ship: Ship): Boolean {
        val result = ship.wasHit(x, y)
        if (result) disposable = true
        return result
    }

    fun wasHit(x: Int, y: Int): Boolean {
        val result = x >= this.x - hitBox && x <= this.x + diameter + hitBox && y >= this.y - hitBox && y <= this.y + diameter + hitBox
        if (result) disposable = true
        return result
    }

}