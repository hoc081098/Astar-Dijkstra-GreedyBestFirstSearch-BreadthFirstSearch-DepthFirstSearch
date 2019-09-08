package com.hoc.kotlin

import java.awt.Color
import java.awt.Graphics2D
import javax.imageio.ImageIO

/**
 * Created by Peter Hoc on 26/04/2018
 */

enum class PointType {
  EMPTY,
  WALL,
  OPEN,
  CLOSED,
  PATH,
}

data class Point(var x: Int, var y: Int, var type: PointType, val cellSize: Int) {
  fun paint(g2d: Graphics2D) {
    g2d.color = when (type) {
      PointType.EMPTY -> return
      PointType.OPEN -> OPEN_COLOR
      PointType.CLOSED -> CLOSED_COLOR
      PointType.WALL -> WALL_COLOR
      PointType.PATH -> {
        g2d.drawImage(pathImage, x * cellSize + 1, y * cellSize + 1, cellSize, cellSize, null)
        return
      }
    }
    g2d.fillRect(x * cellSize + 1, y * cellSize + 1, cellSize, cellSize)
  }


  private companion object {
    val pathImage = ImageIO.read(this::class.java.getResource(PATH_IMAGE_PATH))!!
    val CLOSED_COLOR = Color(255, 138, 128)
    val WALL_COLOR = Color(51)
    val OPEN_COLOR = Color(178, 255, 89)
  }
}
