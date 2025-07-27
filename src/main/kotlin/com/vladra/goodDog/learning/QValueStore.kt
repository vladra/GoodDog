package com.vladra.goodDog.learning

class QValueStore<S, A> {
    private val table = mutableMapOf<Pair<S, A>, Double>()

    fun getQValue(state: S, action: A): Double {
        return table[Pair(state, action)] ?: 0.0
    }

    fun setQValue(state: S, action: A, value: Double) {
        table[Pair(state, action)] = value
    }

    fun getBestAction(state: S, actions: List<A>): A {
        return actions.maxByOrNull { getQValue(state, it) } ?: actions.random()
    }

    fun contains(state: S, action: A): Boolean {
        return table.containsKey(Pair(state, action))
    }

    fun serialize(encodeKey: (S, A) -> String): Map<String, Double> {
        return table.mapKeys { (key, _) -> encodeKey(key.first, key.second) }
    }

    fun deserialize(data: Map<String, Double>, decodeKey: (String) -> Pair<S, A>) {
        table.clear()
        for ((k, v) in data) {
            val (s, a) = decodeKey(k)
            table[Pair(s, a)] = v
        }
    }
}
