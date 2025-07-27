package com.vladra.goodDog.learning

import FleeGoal
import FollowGoal
import StayGoal
import com.destroystokyo.paper.entity.ai.Goal
import com.vladra.goodDog.dog.Dog
import com.vladra.goodDog.dog.goal.AttackGoal
import com.vladra.goodDog.dog.goal.SitGoal
import org.bukkit.entity.Wolf
import org.bukkit.plugin.java.JavaPlugin

class DogTrainingProblem(private val dog: Dog, private val plugin: JavaPlugin) :
    Problem<Dog, Goal<Wolf>> {

    override fun getCurrentState(): Dog {
        dog.senseCurrentState()
        return dog
    }

    override fun getAvailableActions(state: Dog): List<Goal<Wolf>> {
        return listOf(
            AttackGoal(dog, plugin),
            FleeGoal(dog, plugin),
            FollowGoal(dog, plugin),
            StayGoal(dog, plugin)
        )
    }

    override fun takeAction(state: Dog, action: Goal<Wolf>): Pair<Double, Dog> {

        dog.addGoal(action, priority = 1)

        val reward = when (action) {
            is AttackGoal -> if (dog.seesHostileMob) 5.0 else -2.0
            is FleeGoal -> if (dog.seesHostileMob && dog.dogHealth < 2) 3.0 else -1.0
            is FollowGoal -> if (!dog.playerNearby) 2.0 else -0.5
            is SitGoal, is StayGoal -> if (!dog.playerInDanger) 1.0 else -1.0
            else -> 0.0
        }

        dog.senseCurrentState()
        return reward to dog
    }
}
