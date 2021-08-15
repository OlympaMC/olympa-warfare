package fr.olympa.warfare.weapons;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import fr.olympa.api.spigot.utils.SpigotUtils;
import fr.olympa.core.spigot.OlympaCore;
import fr.olympa.warfare.OlympaWarfare;
import fr.olympa.warfare.weapons.guns.GunFlag;

public class Grenade implements Weapon, ItemStackable, Cloneable {
	
	public static final Grenade GRENADE = new Grenade(1, Material.BLACK_DYE, "Grenade", "Engin explosif détonant quelques secondes après l'avoir lancée.");
	public static Map<Integer, Grenade> GRENADES;
	
	private final int id;
	private final String name;
	private final ItemStack item;
	
	private int amount = 0;
	
	private Grenade(int id, Material material, String name, String description) {
		this.id = id;
		this.name = name;
		
		item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§c" + name);
		meta.setLore(SpigotUtils.wrapAndAlign(description, 35));
		meta.getPersistentDataContainer().set(WeaponsListener.GRENADE_KEY, PersistentDataType.INTEGER, id);
		meta.setCustomModelData(1);
		item.setItemMeta(meta);
		
		if (GRENADES == null) GRENADES = new HashMap<>();
		GRENADES.put(id, this);
	}
	
	@Override
	public String getName() {
		return name + " x" + amount;
	}
	
	@Override
	public String getId() {
		return "grenade" + id;
	}
	
	@Override
	public ItemStack getDemoItem() {
		return item.clone();
	}
	
	@Override
	public void giveItems(Player p) {
		p.getInventory().addItem(get(amount));
	}

	public ItemStack get(int amount) {
		ItemStack item = this.item.clone();
		item.setAmount(amount);
		return item;
	}
	
	public Grenade toAmount(int amount) {
		Grenade grenade = clone();
		grenade.amount = amount;
		return grenade;
	}
	
	@Override
	protected Grenade clone() {
		try {
			return (Grenade) super.clone();
		}catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			Location location = p.getEyeLocation();
			GunFlag flag = OlympaCore.getInstance().getRegionManager().getMostImportantFlag(location, GunFlag.class);
			if (flag != null && !flag.isFireEnabled(p, true)) return;
			ItemStack item = e.getItem();
			ItemStack single = item.clone();
			single.setAmount(1);
			item.setAmount(item.getAmount() - 1);
			p.getWorld().playSound(location, Sound.ENTITY_EGG_THROW, 0.5f, 1);
			Item itemEntity = p.getWorld().dropItem(location, single);
			itemEntity.setVelocity(location.getDirection());
			itemEntity.setPersistent(false);
			itemEntity.setPickupDelay(Short.MAX_VALUE);
			Bukkit.getScheduler().runTaskLater(OlympaWarfare.getInstance(), () -> {
				itemEntity.remove();
				p.getWorld().createExplosion(itemEntity.getLocation(), 4f, false, false, p);
			}, 45);
		}
	}

}
