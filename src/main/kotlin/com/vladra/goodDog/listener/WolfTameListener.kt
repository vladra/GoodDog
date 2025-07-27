package com.vladra.goodDog.listener


import com.vladra.goodDog.dog.DogManager
import org.bukkit.entity.Player
import org.bukkit.entity.Wolf
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityTameEvent

class WolfTameListener(private val dogManager: DogManager) : Listener {

    @EventHandler
    fun onWolfTame(event: EntityTameEvent) {
        val wolf = event.entity as? Wolf ?: return
        val owner = event.owner as? Player ?: return
        dogManager.newDog(wolf, owner)
        owner.sendMessage("You have tamed a wolf. It will listen to you carefully. ")

    }
}
