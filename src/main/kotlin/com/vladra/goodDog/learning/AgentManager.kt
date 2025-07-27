package com.vladra.goodDog.learning

import FleeGoal
import FollowGoal
import QLearningAgent
import StayGoal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.vladra.goodDog.dog.Dog
import com.vladra.goodDog.dog.goal.AttackGoal
import com.vladra.goodDog.dog.goal.SitGoal
import com.vladra.goodDog.listener.CombatListener
import org.bukkit.Bukkit
import org.bukkit.entity.Wolf
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

object AgentManager {
    private val agents = mutableMapOf<UUID, QLearningAgent>()

    fun startAgent(agent: QLearningAgent, plugin: JavaPlugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            agent.step()
        }, 0L, 20L)
    }

    fun getOrCreateAgent(dog: Dog, plugin: JavaPlugin): QLearningAgent {
        return agents.getOrPut(dog.uuid) {
            val problem = DogTrainingProblem(dog, plugin)
            QLearningAgent(
                dog = dog,
                alpha = 0.2,
                gamma = 0.9,
                rho = 0.1,
                actionSupplier = { listOf(AttackGoal(dog, plugin), FleeGoal(dog, plugin),
                    FollowGoal(dog, plugin), SitGoal(dog, plugin), StayGoal(dog, plugin)) },
                actionExecutor = { goal -> dog.clearGoals(); dog.addGoal(goal, priority = 1) }
            )

        }
    }

    fun getAgent(wolf: Wolf): QLearningAgent? {
        return agents[wolf.uniqueId]
    }

    fun removeAgent(wolf: Wolf) {
        agents.remove(wolf.uniqueId)
    }

    fun clear() {
        agents.clear()
    }
}
