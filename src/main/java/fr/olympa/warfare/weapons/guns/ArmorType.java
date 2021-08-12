package fr.olympa.warfare.weapons.guns;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import fr.olympa.api.spigot.item.ImmutableItemStack;

public enum ArmorType {
	CIVIL("Tenue civile", "LEATHER", "Bandana", "Veste", "Jeans", "Baskets", true),
	GANGSTER("Tenue de gangster", "GOLDEN", "Cagoule en Kevlar", "Veste en Kevlar", "Pantalon en Kevlar", "Chaussures en Kevlar", true, Enchantment.PROTECTION_PROJECTILE, 1),
	ANTIRIOT("Armure anti-émeutes", "CHAINMAIL", "Casque anti-émeutes", "Plastron anti-émeutes", "Jambières anti-émeutes", "Bottes anti-émeutes", true, Enchantment.PROTECTION_ENVIRONMENTAL, 1),
	MILITARY("Armure en Kevlar renforcé", "IRON", "Casque en Kevlar renforcé", "Plastron en Kevlar renforcé", "Jambières en Kevlar renforcé", "Bottes en Kevlar renforcé", false, Enchantment.PROTECTION_PROJECTILE, 1);

	private String name;
	private String type;
	private boolean unbreakable;
	private Enchantment enchantment;
	private int level;
	private ImmutableItemStack helmet;
	private ImmutableItemStack chestplate;
	private ImmutableItemStack leggings;
	private ImmutableItemStack boots;

	private ArmorType(String name, String type, String helmet, String chestplate, String leggings, String boots, boolean unbreakable) {
		this(name, type, helmet, chestplate, leggings, boots, unbreakable, null, 0);
	}

	private ArmorType(String name, String type, String helmet, String chestplate, String leggings, String boots, boolean unbreakable, Enchantment enchantment, int level) {
		this.name = name;
		this.type = type;
		this.unbreakable = unbreakable;
		this.enchantment = enchantment;
		this.level = level;
		this.helmet = createItem(ArmorSlot.HELMET, helmet);
		this.chestplate = createItem(ArmorSlot.CHESTPLATE, chestplate);
		this.leggings = createItem(ArmorSlot.LEGGINGS, leggings);
		this.boots = createItem(ArmorSlot.BOOTS, boots);
	}

	public String getName() {
		return name;
	}

	private ImmutableItemStack createItem(ArmorSlot slot, String name) {
		ItemStack item = new ItemStack(Material.valueOf(type + "_" + slot.name()));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§b" + name);
		meta.setUnbreakable(unbreakable);
		if (enchantment != null) meta.addEnchant(enchantment, level, true);
		item.setItemMeta(meta);
		return new ImmutableItemStack(item);
	}

	public ImmutableItemStack getImmutable(ArmorSlot slot) {
		return switch (slot) {
		case BOOTS -> boots;
		case CHESTPLATE -> chestplate;
		case HELMET -> helmet;
		case LEGGINGS -> leggings;
		};
	}
	
	public ItemStack get(ArmorSlot slot) {
		return getImmutable(slot).toMutableStack();
	}

	public void setFull(Player p) {
		PlayerInventory inv = p.getInventory();
		inv.setHelmet(getImmutable(ArmorSlot.HELMET));
		inv.setChestplate(getImmutable(ArmorSlot.CHESTPLATE));
		inv.setLeggings(getImmutable(ArmorSlot.LEGGINGS));
		inv.setBoots(getImmutable(ArmorSlot.BOOTS));
	}

	public enum ArmorSlot {
		HELMET(EquipmentSlot.HEAD), CHESTPLATE(EquipmentSlot.CHEST), LEGGINGS(EquipmentSlot.LEGS), BOOTS(EquipmentSlot.FEET);
		
		private EquipmentSlot slot;
		
		private ArmorSlot(EquipmentSlot slot) {
			this.slot = slot;
		}
		
		public EquipmentSlot getSlot() {
			return slot;
		}
	}

}
