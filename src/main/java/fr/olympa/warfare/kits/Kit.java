package fr.olympa.warfare.kits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.olympa.api.spigot.command.essentials.KitCommand.IKit;
import fr.olympa.api.spigot.item.ItemUtils;
import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.OlympaPlayerWarfare;
import fr.olympa.warfare.OlympaWarfare;
import fr.olympa.api.spigot.utils.SpigotUtils;

public class Kit implements IKit<OlympaPlayerWarfare> {
	
	private String id, name;
	private ItemStack[] items;
	private ItemStack[] cachedItems;
	private EnumMap<EquipmentSlot, ItemStack> cachedEquipment;
	private PotionEffect[] cachedPotions;
	private List<String> itemsDescription;
	private ItemStack icon, iconGUIGood, iconGUIBad;
	private int minLevel;
	
	protected Kit(String id, String name, ItemStack[] items, String[] itemsDescription, ItemStack icon, int minLevel) {
		this.id = id;
		this.name = name;
		this.items = items;
		this.itemsDescription = new ArrayList<>(Arrays.asList(itemsDescription));
		this.icon = icon;
		this.minLevel = minLevel;
		refreshIconGUI();
		refreshItems();
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
		OlympaWarfare.getInstance().kits.columnId.updateAsync(this, id, null, null);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		OlympaWarfare.getInstance().kits.columnName.updateAsync(this, name, null, null);
		refreshIconGUI();
	}
	
	public ItemStack[] getItems() {
		return items;
	}
	
	public void setItems(ItemStack[] items) {
		this.items = items;
		try {
			OlympaWarfare.getInstance().kits.columnItems.updateAsync(this, ItemUtils.serializeItemsArray(items), null, null);
		}catch (IOException e) {
			e.printStackTrace();
		}
		refreshItems();
	}
	
	private void refreshItems() {
		List<ItemStack> realItems = new ArrayList<>(items.length);
		List<PotionEffect> potionItems = new ArrayList<>(2);
		cachedEquipment = new EnumMap<>(EquipmentSlot.class);
		for (ItemStack item : items) {
			String itemType = item.getType().name();
			if (item.getType() == Material.POTION) {
				PotionMeta meta = (PotionMeta) item.getItemMeta();
				PotionData data = meta.getBasePotionData();
				if (data.getType().getEffectType() != null) {
					potionItems.add(new PotionEffect(data.getType().getEffectType(), 9999999, data.isUpgraded() ? 1 : 0, false, false));
				}
				meta.getCustomEffects().forEach(potionItems::add);
			}else if (itemType.endsWith("_HELMET")) {
				cachedEquipment.put(EquipmentSlot.HEAD, item);
			}else if (itemType.endsWith("_CHESTPLATE")) {
				cachedEquipment.put(EquipmentSlot.CHEST, item);
			}else if (itemType.endsWith("_LEGGINGS")) {
				cachedEquipment.put(EquipmentSlot.LEGS, item);
			}else if (itemType.endsWith("_BOOTS")) {
				cachedEquipment.put(EquipmentSlot.FEET, item);
			}else realItems.add(item);
		}
		cachedItems = realItems.toArray(ItemStack[]::new);
		cachedPotions = potionItems.toArray(PotionEffect[]::new);
	}
	
	public void addItemDescription(String item) {
		this.itemsDescription.add(item);
		refreshItemsDescription();
	}
	
	public void removeItemDescription(int index) throws IndexOutOfBoundsException {
		this.itemsDescription.remove(index);
		refreshItemsDescription();
	}
	
	private void refreshItemsDescription() {
		OlympaWarfare.getInstance().kits.columnItemsDescription.updateAsync(this, String.join("||", itemsDescription), null, null);
		refreshIconGUI();
	}
	
	public ItemStack getIconGUI(boolean good) {
		return good ? iconGUIGood : iconGUIBad;
	}
	
	private void refreshIconGUI() {
		int i = 0;
		String[] lore = new String[itemsDescription.size() + 6];
		lore[i++] = "";
		for (String item : itemsDescription) lore[i++] = "§8● §7" + item;
		lore[i++] = "";
		int validityIndex = i++;
		lore[i++] = "";
		lore[i++] = "§8§lClic droit> §7voir le contenu";
		lore[i++] = "§8§lClic gauche> §7§lsélectionner le kit";
		lore[validityIndex] = "§a✔ §7Niveau " + minLevel;
		iconGUIGood = ItemUtils.name(ItemUtils.lore(icon.clone(), lore), name);
		lore[validityIndex] = "§c✖ §7Niveau " + minLevel;
		iconGUIBad = ItemUtils.name(ItemUtils.lore(icon.clone(), lore), name);
	}
	
	public ItemStack getIcon() {
		return icon;
	}
	
	public void setIcon(ItemStack icon) {
		this.icon = icon;
		try {
			OlympaWarfare.getInstance().kits.columnIcon.updateAsync(this, SpigotUtils.serialize(icon), null, null);
		}catch (IOException e) {
			e.printStackTrace();
		}
		refreshIconGUI();
	}
	
	public int getMinLevel() {
		return minLevel;
	}
	
	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
		OlympaWarfare.getInstance().kits.columnLevel.updateAsync(this, minLevel, null, null);
	}
	
	@Override
	public void give(OlympaPlayerWarfare olympaPlayer, Player p) {
		//Bukkit.getScheduler().runTaskAsynchronously(OlympaPvPKit.getInstance(), () -> {
			p.teleport(OlympaWarfare.getInstance().spawnPoints.getBestLocation());
			p.getInventory().clear();
			p.getInventory().setHeldItemSlot(0);
			SpigotUtils.giveItems(p, cachedItems);
			for (PotionEffect effect : cachedPotions) p.addPotionEffect(effect);
			p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 0, false, false));
			cachedEquipment.forEach((slot, item) -> p.getInventory().setItem(slot, item));
			olympaPlayer.setInPvPZone(this);
			p.closeInventory();
			p.setGameMode(GameMode.ADVENTURE);
			Prefix.DEFAULT_GOOD.sendMessage(p, "Tu as reçu le kit %s ! Bon combat !", id);
			//});
	}
	
	@Override
	public boolean canTake(OlympaPlayerWarfare player) {
		return !player.isInPvPZone() && player.getLevel() >= minLevel;
	}
	
	@Override
	public void sendImpossibleToTake(OlympaPlayerWarfare player) {
		if (player.getLevel() < minLevel) {
			Prefix.DEFAULT_BAD.sendMessage((Player) player.getPlayer(), "Tu dois être niveau %d pour pouvoir prendre ce kit !", minLevel);
		}else Prefix.DEFAULT_BAD.sendMessage((Player) player.getPlayer(), "Tu ne peux pas prendre de kit si tu es déjà en combat !");
	}
	
	@Override
	public long getTimeBetween() {
		return 0;
	}
	
	@Override
	public long getLastTake(OlympaPlayerWarfare player) {
		return 0;
	}
	
	@Override
	public void setLastTake(OlympaPlayerWarfare player, long time) {}
	
}
