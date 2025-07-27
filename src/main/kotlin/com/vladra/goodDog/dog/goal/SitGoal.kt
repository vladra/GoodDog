package com.vladra.goodDog.dog.goal

import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.destroystokyo.paper.entity.ai.GoalType
import com.vladra.goodDog.dog.Dog
import com.vladra.goodDog.dog.DogManager

import org.bukkit.NamespacedKey

import org.bukkit.entity.Wolf
import org.bukkit.plugin.java.JavaPlugin
import java.util.EnumSet

//keeps dog sitting

class SitGoal(
    private val dog: Dog,
    private val plugin: JavaPlugin,

    ) : Goal<Wolf> {

    private val key: GoalKey<Wolf> =
        GoalKey.of(Wolf::class.java, NamespacedKey(plugin, "dog_sit"))

    override fun getKey(): GoalKey<Wolf> = key

    override fun getTypes(): EnumSet<GoalType> =
        EnumSet.of(GoalType.LOOK, GoalType.MOVE)

    override fun shouldActivate(): Boolean {
        return true
    }

    override fun shouldStayActive(): Boolean {
        return shouldActivate()
    }

    override fun start() {
        dog.dogEntity.isSitting = true
        dog.dogEntity.pathfinder.stopPathfinding()

        val owner = dog.ownerEntity
        owner?.sendMessage("Your dog is sitting!")
    }

    override fun tick() {
        if (!dog.dogEntity.isSitting) {
            dog.dogEntity.isSitting = true
        }
    }

    override fun stop() {
        dog.dogEntity.isSitting = false
    }

}
