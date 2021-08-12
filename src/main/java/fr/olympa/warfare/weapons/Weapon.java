package fr.olympa.warfare.weapons;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public interface Weapon {
	
	public default void onInteract(PlayerInteractEvent e) {}
	
	public default void onEntityHit(EntityDamageByEntityEvent e) {}
	
	public default void itemHeld(Player p, ItemStack item, Weapon previous) {}
	
	public default void itemNoLongerHeld(Player p, ItemStack item) {}
	
}
