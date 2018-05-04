package com.hoc.kotlin

/**
 * Created by Peter Hoc on 26/04/2018
 */

interface Contract {
    interface View {
        fun repaint()
        fun showMessage(pathInfo: String, visited: String, time: String)
        fun changePointType(x: Int, y: Int, type: PointType)
    }
}