package com.nexomc.nexo_bolt_addon

import com.nexomc.nexo.NexoPlugin
import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent
import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent
import com.nexomc.nexo.mechanics.furniture.BlockLocation
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager
import com.nexomc.nexo.utils.AdventureUtils
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlot
import org.popcraft.bolt.event.LockBlockEvent
import org.popcraft.bolt.util.Action

class NexoFurnitureListener : Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun NexoFurnitureInteractEvent.onInteract() {
        if (useFurniture == Event.Result.DENY) return
        if (boltPlugin.player(player).action != null) return
        if (!bolt.isProtectedExact(baseEntity)) return
        if (bolt.canAccess(baseEntity, player, "private")) return

        isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun NexoFurnitureBreakEvent.onBreak() {
        bolt.removeProtection(bolt.findProtection(baseEntity) ?: return)
    }

    @EventHandler
    fun NexoFurnitureInteractEvent.onLock() {
        if (hand != EquipmentSlot.HAND || useFurniture == Event.Result.DENY) return
        if (boltActionFurniture(player, baseEntity, mechanic)) isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun NexoFurnitureBreakEvent.onLock() {
        if (boltActionFurniture(player, baseEntity, mechanic)) isCancelled = true
    }

    private fun boltActionFurniture(player: Player, baseEntity: ItemDisplay, mechanic: FurnitureMechanic): Boolean {
        val action = boltPlugin.player(player).action ?: return false
        when (action.type) {
            Action.Type.LOCK -> when {
                !bolt.isProtectedExact(baseEntity) -> {
                    bolt.saveProtection(bolt.createProtection(baseEntity, player.uniqueId, action.data))
                    player.sendMessage(AdventureUtils.MINI_MESSAGE.deserialize("Locked <yellow>Private <i>${mechanic.itemID}"))
                }

                else -> player.sendMessage(AdventureUtils.MINI_MESSAGE.deserialize("Could not private <yellow><i>${mechanic.itemID}"))
            }
            Action.Type.UNLOCK -> when {
                bolt.isProtectedExact(baseEntity) && bolt.canAccess(baseEntity, player) -> {
                    bolt.removeProtection(bolt.findProtection(baseEntity))
                    player.sendMessage(AdventureUtils.MINI_MESSAGE.deserialize("Unlocked <yellow><i>${mechanic.itemID}"))
                }

                else -> player.sendMessage(AdventureUtils.MINI_MESSAGE.deserialize("Furniture <yellow><i>${mechanic.itemID}</i></yellow> was not private"))
            }
            else -> return false
        }

        return true
    }
}