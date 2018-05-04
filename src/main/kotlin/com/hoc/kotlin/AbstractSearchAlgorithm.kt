package com.hoc.kotlin

import kotlinx.coroutines.experimental.Job
import java.lang.Math.sqrt
import java.util.*
import kotlin.math.pow

/**
 * Created by Peter Hoc on 26/04/2018
 */

typealias HeuristicFunction = (point1: Point, point2: Point) -> Double

interface ISearchAlgorithm {
    fun run(begin: Point, end: Point, walls: List<Pair<Int, Int>>): Job
}

abstract class AbstractSearchAlgorithm(
        val view: Contract.View,
        val cols: Int,
        val rows: Int
) : ISearchAlgorithm {
    val nodes = Array(cols) { col -> Array(rows) { Node(Point(col, it, PointType.EMPTY, 0)) } }
    var beginNode: Node? = null
    var endNode: Node? = null
    var allowDiagonalMovement = true
    var notCrossCorner = true

    protected fun initialize(walls: List<Pair<Int, Int>>, begin: Point, end: Point) {
        nodes.forEach { it.forEach { it.point.type = PointType.EMPTY } }
        walls.forEach { (x, y) -> nodes[x][y].point.type = PointType.WALL }

        val (startX, startY) = begin
        beginNode = nodes[startX][startY].apply { parent = null }

        val (endX, endY) = end
        endNode = nodes[endX][endY]
    }

    protected fun onNoPath(visitedCount: Int, millis: Long) {
        view.showMessage(
                "No path!",
                "Visited: $visitedCount",
                "Time: %.4fs".format(millis / 1000.0)
        )
    }

    protected fun constructPath(current: Node?, countVisited: Int, millis: Long) {
        val totalCost = (current ?: throw IllegalStateException("Current node is null"))
                .reversedPathIterable
                .zipWithNext(::distance)
                .sum()
        view.showMessage(
                "Path's length: %.4f".format(totalCost),
                "Visited: $countVisited",
                "Time: %.4fs".format(millis / 1000.0)
        )
    }

    protected fun distance(a: Node, b: Node): Double {
        val (xA, yA) = a.point
        val (xB, yB) = b.point
        val p = (xA - xB).toDouble().pow(2)
        val q = (yA - yB).toDouble().pow(2)
        return sqrt(p + q)
    }

    protected fun isCorner(n1: Node, n2: Node): Boolean {
        val (x1, y1) = n1.point
        val (x2, y2) = n2.point
        return nodes[x1][y2].point.type === PointType.WALL && nodes[x2][y1].point.type === PointType.WALL
    }

    data class Node(
            val point: Point,
            var fScore: Long = 0L,
            var gScore: Long = 0L,
            var hScore: Long = 0L,
            var parent: Node? = null
    ) {
        val reversedPathIterable: Sequence<Node> = Sequence {
            object : Iterator<Node> {
                private var p: Node? = this@Node

                override fun hasNext() = p !== null

                override fun next(): Node {
                    val next = p
                    p = p?.parent
                    return next ?: throw NoSuchElementException()
                }
            }
        }

        override fun equals(other: Any?) = when {
            this === other -> true
            other !is Node -> false
            point != other.point -> false
            else -> true
        }

        override fun hashCode() = point.hashCode()
    }

    protected companion object {
        @JvmStatic
        val direction = sequenceOf(
                0 to 1,
                0 to -1,
                1 to 0,
                -1 to 0,

                1 to 1,
                1 to -1,
                -1 to -1,
                -1 to 1
        )
        const val K = 10
    }
}