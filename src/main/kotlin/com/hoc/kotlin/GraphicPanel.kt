package com.hoc.kotlin

import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel

/**
 * Created by Peter Hoc on 26/04/2018
 */

class GraphicPanel(cols: Int, rows: Int, cellSize: Int) : JPanel() {
    private val board = Board(cols, rows, cellSize)
    var walls: List<Pair<Int, Int>>
        get() = board.walls
        set(value) {
            board.clearWall()
            value.forEach { (x, y) -> board.addWall(x, y) }
        }
    var begin: Point?
        get() = board.begin
        set(value) {
            board.begin = value
        }
    var end: Point?
        get() = board.end
        set(value) {
            board.end = value
        }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        board.paint(g as Graphics2D)
    }

    fun clearAll() = board.clearAll()
    fun clearPath() = board.clearPathOpenedClosed()
    fun clearPathAndWalls() {
        board.clearPathOpenedClosed()
        board.clearWall()
    }

    operator fun get(x: Int, y: Int) = board.getPointAt(x, y)

    fun addWall(x: Int, y: Int) = board.addWall(x, y)
    fun removeWall(x: Int, y: Int) = board.removeWall(x, y)

}
