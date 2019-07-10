package tech.davidpereira.si

import java.awt.Graphics

class GameLoop {

    private var ship = Ship()
    private var troop = AlienTroop()

    private var shipBombs: MutableList<ShipBomb> = mutableListOf()
    private var alienBombs: MutableList<AlienBomb> = mutableListOf()

    var score = 0
    var gameOver = false

    val lives: Int
        get() = ship.lives

    fun loop(g: Graphics) {
        if (!gameOver) {

            // Ship behaviour
            ship.move()
            ship.show(g)

            // Ship Bombs behaviour
            validShipBombs().forEach {
                it.move()
                it.checkMissed()

                if (it.hit(troop)) score++

                validAlienBombs().forEach { ab -> it.hit(ab) }

                it.show(g)
            }

            // Alien Troop behaviour
            troop.move()
            alienBombs.addAll(troop.shoot())
            troop.show(g)

            // Alien Bombs behaviour
            validAlienBombs().forEach {
                it.move()
                it.checkMissed()

                if (it.hit(ship)) score++

                it.show(g)
            }

            if (ship.lost() || troop.won() || troop.lost()) {
                gameOver = true
            }
        }

        disposeElements()
    }

    private fun validAlienBombs() = alienBombs.filter { !it.disposable }

    private fun validShipBombs() = shipBombs.filter { !it.disposable }

    fun reset() {
        ship = Ship()
        troop = AlienTroop()
        shipBombs = mutableListOf()
        alienBombs = mutableListOf()
        gameOver = false
        score = 0
    }

    private fun disposeElements() {
        shipBombs.removeAll { it.disposable }
        alienBombs.removeAll { it.disposable }
        troop.aliens.removeAll { it.disposable }
    }

    fun lost(): Boolean {
        return ship.lost() || troop.won()
    }

    fun left() {
        ship.left()
    }

    fun right() {
        ship.right()
    }

    fun shoot() {
        shipBombs.add(ship.shoot())
    }

    fun stop() {
        ship.stop()
    }

}