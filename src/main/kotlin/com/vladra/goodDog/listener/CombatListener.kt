package com.vladra.goodDog.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

class CombatListener(private val plugin: JavaPlugin) : Listener {

    val lastCombatTime = mutableMapOf<UUID, Long>()

    @EventHandler
    fun CombatListener(event: EntityDamageByEntityEvent) {
        val player = when {
            event.entity is Player -> event.entity as Player
            event.damager is Player -> event.damager as Player
            else -> return
        }

        lastCombatTime[player.uniqueId] = System.currentTimeMillis()
        player.sendMessage("Your dog has noticed that you are in combat.")

    }

    //combat helpers

    fun isInCombat(player: Player): Boolean {
        val time = lastCombatTime[player.uniqueId] ?: return false
        return System.currentTimeMillis() - time < 10_000
    }

}