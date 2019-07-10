package tech.davidpereira.si

import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.lang.Math.abs
import javax.imageio.ImageIO.read
import javax.xml.bind.DatatypeConverter.parseBase64Binary
import kotlin.random.Random

const val alienSize = 35

val sprites = AlienSprites()

class AlienSprites {

    private val spritesSrc = "iVBORw0KGgoAAAANSUhEUgAAAEAAAAEACAYAAAADRnAGAAACGUlEQVR42u3aSQ7CMBAEQIsn8P+/hiviAAK8zFIt5QbELiTHmfEYE3L9mZE9AAAAqAVwBQ8AAAD6THY5CgAAAKbfbPX3AQAAYBEEAADAuZrC6UUyfMEEAIBiAN8OePXnAQAAsLcmmKFPAQAAgHMbm+gbr3Sdo/LtcAAAANR6GywPAgBAM4D2JXAAABoBzBjA7AmlOx8AAEAzAOcDAADovTc4vQim6wUCABAYQG8QAADd4dPd2fRVYQAAANQG0B4HAABAawDnAwAA6AXgfAAAALpA2uMAAABwPgAAgPoAM9Ci/R4AAAD2dmqcEQIAIC/AiQGuAAYAAECcRS/a/cJXkUf2AAAAoBaA3iAAALrD+gIAAADY9baX/nwAAADNADwFAADo9YK0e5FMX/UFACA5QPSNEAAAAHKtCekmDAAAAADvBljtfgAAAGgMMGOrunvCy2uCAAAACFU6BwAAwF6AGQPa/XsAAADYB+B8AAAAtU+ItD4OAwAAAFVhAACaA0T7B44/BQAAANALwGMQAAAAADYO8If2+P31AgAAQN0SWbhFDwCAZlXgaO1xAAAA1FngnA8AACAeQPSNEAAAAM4CnC64AAAA4GzN4N9NSfgKEAAAAACszO26X8/X6BYAAAD0Anid8KcLAAAAAAAAAJBnwNEvAAAA9Jns1ygAAAAAAAAAAAAAAAAAAABAQ4COCENERERERERERBrnAa1sJuUVr3rsAAAAAElFTkSuQmCC"
    private val spritesImg: BufferedImage
    private val alienCoords = arrayOf(
        arrayOf(arrayOf(0, 0, 51, 34), arrayOf(0, 102, 51, 34)),
        arrayOf(arrayOf(0, 137, 50, 33), arrayOf(0, 170, 50, 34)),
        arrayOf(arrayOf(0, 68, 50, 32), arrayOf(0, 34, 50, 32))
    )

    var aliens: Array<Array<BufferedImage>>

    init {
        spritesImg = read(ByteArrayInputStream(parseBase64Binary(spritesSrc)))

        aliens = arrayOf(
            arrayOf(
                spritesImg.getSubimage(
                    alienCoords[0][0][0],
                    alienCoords[0][0][1],
                    alienCoords[0][0][2],
                    alienCoords[0][0][3]
                ),
                spritesImg.getSubimage(
                    alienCoords[0][1][0],
                    alienCoords[0][1][1],
                    alienCoords[0][1][2],
                    alienCoords[0][1][3]
                )
            ),
            arrayOf(
                spritesImg.getSubimage(
                    alienCoords[1][0][0],
                    alienCoords[1][0][1],
                    alienCoords[1][0][2],
                    alienCoords[1][0][3]
                ),
                spritesImg.getSubimage(
                    alienCoords[1][1][0],
                    alienCoords[1][1][1],
                    alienCoords[1][1][2],
                    alienCoords[1][1][3]
                )
            ),
            arrayOf(
                spritesImg.getSubimage(
                    alienCoords[2][0][0],
                    alienCoords[2][0][1],
                    alienCoords[2][0][2],
                    alienCoords[2][0][3]
                ),
                spritesImg.getSubimage(
                    alienCoords[2][1][0],
                    alienCoords[2][1][1],
                    alienCoords[2][1][2],
                    alienCoords[2][1][3]
                )
            )
        )
    }

}

class AlienTroop {

    val aliens: MutableList<Alien> = mutableListOf()

    private val numberOfLines = 5
    private val numberOfAliensInLine = 10
    private val troopVelocity = 3

    private var dx = 0

    init {
        repeat(numberOfLines) { line ->
            aliens.addAll((1..numberOfAliensInLine).map { Alien(xPosition(it), line * -(alienSize * 2), line % 3) })
        }
    }

    private fun xPosition(numberInLine: Int): Int {
        val startPosition = ALIEN_TROOP_PADDING / 2
        val spacing = (GAME_AREA_WIDTH - ALIEN_TROOP_PADDING - (numberOfAliensInLine - 1) * alienSize) / numberOfAliensInLine
        return startPosition + (alienSize + spacing) * (numberInLine - 1)
    }

    fun move() {
        if (validAliens().none { it.hitEdges() }) {
            validAliens().forEach { it.move() }
        } else {
            dx += troopVelocity

            if (abs(dx) >= alienSize) {
                dx = 0
                validAliens().forEach { it.advance() }
            }
        }
    }

    fun show(g: Graphics) {
        validAliens().forEach { it.show(g) }
    }

    fun won(): Boolean {
        return validAliens().any { it.hitFloor() }
    }

    fun lost(): Boolean {
        return validAliens().isEmpty()
    }

    fun shoot(): List<AlienBomb> {
        val alienMap = validAliens().groupBy { it.x }
        return alienMap.keys.map { key -> alienMap[key]?.maxBy { it.y } }.mapNotNull { it?.shoot() }
    }

    private fun validAliens() = aliens.filter { !it.disposable }

}

class Alien(var x: Int, var y: Int, private val type: Int) {

    private val probabilityToShoot = 2

    private var velocity = 2
    private var face = 0
    private var dx = 0

    var disposable = false

    fun move() {
        dx += velocity

        if (abs(dx) >= alienSize) {
            x += dx
            dx = 0
            face = 1 - face
        }
    }

    fun hitEdges(): Boolean {
        return x + dx < 0 || x + dx > GAME_AREA_WIDTH - alienSize
    }

    fun advance() {
        velocity *= -1
        y += alienSize
        dx = 0
        face = 1 - face
    }

    fun hitFloor(): Boolean {
        return y + 2 * alienSize >= GAME_AREA_HEIGHT - HUD_BAR_HEIGHT - alienSize
    }

    fun shoot(): AlienBomb? {
        val random = Random.nextInt(1000)

        if (random < probabilityToShoot) {
            return AlienBomb(x + alienSize / 2, y + alienSize)
        }

        return null
    }

    fun wasHit(x: Int, y: Int): Boolean {
        val result = x >= this.x && x <= this.x + alienSize && y >= this.y && y <= this.y + alienSize
        if (result) disposable = true
        return result
    }

    fun show(g: Graphics) {
        g.drawImage(sprites.aliens[type][face], x, y, alienSize, alienSize, null)
    }

}