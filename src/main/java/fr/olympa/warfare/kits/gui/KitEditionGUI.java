package fr.olympa.warfare.kits.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import fr.olympa.api.spigot.gui.Inventories;
import fr.olympa.api.spigot.gui.OlympaGUI;
import fr.olympa.api.spigot.item.ItemUtils;
import fr.olympa.api.utils.Prefix;

public class KitEditionGUI extends OlympaGUI {
	
	private Consumer<ItemStack[]> end;
	
	public KitEditionGUI(ItemStack[] items, Consumer<ItemStack[]> end) {
		super("Modifier le kit", 2);
		this.end = end;
		
		int i = 0;
		for (ItemStack item : items) {
			inv.setItem(i++, item);
		}
		
		inv.setItem(16, ItemUtils.item(Material.BARRIER, "§cAnnuler"));
		inv.setItem(17, ItemUtils.item(Material.DIAMOND, "§aValider"));
	}
	
	@Override
	public boolean onMoveItem(Player p, ItemStack moved) {
		return false;
	}
	
	@Override
	public boolean onClickCursor(Player p, ItemStack current, ItemStack cursor, int slot) {
		return slot >= 16; // cancel si c'est les item 16 et 17
	}
	
	@Override
	public boolean onClick(Player p, ItemStack current, int slot, ClickType click) {
		if (slot < 16) return false;
		if (slot == 17) {
			List<ItemStack> items = new ArrayList<>();
			ItemStack[] contents = inv.getContents();
			for (int i = 0; i < contents.length && i < 16; i++) {
				ItemStack item = contents[i];
				if (item != null) items.add(item);
			}
			if (items.isEmpty()) {
				Prefix.DEFAULT_BAD.sendMessage(p, "Tu ne peux pas créer un kit vide !");
				return true;
			}
			end.accept(items.toArray(ItemStack[]::new));
		}
		Inventories.closeAndExit(p);
		return true;
	}
	
}
