package com.vladra.goodDog.dog.goal

import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.vladra.goodDog.dog.Dog
import com.vladra.goodDog.utils.PathingUtils
import org.bukkit.entity.Wolf
import org.bukkit.plugin.java.JavaPlugin

class AttackGoal(
    private val dog: Dog,
    private val plugin: JavaPlugin,

    ) : Goal<Wolf> {
    private val key: GoalKey<Wolf> =
        GoalKey.of(Wolf::class.java, org.bukkit.NamespacedKey(plugin, "pet_follow"))

    override fun getKey(): GoalKey<Wolf> = key

    override fun getTypes(): java.util.EnumSet<com.destroystokyo.paper.entity.ai.GoalType> =
        java.util.EnumSet.of(com.destroystokyo.paper.entity.ai.GoalType.MOVE)

    override fun shouldActivate(): Boolean {

        val attackTarget = dog.attackTarget ?: return false
        if (attackTarget.isDead) {
            dog.dogEntity.target = null
            dog.dogEntity.pathfinder.stopPathfinding()
            return false
        }
        return  dog.dogEntity.location.distanceSquared(
            dog.ownerEntity?.location ?: return false) > 15.0
    }

    override fun shouldStayActive(): Boolean {
        return shouldActivate()
    }

    override fun tick() {
        val attackTarget = dog.attackTarget?: return
        val ignoreHazards = dog.hazardOverrideOnce

        if (!ignoreHazards && !PathingUtils.isPathSafe(dog.dogEntity,
                attackTarget.location)) {
            val safe = PathingUtils.findSafeNearbyLocation(attackTarget.location,
                6.0, dog.dogEntity)
            if (safe != null) {
                dog.dogEntity.pathfinder.moveTo(safe, 1.2)
                dog.dogEntity.target = attackTarget
            } else {
                dog.dogEntity.pathfinder.stopPathfinding()
                dog.dogEntity.target = null
            }
        } else {
            dog.dogEntity.pathfinder.moveTo(attackTarget, 1.2)
            dog.dogEntity.target = attackTarget
            dog.hazardOverrideOnce = false // Use it up
        }
    }

    override fun stop() {
        dog.dogEntity.pathfinder.stopPathfinding()
    }

    override fun start() {
        val owner = dog.ownerEntity
        owner?.sendMessage("Your dog is attacking!")
    }
}
