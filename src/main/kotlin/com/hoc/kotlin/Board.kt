package com.hoc.kotlin

import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * Created by Peter Hoc on 26/04/2018
 */

class Board(private val cols: Int, private val rows: Int, private val cellSize: Int) {
    private val points = Array(cols) { col -> Array(rows) { Point(col, it, PointType.EMPTY, cellSize) } }
    var begin: Point? = points[0][0]
        set(value) {
            if (value === null) {
                field = null
                return
            }
            val (x, y) = value
            checkIndex(x, y) || return
            points[x][y].takeIf { it.type != PointType.WALL }?.let { field = it }
        }
    var end: Point? = points[cols - 1][rows - 1]
        set(value) {
            if (value === null) {
                field = null
                return
            }
            val (x, y) = value
            checkIndex(x, y) || return
            points[x][y].takeIf { it.type != PointType.WALL }?.let { field = it }
        }
    val walls: MutableList<Point> = mutableListOf()
    private val startImage: BufferedImage = ImageIO.read(Board::class.java.getResource(START_IMAGE_PATH))
    private val endImage: BufferedImage = ImageIO.read(Board::class.java.getResource(END_IMAGE_PATH))

    fun clearAll() {
        end = null
        begin = null
        points.forEach { col -> col.forEach { it.type = PointType.EMPTY } }
        walls.clear()
    }

    fun clearPathOpenedClosed() {
        points.forEach {
            it.forEach {
                if (it.type === PointType.PATH || it.type == PointType.CLOSED || it.type == PointType.OPEN) {
                    it.type = PointType.EMPTY
                }
            }
        }
    }

    fun paint(g2d: Graphics2D) {
        points.forEachIndexed { i, col ->
            col.forEachIndexed { j, p ->
                g2d.color = Color.darkGray
                g2d.drawRect(i * cellSize, j * cellSize, cellSize, cellSize)
                p.paint(g2d)
            }
        }
        begin?.run { g2d.drawImage(startImage, x * cellSize, y * cellSize, cellSize, cellSize, null) }
        end?.run { g2d.drawImage(endImage, x * cellSize, y * cellSize, cellSize, cellSize, null) }
    }

    fun getPointAt(x: Int, y: Int): Point {
        checkIndex(x, y) || throw IllegalArgumentException()
        return points[x][y]
    }

    fun addWall(x: Int, y: Int) {
        checkIndex(x, y) || return
        when (points[x][y]) {
            begin -> return
            end -> return
            else -> points[x][y].apply { type = PointType.WALL }.let { walls += it }
        }
    }

    private fun checkIndex(x: Int, y: Int) = x in 0 until cols && y in 0 until rows

    fun clearWall() = walls.onEach { it.type = PointType.EMPTY }.clear()

    fun removeWall(x: Int, y: Int) = points[x][y].apply { type = PointType.EMPTY }.let { walls -= it }
}
