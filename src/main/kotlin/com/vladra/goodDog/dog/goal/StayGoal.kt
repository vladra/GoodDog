import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.vladra.goodDog.dog.Dog
import com.vladra.goodDog.dog.DogManager

import com.vladra.goodDog.utils.PathingUtils

import org.bukkit.entity.Player
import org.bukkit.entity.Wolf
import org.bukkit.plugin.java.JavaPlugin
import kotlin.random.Random


//dogs stay in their general area

class StayGoal(
    private val dog: Dog,
    private val plugin: JavaPlugin,

    ) : Goal<Wolf> {

    private val key: GoalKey<Wolf> =
        GoalKey.of(Wolf::class.java, org.bukkit.NamespacedKey(plugin, "pet_stay"))
    private var stayLocation = dog.dogEntity.location.clone()
    private val wanderRadius = 3.0

    override fun getKey(): GoalKey<Wolf> = key

    override fun getTypes(): java.util.EnumSet<com.destroystokyo.paper.entity.ai.GoalType> =
        java.util.EnumSet.of(com.destroystokyo.paper.entity.ai.GoalType.MOVE)

    override fun shouldActivate(): Boolean {
        return true
    }

    override fun shouldStayActive(): Boolean {
        return shouldActivate()
    }

    override fun tick() {
        if (Random.nextDouble() < 0.01) { //random chance to go somewhere else

            val offsetX = Random.nextDouble(-wanderRadius, wanderRadius)
            val offsetZ = Random.nextDouble(-wanderRadius, wanderRadius)
            val target = stayLocation.clone().add(offsetX, 0.0, offsetZ)

            // adjust y to be correct
            target.y = target.world.getHighestBlockAt(target).location.y + 1.0

            val heightDiff = target.y - dog.dogEntity.location.y
            if (heightDiff > 1.5 || heightDiff < -2.0) return  // skip extreme elevation jumps

            // check if safe
            if (!PathingUtils.isPathSafe(dog.dogEntity, target)) {
                val safe = PathingUtils.findSafeNearbyLocation(target, 3.0, dog.dogEntity)
                if (safe != null) {
                    dog.dogEntity.pathfinder.moveTo(safe, 1.2)
                } else {
                    dog.dogEntity.pathfinder.stopPathfinding()
                }
                return
            }

            // move if safe
            dog.dogEntity.pathfinder.moveTo(target, 1.2)
        }
    }


    override fun start(){
        stayLocation = dog.dogEntity.location.clone()
        val owner = dog.ownerEntity
        owner?.sendMessage("Your dog is staying!")
    }

    override fun stop() {
        dog.dogEntity.pathfinder.stopPathfinding()
    }
}
