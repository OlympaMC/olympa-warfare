package fr.olympa.warfare.weapons;

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

public enum Grenade implements Weapon, ItemStackable {
	
	GRENADE(Material.BLACK_DYE, "Grenade", "Engin explosif détonant quelques secondes après l'avoir lancée."),
	;
	
	private final String name;
	private final ItemStack item;
	
	private Grenade(Material material, String name, String description) {
		this.name = name;
		
		item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§c" + name);
		meta.setLore(SpigotUtils.wrapAndAlign(description, 35));
		meta.getPersistentDataContainer().set(WeaponsListener.GRENADE_KEY, PersistentDataType.INTEGER, ordinal());
		meta.setCustomModelData(1);
		item.setItemMeta(meta);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getId() {
		return name();
	}
	
	@Override
	public ItemStack createItem() {
		return item.clone();
	}

	public ItemStack get(int amount) {
		ItemStack item = this.item.clone();
		item.setAmount(amount);
		return item;
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
				p.getWorld().createExplosion(itemEntity.getLocation(), 4.5f, false, false, p);
			}, 65);
		}
	}

}
