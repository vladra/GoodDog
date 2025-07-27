package com.vladra.goodDog.event

import QLearningAgent
import com.destroystokyo.paper.entity.ai.Goal
import com.vladra.goodDog.dog.Dog
import com.vladra.goodDog.dog.DogManager
import com.vladra.goodDog.learning.AgentManager
import org.bukkit.entity.Wolf
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class ChatListener(
    private val plugin: JavaPlugin,
    private val dogManager: DogManager
) : Listener {

    private val goodWords = listOf(
        "good dog", "good boy", "good girl", "good", "yes", "attaboy", "atta girl", "nice", "well done"
    )

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val message = event.message().toString().lowercase()

        if (goodWords.any { message.contains(it) }) {
            val player = event.player

            object : BukkitRunnable() {
                override fun run() {
                    val nearbyDog = player.getNearbyEntities(10.0, 10.0, 10.0)
                        .filterIsInstance<Wolf>()
                        .filter { it.isTamed && it.ownerUniqueId == player.uniqueId }
                        .minByOrNull { it.location.distanceSquared(player.location) }

                    if (nearbyDog != null) {
                        val dog = dogManager.getDog(nearbyDog) ?: return
                        player.sendMessage(
                            Component.text("You praised ${dog.name ?: "your dog"}!")
                                .color(NamedTextColor.GREEN)
                        )

                        val agent = AgentManager.getAgent(dog.dogEntity) as? QLearningAgent
                        agent?.reward(+20.0)

                    }
                }
            }.runTask(plugin)
        }
    }
}
