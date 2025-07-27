package com.vladra.goodDog.utils

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Mob

object PathingUtils {
    val DANGEROUS_BLOCKS = setOf(
        Material.LAVA, Material.FIRE, Material.CACTUS,
        Material.MAGMA_BLOCK, Material.SWEET_BERRY_BUSH,
        Material.CAMPFIRE, Material.SOUL_FIRE
    )


    fun isPathSafe(mob: Mob, target: Location): Boolean {
        val path = mob.pathfinder.findPath(target) ?: return false

        return path.points.none { point ->
            val block: Block = point.block
            DANGEROUS_BLOCKS.contains(block.type)
        }
    }


    fun findSafeNearbyLocation(target: Location, radius: Double, mob: Mob): Location? {
        val world = target.world ?: return null

        return (0..25).asSequence()
            .map {
                target.clone().add(
                    (Math.random() - 0.5) * radius * 2,
                    0.0,
                    (Math.random() - 0.5) * radius * 2
                ).block.location
            }
            .firstOrNull { candidate ->
                val path = mob.pathfinder.findPath(candidate)
                path != null && path.points.none { DANGEROUS_BLOCKS.contains(it.block.type) }
            }
    }
}
