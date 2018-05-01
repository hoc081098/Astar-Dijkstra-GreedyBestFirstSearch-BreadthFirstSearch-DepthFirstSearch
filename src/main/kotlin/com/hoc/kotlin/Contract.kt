package com.hoc.kotlin

import kotlinx.coroutines.experimental.Job

/**
 * Created by Peter Hoc on 26/04/2018
 */

interface Contract {
    interface Controller {
        fun run(begin: Point, end: Point, walls: List<Point>): Job
    }

    interface View {
        fun repaint()
        fun showMessage(message: String)
        fun changePointType(x: Int, y: Int, type: PointType)
    }
}