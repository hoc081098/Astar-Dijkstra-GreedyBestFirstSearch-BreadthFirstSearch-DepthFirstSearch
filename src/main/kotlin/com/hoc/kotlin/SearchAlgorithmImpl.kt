package com.hoc.kotlin

/**
 * Created by Peter Hoc on 01/05/2018
 */

class AStar(
        view: Contract.View,
        hFunction: HeuristicFunction,
        cols: Int,
        rows: Int
) : SearchAlgorithm(view, hFunction, cols, rows) {
    override val keyExtractor = Node::fScore
}

class GreedyBestFirst(
        view: Contract.View,
        hFunction: HeuristicFunction,
        cols: Int,
        rows: Int
) : SearchAlgorithm(view, hFunction, cols, rows) {
    override val keyExtractor: (Node) -> Long = Node::hScore
}

class Dijkstra(
        view: Contract.View,
        hFunction: HeuristicFunction,
        cols: Int,
        rows: Int
) : SearchAlgorithm(view, hFunction, cols, rows) {
    override val keyExtractor = Node::gScore
}
