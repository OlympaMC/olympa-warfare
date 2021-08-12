package fr.olympa.warfare.kits.gui;

import java.util.ArrayList;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import fr.olympa.api.spigot.gui.templates.PagedGUI;
import fr.olympa.warfare.OlympaPlayerWarfare;
import fr.olympa.warfare.OlympaWarfare;
import fr.olympa.warfare.kits.Kit;

public class KitListGUI extends PagedGUI<Kit> {
	
	private OlympaPlayerWarfare player;
	
	public KitListGUI(OlympaPlayerWarfare player) {
		super("Kits", DyeColor.RED, new ArrayList<>(OlympaWarfare.getInstance().kits.getKits()), 3);
		this.player = player;
		OlympaWarfare.getInstance().kits.getKits().stream().filter(kit -> kit.getMinLevel() > player.getLevel()).forEach(kit -> updateObjectItem(kit, kit.getIconGUI(false)));
	}
	
	@Override
	public ItemStack getItemStack(Kit kit) {
		return kit.getIconGUI(player == null || (kit.getMinLevel() <= player.getLevel()));
	}

	@Override
	public void click(Kit kit, Player p, ClickType click) {
		if (click.isLeftClick()) {
			if (kit.canTake(player)) {
				kit.give(player, p);
			}else {
				kit.sendImpossibleToTake(player);
			}
		}else if (click.isRightClick()) {
			new KitViewGUI(kit).create(p);
		}
	}
	
}
