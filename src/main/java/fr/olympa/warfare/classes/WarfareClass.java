package fr.olympa.warfare.classes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.olympa.api.spigot.item.ItemUtils;
import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.weapons.ArmorType;
import fr.olympa.warfare.weapons.Grenade;
import fr.olympa.warfare.weapons.ItemStackable;
import fr.olympa.warfare.weapons.Knife;
import fr.olympa.warfare.weapons.guns.GunType;

public enum WarfareClass {
	
	SOLDAT(
			1,
			"Soldat",
			GunType.P22,
			GunType.M16,
			Knife.SURIN,
			ArmorType.PP1),
	BOURINOS(
			2,
			"Bourrin",
			GunType.KSG,
			GunType.REM_870,
			Knife.BATTE,
			ArmorType.P1PP1),
	POLY(
			4,
			"Polyvalent",
			GunType.SDMR,
			GunType.G19,
			Grenade.GRENADE.toAmount(2),
			Knife.BATTE,
			ArmorType.PP1),
	KAMIKAZE(
			7,
			"Kamikaze",
			Grenade.GRENADE.toAmount(8),
			GunType.AK_20,
			Knife.SURIN,
			ArmorType.PP1),
	SNIPER(
			10,
			"Sniper",
			GunType.DRAGUNOV,
			Knife.SURIN,
			ArmorType.P1PP1),
			;
	
	private int minLevel;
	private String name;
	private ItemStackable[] stackables;
	
	private ItemStack itemValid, itemInvalid;
	
	private WarfareClass(int minLevel, String name, ItemStackable... stackables) {
		this.minLevel = minLevel;
		this.name = name;
		this.stackables = stackables;
		
		List<String> lore = new ArrayList<>(stackables.length + 3);
		lore.add("");
		for (ItemStackable stackable : stackables) {
			lore.add("§8> §7" + stackable.getName());
		}
		lore.add("");
		lore.add("§a✔ §7Niveau " + minLevel);
		itemValid = ItemUtils.name(ItemUtils.setRawLore(stackables[0].getDemoItem().clone(), lore), "§7Classe §l" + name);
		lore.set(lore.size() - 1, "§c✖ §7Niveau " + minLevel);
		itemInvalid = ItemUtils.name(ItemUtils.setRawLore(stackables[0].getDemoItem().clone(), lore), "§7Classe §l" + name);
	}
	
	public int getMinLevel() {
		return minLevel;
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStack getItem(boolean valid) {
		return valid ? itemValid : itemInvalid;
	}
	
	public void give(Player p) {
		for (ItemStackable stackable : stackables) {
			stackable.giveItems(p);
		}
		Prefix.DEFAULT_GOOD.sendMessage(p, "Tu as reçu la classe §l%s", name);
	}
	
}
