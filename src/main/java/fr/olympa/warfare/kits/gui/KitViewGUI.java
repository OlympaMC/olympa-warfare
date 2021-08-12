package fr.olympa.warfare.kits.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import fr.olympa.api.spigot.gui.OlympaGUI;
import fr.olympa.api.spigot.item.ItemUtils;
import fr.olympa.warfare.OlympaPlayerWarfare;
import fr.olympa.warfare.kits.Kit;

public class KitViewGUI extends OlympaGUI {
	
	private Kit kit;
	
	public KitViewGUI(Kit kit) {
		super("Kit " + kit.getId(), kit.getItems().length <= 9 ? 2 : 3);
		this.kit = kit;
		for (int i = 0; i < kit.getItems().length; i++) {
			inv.setItem(i, kit.getItems()[i]);
		}
		inv.setItem(inv.getSize() - 6, ItemUtils.item(Material.OAK_DOOR, "§a← Revenir à la liste"));
		inv.setItem(inv.getSize() - 4, ItemUtils.item(Material.DIAMOND, "§b✦ Prendre ce kit"));
	}
	
	@Override
	public boolean onClick(Player p, ItemStack current, int slot, ClickType click) {
		OlympaPlayerWarfare olympaPlayer = OlympaPlayerWarfare.get(p);
		if (slot == 12) {
			new KitListGUI(olympaPlayer).create(p);
		}else if (slot == 14) {
			if (kit.canTake(olympaPlayer)) {
				kit.give(olympaPlayer, p);
			}else {
				kit.sendImpossibleToTake(olympaPlayer);
			}
		}
		return true;
	}
	
}
