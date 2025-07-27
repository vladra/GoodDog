package com.vladra.goodDog.dog


import FollowGoal
import StayGoal
import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.destroystokyo.paper.entity.ai.MobGoals
import com.vladra.goodDog.GoodDog
import com.vladra.goodDog.listener.CombatListener
import org.bukkit.Bukkit
import java.util.UUID
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.entity.Wolf


class Dog(
    val uuid: UUID,     // unique dog id (same as wolf's uuid)
    val ownerUUID: UUID,
    val dogEntity: Wolf,
    var combatListener: CombatListener,
    val ownerEntity: Player? = null,
    var name: String? = null  // name
) {
    //runtime states
    var threat: LivingEntity? = null
    var attackTarget: LivingEntity? = null
    var hazardOverrideOnce : Boolean = false

    //info used in decision-making. initialized fields
    var currentGoal: Goal<Wolf>? = null

    var dogHealth: Double = dogEntity.health
    var playerHealth: Double? = null
    var playerNearby = false
    var playerInDanger = false
    var seesPassiveMob = false
    var seesHostileMob = false
    var nearbyHostileCount = 0
    var detectsCreature = false

    fun getCurrentGoalList() : Collection<Goal<Mob>> {
        val mobGoals = Bukkit.getMobGoals()
        return mobGoals.getRunningGoals(dogEntity as Mob)

    }

    fun addGoal(goal: Goal<Wolf>, priority: Int ) {
        if (currentGoal == goal) return
        val mobGoals = Bukkit.getMobGoals()
        @Suppress("UNCHECKED_CAST")
        mobGoals.addGoal(dogEntity as Mob, priority, goal as Goal<Mob>)
    }

    fun removeGoal(goalKey: GoalKey<Wolf>) {
        val mobGoals = Bukkit.getMobGoals()
        @Suppress("UNCHECKED_CAST")

        val bool = mobGoals.hasGoal(dogEntity as Mob, goalKey as GoalKey<Mob>)
        if (!bool) return
        mobGoals.removeGoal(dogEntity, goalKey as GoalKey<Mob>)
    }

    fun clearGoals() {
        val mobGoals: MobGoals = Bukkit.getMobGoals()
        mobGoals.removeAllGoals(dogEntity as Mob)
    }

    fun senseCurrentState() {
        val world = dogEntity.world
        val nearbyEntities = world.getNearbyEntities(dogEntity.location, 10.0, 10.0, 10.0)

        val hostiles = nearbyEntities.filterIsInstance<Monster>()
        val passives = nearbyEntities.filter { it is LivingEntity && it !is Monster }

        dogHealth = dogEntity.health
        playerHealth = ownerEntity?.health

        if (ownerEntity == null) {
            playerNearby = false
            playerInDanger = false
        } else {
            playerNearby = dogEntity.location.distanceSquared(ownerEntity.location ) < 100
            playerInDanger = (combatListener.isInCombat(ownerEntity))
        }

        //todo: use ray casting
        seesPassiveMob = passives.isNotEmpty()
        seesHostileMob = hostiles.isNotEmpty()

        nearbyHostileCount = hostiles.size
        val detectedEntities = world.getNearbyEntities(dogEntity.location, 50.0, 50.0, 50.0)
        detectsCreature = detectedEntities.isNotEmpty()

    }



}

