package com.hoc.kotlin

/**
 * Created by Peter Hoc on 26/04/2018
 */

class AStar(view: Contract.View, hFunction: HeuristicFunction) : SearchAlgorithm(view, hFunction) {
    override val priority = { n1: Node, n2: Node ->
        n1.fScore.compareTo(n2.fScore)
    }
}
