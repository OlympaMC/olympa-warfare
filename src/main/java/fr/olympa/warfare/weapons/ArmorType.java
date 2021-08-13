package fr.olympa.warfare.weapons;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import fr.olympa.api.spigot.item.ImmutableItemStack;

public enum ArmorType implements ItemStackable {
	P1PP1(
			"GOLDEN",
			"Double protection",
			Enchantment.PROTECTION_ENVIRONMENTAL,
			Enchantment.PROTECTION_PROJECTILE),
	PP1(
			"GOLDEN",
			"Protection",
			Enchantment.PROTECTION_PROJECTILE),
			;

	private ImmutableItemStack helmet;
	private ImmutableItemStack chestplate;
	private ImmutableItemStack leggings;
	private ImmutableItemStack boots;
	private String name;

	private ArmorType(String type, String name, Enchantment... enchantments) {
		this.name = name;
		this.helmet = createItem(ArmorSlot.HELMET, "Casque", type, enchantments);
		this.chestplate = createItem(ArmorSlot.CHESTPLATE, "Veste", type, enchantments);
		this.leggings = createItem(ArmorSlot.LEGGINGS, "Jambières", type, enchantments);
		this.boots = createItem(ArmorSlot.BOOTS, "Bottes", type, enchantments);
	}

	private ImmutableItemStack createItem(ArmorSlot slot, String name, String type, Enchantment... enchantments) {
		ItemStack item = new ItemStack(Material.valueOf(type + "_" + slot.name()));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§b" + name);
		meta.setUnbreakable(true);
		for (Enchantment enchantment : enchantments) meta.addEnchant(enchantment, 1, true);
		item.setItemMeta(meta);
		return new ImmutableItemStack(item);
	}

	@Override
	public String getId() {
		return name();
	}
	
	@Override
	public String getName() {
		return name;
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
	
	@Override
	public ItemStack getDemoItem() {
		return helmet.toMutableStack();
	}
	
	@Override
	public void giveItems(Player p) {
		setFull(p);
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
