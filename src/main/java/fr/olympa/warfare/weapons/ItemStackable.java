package fr.olympa.warfare.weapons;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ItemStackable {
	
	public String getName();
	
	public String getId();
	
	public default String getUniqueId() {
		return getClass().getSimpleName() + "-" + getId();
	}
	
	public void giveItems(Player p);
	
	public ItemStack getDemoItem();
	
}
