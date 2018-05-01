package com.hoc.kotlin

import com.hoc.kotlin.swing.Swing
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import java.lang.Math.sqrt
import java.util.PriorityQueue
import kotlin.NoSuchElementException
import kotlin.math.pow

/**
 * Created by Peter Hoc on 26/04/2018
 */

typealias HeuristicFunction = (point1: Point, point2: Point) -> Double

abstract class SearchAlgorithm(
        private val view: Contract.View,
        var hFunction: HeuristicFunction,
        val cols: Int,
        val rows: Int
) : Contract.Controller {
    private val nodes = Array(cols) { col -> Array(rows) { Node(Point(col, it, PointType.EMPTY, 0)) } }
    private var beginNode: Node? = null
    private var endNode: Node? = null
    var allowDiagonalMovement = true
    var notCrossCorner = true
    abstract val keyExtractor: (Node) -> Long

    override fun run(begin: Point, end: Point, walls: List<Point>) = launch(CommonPool) {
        initialize(walls, begin, end)
        val beginNode = beginNode!!
        val endNode = endNode!!
        val openSet = PriorityQueue(compareBy(keyExtractor)).apply { offer(beginNode) }
        var current = beginNode

        while (openSet.isNotEmpty()) {
            withContext(Swing) {
                current.reversedPathIterable.forEach { (p) -> view.changePointType(p.x, p.y, PointType.CLOSED) }
            }

            current = openSet.poll()

            if (current === endNode) {
                withContext(Swing) {
                    current.reversedPathIterable.forEach { (p) -> view.changePointType(p.x, p.y, PointType.PATH) }
                    view.repaint()
                }
                delay(10)
                return@launch constructPath(current)
            }
            current.point.type = PointType.CLOSED

            direction.take(if (allowDiagonalMovement) 8 else 4)
                    .map { (dx, dy) -> current.point.run { x + dx to y + dy } }
                    .filter { (x, y) -> x in 0 until cols && y in 0 until rows }
                    .mapNotNull { (x, y) ->
                        val neighbor = nodes[x][y]
                        val neighborPoint = neighbor.point

                        when {
                            neighborPoint.type === PointType.CLOSED
                                    || neighborPoint.type === PointType.WALL
                                    || notCrossCorner && isCorner(current, neighbor) -> null
                            else -> {
                                val newGScore = current.gScore + (K * distance(current, neighbor)).toLong()
                                if (neighborPoint.type !== PointType.OPEN || newGScore < neighbor.gScore) {
                                    Triple(neighbor, neighborPoint, newGScore)
                                } else null
                            }
                        }
                    }
                    .forEach { (neighbor, neighborPoint, newGScore) ->
                        neighbor.run {
                            gScore = newGScore
                            hScore = (K * hFunction(neighborPoint, endNode.point)).toLong()
                            fScore = newGScore + hScore
                            parent = current
                        }

                        neighborPoint.run {
                            if (type !== PointType.OPEN) {
                                openSet.offer(neighbor)
                                type = PointType.OPEN
                                withContext(Swing) { view.changePointType(x, y, PointType.OPEN) }
                            }
                        }
                    }

            withContext(Swing) {
                current.reversedPathIterable.forEach { (p) -> view.changePointType(p.x, p.y, PointType.PATH) }
                view.repaint()
            }
            delay(10)
        }

        view.showMessage("No path!")
    }

    private fun initialize(walls: List<Point>, begin: Point, end: Point) {
        nodes.forEach { it.forEach { it.point.type = PointType.EMPTY } }
        walls.forEach { (x, y) -> nodes[x][y].point.type = PointType.WALL }

        val (startX, startY) = begin
        beginNode = nodes[startX][startY]

        val (endX, endY) = end
        endNode = nodes[endX][endY]
    }

    private fun constructPath(current: Node?) {
        if (current === null) return
        val totalCost = current.reversedPathIterable
                .zipWithNext(::distance)
                .sum()
        view.showMessage("Path's length: %.4f".format(totalCost))
    }

    private fun distance(a: Node, b: Node): Double {
        val (xA, yA) = a.point
        val (xB, yB) = b.point
        val p = (xA - xB).toDouble().pow(2)
        val q = (yA - yB).toDouble().pow(2)
        return sqrt(p + q)
    }

    private fun isCorner(n1: Node, n2: Node): Boolean {
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
    }

    private companion object {
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