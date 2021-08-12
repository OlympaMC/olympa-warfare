package fr.olympa.warfare.weapons;

import org.bukkit.inventory.ItemStack;

public interface ItemStackable {
	
	public String getName();
	
	public String getId();
	
	public default String getUniqueId() {
		return getClass().getSimpleName() + "-" + getId();
	}
	
	public ItemStack createItem();
	
	public default ItemStack getDemoItem() {
		return createItem();
	}
	
}
