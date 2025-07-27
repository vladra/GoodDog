import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.vladra.goodDog.dog.Dog
import com.vladra.goodDog.dog.DogManager
import com.vladra.goodDog.utils.PathingUtils
import org.bukkit.entity.Player
import org.bukkit.entity.Wolf
import org.bukkit.plugin.java.JavaPlugin

//a follow/come here behavior

class FollowGoal(
    private val dog: Dog,
    private val plugin: JavaPlugin,

) : Goal<Wolf> {
    private val key: GoalKey<Wolf> =
        GoalKey.of(Wolf::class.java, org.bukkit.NamespacedKey(plugin, "pet_follow"))

    override fun getKey(): GoalKey<Wolf> = key

    override fun getTypes(): java.util.EnumSet<com.destroystokyo.paper.entity.ai.GoalType> =
        java.util.EnumSet.of(com.destroystokyo.paper.entity.ai.GoalType.MOVE)

    override fun shouldActivate(): Boolean {
        return dog.dogEntity.location.distanceSquared(dog.ownerEntity?.location ?: return false) > 4.0
    }

    override fun shouldStayActive(): Boolean {
        return shouldActivate()
    }

    override fun tick() {
        val target = dog.ownerEntity?.location ?: return

        val ignoreHazards = dog.hazardOverrideOnce

        if (!ignoreHazards && !PathingUtils.isPathSafe(dog.dogEntity, target)) {
            val safe = PathingUtils.findSafeNearbyLocation(target, 6.0, dog.dogEntity)
            if (safe != null) {
                dog.dogEntity.pathfinder.moveTo(safe, 1.2)
            } else {
                dog.dogEntity.pathfinder.stopPathfinding()
            }
        } else {
            dog.dogEntity.pathfinder.moveTo(target, 1.2)
            dog.hazardOverrideOnce = false // Use it up
        }
    }

    override fun stop() {
        dog.dogEntity.pathfinder.stopPathfinding()
    }

    override fun start() {
        val owner = dog.ownerEntity
        owner?.sendMessage("Your dog is following!")
    }
}
