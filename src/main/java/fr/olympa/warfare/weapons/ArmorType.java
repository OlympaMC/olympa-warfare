package fr.olympa.warfare.weapons;

import java.util.EnumMap;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import fr.olympa.api.spigot.item.ImmutableItemStack;
import fr.olympa.warfare.teamdeathmatch.Team;

public enum ArmorType implements ItemStackable {
	P1PP1(
			"Double protection",
			Enchantment.PROTECTION_ENVIRONMENTAL,
			Enchantment.PROTECTION_PROJECTILE),
	PP1(
			"Protection",
			Enchantment.PROTECTION_PROJECTILE),
			;

	private EnumMap<Team, ImmutableItemStack> helmet;
	private EnumMap<Team, ImmutableItemStack> chestplate;
	private EnumMap<Team, ImmutableItemStack> leggings;
	private EnumMap<Team, ImmutableItemStack> boots;
	private String name;

	private ArmorType(String name, Enchantment... enchantments) {
		this.name = name;
		this.helmet = createItem(ArmorSlot.HELMET, "Casque", enchantments);
		this.chestplate = createItem(ArmorSlot.CHESTPLATE, "Veste", enchantments);
		this.leggings = createItem(ArmorSlot.LEGGINGS, "Jambières", enchantments);
		this.boots = createItem(ArmorSlot.BOOTS, "Bottes", enchantments);
	}

	private EnumMap<Team, ImmutableItemStack> createItem(ArmorSlot slot, String name, Enchantment... enchantments) {
		EnumMap<Team, ImmutableItemStack> map = new EnumMap<>(Team.class);
		for (Team team : Team.values()) {
			ItemStack item = new ItemStack(Material.valueOf("LEATHER_" + slot.name()));
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			meta.setDisplayName("§b" + name);
			meta.setUnbreakable(true);
			meta.setColor(team.getArmorColor());
			for (Enchantment enchantment : enchantments) meta.addEnchant(enchantment, 2, true);
			item.setItemMeta(meta);
			map.put(team, new ImmutableItemStack(item));
		}
		return map;
	}

	@Override
	public String getId() {
		return name();
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public ImmutableItemStack getImmutable(ArmorSlot slot, Team team) {
		return switch (slot) {
		case BOOTS -> boots.get(team);
		case CHESTPLATE -> chestplate.get(team);
		case HELMET -> helmet.get(team);
		case LEGGINGS -> leggings.get(team);
		};
	}
	
	public ItemStack get(ArmorSlot slot, Team team) {
		return getImmutable(slot, team).toMutableStack();
	}

	public void setFull(Player p, Team team) {
		PlayerInventory inv = p.getInventory();
		inv.setHelmet(getImmutable(ArmorSlot.HELMET, team));
		inv.setChestplate(getImmutable(ArmorSlot.CHESTPLATE, team));
		inv.setLeggings(getImmutable(ArmorSlot.LEGGINGS, team));
		inv.setBoots(getImmutable(ArmorSlot.BOOTS, team));
	}
	
	@Override
	public ItemStack getDemoItem() {
		return helmet.get(Team.BLUE).toMutableStack();
	}
	
	@Override
	public void giveItems(Player p) {
		setFull(p, Team.getPlayerTeam(p));
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
