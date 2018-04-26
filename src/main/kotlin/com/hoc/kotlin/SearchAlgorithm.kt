package com.hoc.kotlin

import com.hoc.kotlin.Point.Type
import com.hoc.kotlin.swing.Swing
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import java.lang.Math.sqrt
import java.util.PriorityQueue
import kotlin.Comparator
import kotlin.math.pow

/**
 * Created by Peter Hoc on 26/04/2018
 */

typealias HeuristicFunction = (point1: Point, point2: Point) -> Double

abstract class SearchAlgorithm(
        private val view: Contract.View,
        var hFunction: HeuristicFunction
) : Contract.Presenter {
    private val nodes = Array(COLS) { col ->
        Array(ROWS) { row -> Node(Point(col, row, Type.EMPTY), 0, 0) }
    }
    var beginNode: Node? = null
    var endNode: Node? = null
    abstract val priority: (n1: Node, n2: Node) -> Int
    var isDiagonalMovement = true
    var isNotCrossCorner = true

    override fun run(begin: Point, end: Point, walls: List<Point>) = launch(CommonPool) {
        nodes.forEach {
            it.forEach {
                it.point.type = Type.EMPTY
            }
        }
        walls.forEach { (x, y) -> nodes[x][y].point.type = Type.WALL }

        val (startX, startY) = begin
        beginNode = nodes[startX][startY]

        val (endX, endY) = end
        endNode = nodes[endX][endY]

        val openSet = PriorityQueue(
                Comparator.nullsLast<Node> { o1, o2 -> priority(o1, o2) }
        ).apply { offer(beginNode) }
        var current: Node = beginNode!!
        var success = false

        while (openSet.isNotEmpty()) {

            var p: Node? = current
            while (p !== null) {
                withContext(Swing) {
                    view.changePointType(p?.point, Type.CLOSED)
                }
                p = p.parent
            }

            current = openSet.poll()
            if (current === endNode) {
                success = true
            } else {
                current.point.type = Type.CLOSED

                for ((dx, dy) in direction.subList(0, if (isDiagonalMovement) 8 else 4)) {
                    val x = current.point.x + dx
                    val y = current.point.y + dy

                    if (x !in 0 until COLS || y !in 0 until ROWS) continue

                    val neighbor = nodes[x][y]
                    val neighborPoint = neighbor.point

                    if (neighborPoint.type === Type.CLOSED
                            || neighborPoint.type == Type.WALL
                            || isNotCrossCorner && isCorner(current, neighbor)) continue


                    val gScore = current.gScore + (K * cost(current, neighbor)).toLong()
                    if (neighborPoint.type != Type.OPEN || gScore < neighbor.gScore) {
                        neighbor.gScore = gScore
                        neighbor.fScore = gScore + (K * hFunction(neighborPoint, endNode!!.point)).toLong()
                        neighbor.parent = current

                        if (neighborPoint.type !== Type.OPEN) {
                            openSet.offer(neighbor)
                            neighborPoint.type = Type.OPEN
                            withContext(Swing) {
                                view.changePointType(neighborPoint, Type.OPEN)
                            }
                        }
                    }
                }

            }

            p = current
            while (p !== null) {
                withContext(Swing) {
                    view.changePointType(p?.point, Type.PATH)
                }
                p = p.parent
            }
            withContext(Swing) {
                view.repaint()
            }
            delay(10)
            if (success) {
                break
            }
        }

        when {
            success -> {
                var totalCost = 0.0

                var p: Node? = current
                while (p?.parent !== null) {
                    totalCost += cost(p, p.parent)
                    p = p.parent
                }

                withContext(Swing) {
                    view.showMessage(String.format("Path's length: %f", totalCost))
                }
            }
            else -> withContext(Swing) {
                view.showMessage("No path")
            }
        }
    }

    fun cost(a: Node, b: Node?): Double {
        val (xA, yA) = a.point
        val (xB, yB) = b?.point ?: Point(0, 0, Type.EMPTY)

        val p = (xA - xB).toDouble().pow(2)
        val q = (yA - yB).toDouble().pow(2)
        return sqrt(p + q)
    }

    fun isCorner(n1: Node, n2: Node): Boolean {
        val (x1, y1) = n1.point
        val (x2, y2) = n2.point

        return nodes[x1][y2].point.type === Type.WALL && nodes[x2][y1].point.type === Type.WALL
    }

    class Node(
            internal val point: Point,
            internal var fScore: Long,
            internal var gScore: Long,
            internal var parent: Node? = null
    )

    companion object {
        val direction = listOf(
                0 to 1,
                0 to -1,
                1 to 0,
                -1 to 0,

                1 to 1,
                1 to -1,
                -1 to -1,
                -1 to 1
        )
        private const val K = 10
    }
}