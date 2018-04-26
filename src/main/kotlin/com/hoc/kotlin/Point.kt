package com.hoc.kotlin

import java.awt.Color
import java.awt.Graphics2D

/**
 * Created by Peter Hoc on 26/04/2018
 */

data class Point(var x: Int, var y: Int, var type: Type) {
    fun paint(g2d: Graphics2D) {
        g2d.color = when (type) {
            Type.EMPTY -> return
            Type.OPEN -> OPEN_COLOR
            Type.CLOSED -> CLOSED_COLOR
            Type.PATH -> PATH_COLOR
            Type.WALL -> WALL_COLOR
        }
        g2d.fillRect(x * 20 + 1, y * 20 + 1, 20, 20)
    }

    sealed class Type {
        object EMPTY : Type()
        object WALL : Type()
        object OPEN : Type()
        object CLOSED : Type()
        object PATH : Type()
    }

    companion object {
        private val CLOSED_COLOR = Color(255, 153, 153)
        private val PATH_COLOR = Color(160, 14, 26)
        private val WALL_COLOR = Color(51)
        private val OPEN_COLOR = Color(153, 255, 153)
    }
}
