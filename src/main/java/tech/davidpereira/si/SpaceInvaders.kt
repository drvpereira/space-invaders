package tech.davidpereira.si

import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.*


const val GAME_AREA_WIDTH = 800
const val GAME_AREA_HEIGHT = 600
const val HUD_HEIGHT = 50
const val HUD_BAR_HEIGHT = 5
const val TITLE_BAR_HEIGHT = 22

const val ALIEN_TROOP_PADDING = 150
const val FPS = 30

class GameScene : JPanel(), KeyListener, ActionListener {

    private val game = GameLoop()
    private val timer = Timer(1000 / FPS, this)

    private val mainFont = Font.createFont(Font.PLAIN, this.javaClass.getResourceAsStream("/PressStart2P.ttf"))
    private val hudFont = mainFont.deriveFont(22f)
    private val endGameFont = mainFont.deriveFont(48f)

    init {
        background = Color(51, 51, 51)
        isFocusable = true

        addKeyListener(this)
        timer.start()
    }

    override fun paintComponent(g: Graphics) {
        // Draw Ground
        g.color = Color.WHITE
        g.fillRect(0, GAME_AREA_HEIGHT - HUD_HEIGHT, GAME_AREA_WIDTH, HUD_BAR_HEIGHT)

        // Draw Lives and Score
        g.font = hudFont
        g.drawString("Score: ${game.score}", 90, GAME_AREA_HEIGHT - HUD_HEIGHT + 40)
        g.drawString("Lives: ${game.lives}", GAME_AREA_WIDTH - 300, GAME_AREA_HEIGHT - HUD_HEIGHT + 40)

        game.loop(g)

        // Game Over
        if (game.gameOver) {
            g.font = endGameFont

            if (game.lost())
                g.drawString("Game Over", (GAME_AREA_WIDTH - 430) / 2, GAME_AREA_HEIGHT / 2 - 50)
            else
                g.drawString("You Won!", (GAME_AREA_WIDTH - 400) / 2, GAME_AREA_HEIGHT / 2 - 50)

            g.font = hudFont
            g.drawString("Press 'R' to restart", (GAME_AREA_WIDTH - 460) / 2, GAME_AREA_HEIGHT / 2 + 50)
        }
    }

    override fun actionPerformed(e: ActionEvent) {
        repaint()
    }

    override fun keyTyped(e: KeyEvent) {
        if (e.keyChar == 'r' || e.keyChar == 'R') {
            game.reset()
        }
    }

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_LEFT -> game.left()
            KeyEvent.VK_RIGHT -> game.right()
            KeyEvent.VK_SPACE -> game.shoot()
        }
    }

    override fun keyReleased(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT -> game.stop()
        }
    }

}

class SpaceInvaders {

    private val frame = JFrame()

    init {
        frame.title = "Space Invaders"
        frame.isVisible = true
        frame.isResizable = false
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame.layout = null
        frame.contentPane = GameScene()

        frame.setSize(GAME_AREA_WIDTH, GAME_AREA_HEIGHT + TITLE_BAR_HEIGHT)
    }

}

fun constrain(value: Int, min: Int, max: Int): Int {
    return when {
        value < min -> min
        value > max -> max
        else -> value
    }
}

fun main() {
    SwingUtilities.invokeLater { SpaceInvaders() }
}
