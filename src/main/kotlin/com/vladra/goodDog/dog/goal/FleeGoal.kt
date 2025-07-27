import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.vladra.goodDog.dog.Dog
import com.vladra.goodDog.utils.PathingUtils
import org.bukkit.NamespacedKey
import org.bukkit.entity.Wolf
import org.bukkit.plugin.java.JavaPlugin

class FleeGoal(
    private val dog: Dog,
    private val plugin: JavaPlugin,

    ) : Goal<Wolf> {

    private val key: GoalKey<Wolf> =
        GoalKey.of(Wolf::class.java, NamespacedKey(plugin, "pet_flee"))

    override fun getKey(): GoalKey<Wolf> = key

    override fun getTypes(): java.util.EnumSet<com.destroystokyo.paper.entity.ai.GoalType> =
        java.util.EnumSet.of(com.destroystokyo.paper.entity.ai.GoalType.MOVE)

    override fun shouldActivate(): Boolean {
        return true
    }

    override fun shouldStayActive(): Boolean {
        return shouldActivate()
    }

    override fun start() {
        val owner = dog.ownerEntity
        owner?.sendMessage("Your dog is fleeing!")
    }

    override fun tick() {
        val threat = dog.threat ?: return
        val threatLoc = threat.location
        val wolfLoc = dog.dogEntity.location

        // Direction away from threat
        val fleeVector = wolfLoc.toVector().subtract(threatLoc.toVector()).normalize()

        // Flee harder if threat is close
        val distance = wolfLoc.distance(threatLoc)
        val fleeDistance = if (distance < 4) 12.0 else 8.0

        val target = wolfLoc.clone().add(fleeVector.multiply(fleeDistance))

        if (!PathingUtils.isPathSafe(dog.dogEntity, target)) {
            val safe = PathingUtils.findSafeNearbyLocation(target, 10.0, dog.dogEntity)
            if (safe != null) {
                dog.dogEntity.pathfinder.moveTo(safe, 1.2)
            } else {
                dog.dogEntity.pathfinder.stopPathfinding()
            }
        }
    }

    override fun stop() {
        dog.dogEntity.pathfinder.stopPathfinding()
    }
}
