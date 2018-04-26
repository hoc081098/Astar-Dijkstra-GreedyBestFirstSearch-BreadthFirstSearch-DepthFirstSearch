package com.hoc.kotlin

import java.awt.Font

/**
 * Created by Peter Hoc on 26/04/2018
 */

const val CELL_SIZE = 20
const val COLS = 25
const val ROWS = 25
const val PANEL_WIDTH = CELL_SIZE * COLS
const val PANEL_HEIGHT = CELL_SIZE * ROWS

const val PATH_IMAGE = "/images/path.JPG"
const val START_IMAGE = "/images/start.JPG"
const val END_IMAGE = "/images/end.JPG"

const val ASTAR = "ASTAR"
const val DIJKSTRA = "DIJKSTRA"
const val GREEDY_BEST_FIRST = "GREEDY BEST-FIRST"

const val EUCLIDEAN = "EUCLIDEAN"
const val MANHATTAN = "MANHATTAN"
const val OCTAGONAL = "OCTAGONAL"

private const val FONT_SIZE = 14
private const val FONT_PATH = "/fonts/AndikaNewBasic-R.ttf"

object Constants {
    val FONT: Font = try {
        val resourceAsStream = javaClass.getResourceAsStream(FONT_PATH)
        Font.createFont(Font.TRUETYPE_FONT, resourceAsStream).deriveFont(FONT_SIZE.toFloat())
    } catch (e: Exception) {
        Font("Consolas", Font.PLAIN, FONT_SIZE)
    }
}
