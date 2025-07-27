import com.destroystokyo.paper.entity.ai.Goal
import com.vladra.goodDog.dog.Dog
import com.vladra.goodDog.learning.QValueStore
import org.bukkit.entity.Wolf

class QLearningAgent(
    private val dog: Dog,
    private val alpha: Double,
    private val gamma: Double,
    private val rho: Double,
    private val actionSupplier: () -> List<Goal<Wolf>>,
    private val actionExecutor: (Goal<Wolf>) -> Unit
) {
    private val store = QValueStore<Dog, Goal<Wolf>>()
    private var lastState: Dog? = null
    private var lastAction: Goal<Wolf>? = null

    fun step() {
        dog.senseCurrentState()

        val actions = actionSupplier()
        if (actions.isEmpty()) return

        val unexplored = actions.filter { !store.contains(dog, it) }
        val action = when {
            unexplored.isNotEmpty() -> unexplored.random()
            Math.random() < rho     -> actions.random()
            else                    -> store.getBestAction(dog, actions)
        }

        lastAction = action

        actionExecutor(action)

    }

    fun reward(r: Double) {
        val s = lastState ?: return
        val a = lastAction ?: return

        val currentQ = store.getQValue(s, a)
        val updatedQ = (1 - alpha) * currentQ + alpha * r
        store.setQValue(s, a, updatedQ)
    }

    fun getQStore(): QValueStore<Dog, Goal<Wolf>> = store

    fun reset() {
        lastState = null
        lastAction = null
    }

}
