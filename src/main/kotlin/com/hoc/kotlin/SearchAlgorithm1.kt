package com.hoc.kotlin

import kotlinx.coroutines.*

/**
 * Created by Peter Hoc on 04/05/2018
 */

abstract class SearchAlgorithm1(
    view: Contract.View,
    val hFunction: HeuristicFunction,
    cols: Int,
    rows: Int
) : AbstractSearchAlgorithm(view, cols, rows) {
  protected abstract val keyExtractor: (Node) -> Long

  override fun run(begin: Point, end: Point, walls: List<Pair<Int, Int>>) = CoroutineScope(Dispatchers.Default).launch {
    val startTime = System.currentTimeMillis()

    initialize(walls, begin, end)
    val beginNode = beginNode!!
    val endNode = endNode!!
    val openSet = mutableListOf(beginNode)
    var current = beginNode
    var countVisited = 0

    while (openSet.isNotEmpty()) {
      withContext(Dispatchers.Main) {
        current.reversedPathIterable.forEach { (p) -> view.changePointType(p.x, p.y, PointType.CLOSED) }
      }

      openSet.sortBy(keyExtractor)
      current = openSet.removeAt(0)
      ++countVisited

      if (current === endNode) {
        withContext(Dispatchers.Main) {
          current.reversedPathIterable.forEach { (p) -> view.changePointType(p.x, p.y, PointType.PATH) }
          view.repaint()
        }
        delay(10)

        val millis = System.currentTimeMillis() - startTime
        return@launch constructPath(current, countVisited, millis)
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

            if (neighborPoint.type !== PointType.OPEN) {
              openSet += neighbor
              neighborPoint.type = PointType.OPEN
              withContext(Dispatchers.Main) { view.changePointType(neighborPoint.x, neighborPoint.y, PointType.OPEN) }
            }
          }

      withContext(Dispatchers.Main) {
        current.reversedPathIterable.forEach { (p) -> view.changePointType(p.x, p.y, PointType.PATH) }
        view.repaint()
      }
      //delay(2)
    }

    onNoPath(countVisited, System.currentTimeMillis() - startTime)
  }
}

class AStar(
    view: Contract.View,
    hFunction: HeuristicFunction,
    cols: Int,
    rows: Int
) : SearchAlgorithm1(view, hFunction, cols, rows) {
  override val keyExtractor = Node::fScore
}

class GreedyBestFirst(
    view: Contract.View,
    hFunction: HeuristicFunction,
    cols: Int,
    rows: Int
) : SearchAlgorithm1(view, hFunction, cols, rows) {
  override val keyExtractor = Node::hScore
}

class Dijkstra(
    view: Contract.View,
    hFunction: HeuristicFunction,
    cols: Int,
    rows: Int
) : SearchAlgorithm1(view, hFunction, cols, rows) {
  override val keyExtractor = Node::gScore
}
