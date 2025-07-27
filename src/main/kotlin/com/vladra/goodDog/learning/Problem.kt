package com.vladra.goodDog.learning

interface Problem<S, A> {
    fun getCurrentState(): S
    fun getAvailableActions(state: S): List<A>
    fun takeAction(state: S, action: A): Pair<Double, S>
}
