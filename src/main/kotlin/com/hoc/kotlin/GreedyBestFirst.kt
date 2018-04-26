package com.hoc.kotlin

/**
 * Created by Peter Hoc on 26/04/2018
 */

class GreedyBestFirst(view: Contract.View, hFunction: HeuristicFunction) : SearchAlgorithm(view, hFunction) {
    override val priority = { n1: Node, n2: Node ->
        val endPoint = endNode!!.point
        val hScore1 = 10L * hFunction(n1.point, endPoint)
        val hScore2 = 10L * hFunction(n2.point, endPoint)
        hScore1.compareTo(hScore2)
    }
}
