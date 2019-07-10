package tech.davidpereira.si

import java.awt.Color.WHITE
import java.awt.Graphics

class Ship {

    private val baseWidth = 80
    private val baseHeight = 10
    private val cannonWidth = 15
    private val cannonHeight = 10
    private val velocity = 15
    private val hitBox = 3

    private val baseY = GAME_AREA_HEIGHT - HUD_HEIGHT - baseHeight
    private val middleY = GAME_AREA_HEIGHT - HUD_HEIGHT - 2 * baseHeight
    private val cannonX = (baseWidth - cannonWidth) / 2

    private var x = (GAME_AREA_WIDTH - baseWidth) / 2
    private var dx = 0

    var lives = 3

    fun shoot(): ShipBomb {
        return ShipBomb(x + (baseWidth / 2), GAME_AREA_HEIGHT - HUD_HEIGHT - (2 * baseHeight + cannonHeight))
    }

    fun move() {
        x = constrain(x + dx, 0, GAME_AREA_WIDTH - baseWidth)
    }

    fun left() {
        dx = -velocity
    }

    fun right() {
        dx = velocity
    }

    fun stop() {
        dx = 0
    }

    fun lost(): Boolean {
        return lives <= 0
    }

    fun show(g: Graphics) {
        g.color = WHITE

        g.fillRect(x, baseY, baseWidth, baseHeight)
        g.fillRect(x + 10, middleY, baseWidth - 20, baseHeight)
        g.fillRect(x + cannonX,middleY - cannonHeight, cannonWidth, cannonHeight)
    }

    fun wasHit(x: Int, y: Int): Boolean {
        val hitBase = x >= this.x - hitBox && x <= this.x + baseWidth + hitBox && y >= baseY - hitBox && y <= baseY + baseHeight + hitBox
        val hitMiddle = x >= this.x + 10 - hitBox && x <= this.x + baseWidth - 10 + hitBox && y >= middleY - hitBox && y <= baseY + hitBox
        val hitCannon = x >= this.x + cannonX - hitBox && x <= this.x + cannonX + cannonWidth + hitBox && y >= middleY - cannonHeight - hitBox && y <= middleY + hitBox

        val result = hitBase || hitMiddle || hitCannon

        if (result) lives -= 1

        return result
    }

}