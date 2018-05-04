package com.hoc.kotlin

import com.hoc.kotlin.swing.Swing
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import java.util.*

/**
 * Created by Peter Hoc on 04/05/2018
 */

abstract class SearchAlgorithm2(
        view: Contract.View,
        cols: Int,
        rows: Int
) : AbstractSearchAlgorithm(view, cols, rows) {
    abstract class QueueOrStack {
        abstract fun isNotEmpty(): Boolean
        abstract fun pollOrPop(): Node
        abstract operator fun plusAssign(n: Node)
    }

    protected abstract fun newQueueOrStack(): QueueOrStack

    override fun run(begin: Point, end: Point, walls: List<Pair<Int, Int>>) = launch {
        val startTime = System.currentTimeMillis()

        initialize(walls, begin, end)
        val beginNode = beginNode!!
        val endNode = endNode!!

        val openSet = newQueueOrStack().apply { this += beginNode.apply { parent = null } }
        val closedSet = hashSetOf<Node>().apply { this += beginNode }
        var current: Node
        var countVisited = 0

        while (openSet.isNotEmpty()) {
            current = openSet.pollOrPop()
            ++countVisited

            if (current === endNode) {
                withContext(Swing) {
                    current.reversedPathIterable.forEach { (p) -> view.changePointType(p.x, p.y, PointType.PATH) }
                    view.repaint()
                }
                delay(10)

                val millis = System.currentTimeMillis() - startTime
                return@launch constructPath(current, countVisited, millis)
            }

            direction.take(if (allowDiagonalMovement) 8 else 4)
                    .map { (dx, dy) -> current.point.run { x + dx to y + dy } }
                    .filter { (x, y) -> x in 0 until cols && y in 0 until rows }
                    .map { (x, y) -> nodes[x][y] }
                    .filter { n -> n !in closedSet && n.point.type !== PointType.WALL && !(notCrossCorner && isCorner(current, n)) }
                    .forEach { n ->
                        closedSet += n
                        openSet += n
                        n.parent = current

                        withContext(Swing) {
                            view.changePointType(n.point.x, n.point.y, PointType.OPEN)
                        }
                    }

            withContext(Swing) {
                view.changePointType(current.point.x, current.point.y, PointType.CLOSED)
                view.repaint()
            }
        }

        onNoPath(countVisited, System.currentTimeMillis() - startTime)
    }
}

class BFS(view: Contract.View, cols: Int, rows: Int) : SearchAlgorithm2(view, cols, rows) {
    class QueueContainer : QueueOrStack() {
        private val queue: Queue<Node> = LinkedList()

        override fun isNotEmpty() = queue.isNotEmpty()

        override fun pollOrPop(): Node = queue.remove()

        override fun plusAssign(n: Node) {
            queue.add(n)
        }
    }

    override fun newQueueOrStack() = QueueContainer()
}

class DFS(view: Contract.View, cols: Int, rows: Int) : SearchAlgorithm2(view, cols, rows) {
    class StackContainer : QueueOrStack() {
        private val stack = Stack<Node>()

        override fun isNotEmpty() = stack.isNotEmpty()

        override fun pollOrPop(): Node = stack.pop()

        override fun plusAssign(n: Node) {
            stack.push(n)
        }
    }

    override fun newQueueOrStack() = StackContainer()
}