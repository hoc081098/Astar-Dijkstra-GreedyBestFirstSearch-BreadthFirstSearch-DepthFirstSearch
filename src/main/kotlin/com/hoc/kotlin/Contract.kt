package com.hoc.kotlin

import kotlinx.coroutines.experimental.Job

/**
 * Created by Peter Hoc on 26/04/2018
 */

interface Contract {
    interface Presenter {
        fun run(begin: Point, end: Point, walls: List<Point>): Job
    }

    interface View {
        fun repaint()
        fun showMessage(message: String)
        fun changePointType(p: Point?, type: Point.Type)
    }
}