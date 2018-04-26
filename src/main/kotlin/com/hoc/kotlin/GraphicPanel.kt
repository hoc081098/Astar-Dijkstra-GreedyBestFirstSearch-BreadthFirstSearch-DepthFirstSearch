package com.hoc.kotlin

import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel

/**
 * Created by Peter Hoc on 26/04/2018
 */
class GraphicPanel : JPanel() {
    private val board = Board()
    var begin: Point?
        get() = board.begin
        set(value) {
            board.setBegin(value?.x ?: -1, value?.y ?: -1)
        }
    var end: Point?
        get() = board.end
        set(value) {
            board.setEnd(value?.x ?: -1, value?.y ?: -1)
        }
    val walls = board.walls

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        board.paint(g as Graphics2D)
    }

    fun clear() {
        board.clearWall()
        //board.clearAll()
        board.clearPathOpenedClosed()
    }

    operator fun get(x: Int, y: Int) = board[x, y]
    fun addWall(x: Int, y: Int) = board.addWall(x, y)
    fun removeWall(x: Int, y: Int) = board.removeWall(x, y)
    fun setStart(x: Int, y: Int) = board.setBegin(x, y)
    fun setEnd(x: Int, y: Int) = board.setEnd(x, y)
    operator fun get(p: Point) = this[p.x, p.y]
}
