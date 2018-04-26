package com.hoc.kotlin

import com.hoc.kotlin.Point.Type
import java.awt.Color
import java.awt.Graphics2D
import javax.imageio.ImageIO

/**
 * Created by Peter Hoc on 26/04/2018
 */


class Board {
    private val points = Array(COLS) { col ->
        Array(ROWS) { row ->
            Point(col, row, Type.EMPTY)
        }
    }
    var begin: Point? = points[0][0]
        private set
    var end: Point? = points[COLS - 1][ROWS - 1]
        private set
    val walls: MutableList<Point> = mutableListOf()
    private val startImage = ImageIO.read(Board::class.java.getResource(START_IMAGE)).run {
        getSubimage((width - CELL_SIZE) / 2, (height - CELL_SIZE) / 2, CELL_SIZE, CELL_SIZE)
    }
    private val endImage = ImageIO.read(Board::class.java.getResource(END_IMAGE)).run {
        getSubimage((width - CELL_SIZE) / 2, (height - CELL_SIZE) / 2, CELL_SIZE, CELL_SIZE)
    }

    fun clearAll() {
        end = null
        begin = null
        points.forEach { col ->
            col.forEach { it.type = Type.EMPTY }
        }
    }

    fun clearPathOpenedClosed() {
        points.forEach { col ->
            col.forEach {
                if (it !== begin && it !== end) {
                    it.type = Type.EMPTY
                }
            }
        }
    }

    fun paint(g2d: Graphics2D) {
        points.forEachIndexed { i, col ->
            col.forEachIndexed { j, p ->
                g2d.color = Color.darkGray
                g2d.drawRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE)
                p.paint(g2d)
            }
        }
        begin?.run { g2d.drawImage(startImage, x * CELL_SIZE, y * CELL_SIZE, null) }
        end?.run { g2d.drawImage(endImage, x * CELL_SIZE, y * CELL_SIZE, null) }
    }

    operator fun get(x: Int, y: Int): Point {
        require(x in 0 until COLS && y in 0 until ROWS, { "x must be in [0, COLS) and y must be in [0, ROWS)" })
        return points[x][y]
    }

    fun setBegin(x: Int, y: Int) {
        require(x in 0 until COLS && y in 0 until ROWS, { "x must be in [0, COLS) and y must be in [0, ROWS)" })
        points[x][y].takeIf { it.type != Type.WALL }?.let { begin = it }
    }

    fun setEnd(x: Int, y: Int) {
        require(x in 0 until COLS && y in 0 until ROWS, { "x must be in [0, COLS) and y must be in [0, ROWS)" })
        if (points[x][y].type != Type.WALL) {
            end = points[x][y]
        }
    }

    fun addWall(x: Int, y: Int) {
        require(x in 0 until COLS && y in 0 until ROWS, { "x must be in [0, COLS) and y must be in [0, ROWS)" })
        val point = points[x][y]

        when (point) {
            begin -> return
            end -> return
            else -> point.apply { type = Type.WALL }.let { walls += it }
        }
    }

    fun clearWall() = walls.run {
        forEach { it.type = Type.EMPTY }
        clear()
    }

    fun removeWall(x: Int, y: Int) = points[x][y].apply { type = Type.EMPTY }.let { walls -= it }
}
