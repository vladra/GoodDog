package com.vladra.goodDog

import com.vladra.goodDog.dog.DogManager
import com.vladra.goodDog.event.ChatListener
import com.vladra.goodDog.listener.CombatListener
import com.vladra.goodDog.listener.WolfTameListener

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class GoodDog : JavaPlugin() {

    lateinit var combatListener: CombatListener
    lateinit var dogManager: DogManager

    override fun onEnable() {

        //load configuration
        val configFile = File(this.dataFolder, "config.yml")
        if (!configFile.exists()) {
            this.saveResource("config.yml", false)
        }
        val config = YamlConfiguration.loadConfiguration(configFile) //final init of config

        //load language file
        val langCode = config.getString("language") ?: "en_us"
        val langFile = File(dataFolder, "lang/$langCode.yml")

        if (!langFile.exists()) {
        //load english as the default--sorry!
            val langFile = File(dataFolder, "lang/en_us.yml")
        }
        val langConfig = YamlConfiguration.loadConfiguration(langFile) //final init of lang



        //must initialize this first
        combatListener = CombatListener(this)

        //initialize plugin
        dogManager = DogManager(this, combatListener)
        dogManager.loadDogs()

        //continue init listeners
        server.pluginManager.registerEvents(combatListener, this)
        server.pluginManager.registerEvents(WolfTameListener(dogManager), this)
        server.pluginManager.registerEvents(ChatListener(this, dogManager), this)



    }

    override fun onDisable() {
        // Plugin shutdown logic
        dogManager.save()
    }

}
