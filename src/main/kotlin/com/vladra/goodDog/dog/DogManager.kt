package com.vladra.goodDog.dog

import com.destroystokyo.paper.entity.ai.Goal
import com.vladra.goodDog.learning.AgentManager
import com.vladra.goodDog.listener.CombatListener
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.entity.Wolf
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.util.UUID

class DogManager(private val plugin: JavaPlugin, private val combatListener: CombatListener) {
    private val dogMap = mutableMapOf<UUID, Dog>()
    val dogKey = NamespacedKey(plugin, "goodDog")

    //namedspacedKey functions

    fun isGoodDog(wolf: Wolf): Boolean {
        return wolf.persistentDataContainer.has(dogKey, PersistentDataType.INTEGER)
    }

    fun setIsGoodDog(wolf: Wolf) {
        wolf.persistentDataContainer.set(dogKey, PersistentDataType.INTEGER, 1)
    }

    fun getDog(wolf: Wolf): Dog? {
        val dog = dogMap[wolf.uniqueId]
        return dog
    }


    //setup / load functions

    fun newDog(wolf: Wolf, owner: Player) {
        setIsGoodDog(wolf)
        val dog = Dog(wolf.uniqueId, owner.uniqueId, wolf, combatListener, owner)
        AgentManager.getOrCreateAgent(dog, plugin)
        val agent = AgentManager.getAgent(wolf)
        if (agent != null) AgentManager.startAgent(agent, plugin)
        dogMap[wolf.uniqueId] = dog
    }

    fun loadDogs() {
        val dir = File(plugin.dataFolder, "dogs")
        if (!dir.exists()) dir.mkdirs()

        Bukkit.getWorlds().forEach { world ->
            for (entity in world.entities) {
                val wolf = entity as? Wolf ?: continue
                val uuid = wolf.uniqueId

                val dogFile = File(dir, "$uuid.yml")
                if (!dogFile.exists()) continue

                setIsGoodDog(wolf)

                val config = YamlConfiguration.loadConfiguration(dogFile)

                val ownerUUID = UUID.fromString(config.getString("ownerUUID") ?: continue)
                val name = config.getString("name")
                val owner = Bukkit.getPlayer(ownerUUID)  ?: Bukkit.getOfflinePlayer(ownerUUID)

                //set up dog object
                val dog = Dog(wolf.uniqueId, ownerUUID, wolf, combatListener, owner as Player?, name)
                AgentManager.getOrCreateAgent(dog, plugin)
                dogMap[uuid] = dog

            }
        }
    }

    fun save() {
        val dir = File(plugin.dataFolder, "dogs")
        if (!dir.exists()) dir.mkdirs()

        for ((uuid, dog) in dogMap) {

            val file = File(dir, "$uuid.yml")
            val config = YamlConfiguration().apply {
                set("uuid", dog.uuid.toString())
                set("ownerUUID", dog.ownerUUID.toString())
                set("name", dog.name)

                val agent = AgentManager.getOrCreateAgent(dog, plugin)
                val data = agent.getQStore().serialize { state, action -> "$state@$action" }
                data.forEach { (key, value) -> set("qvalues.$key", value) }
            }

            try {
                config.save(file)
            } catch (e: IOException) {
                plugin.logger.warning("Failed to save dog data for $uuid: ${e.message}")
            }
        }
    }

}