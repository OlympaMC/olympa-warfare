package fr.olympa.warfare.weapons;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

import fr.olympa.api.spigot.utils.SpigotUtils;

public enum Knife implements Weapon, ItemStackable {
	
	BATTE(Material.BLAZE_ROD, "Batte de base-ball", "Arme contondante, peu puissante contre les joueurs, utile pour se défendre contre les infectés.", 2, 3),
	BICHE(
			Material.ARROW,
			"Pied-de-biche",
			"Objet polyvalent contre les joueurs comme contre les infectés.",
			3,
			4),
	SURIN(Material.STICK, "Surin", "Couteau perforant, occasionnant plus de dégâts chez les joueurs que chez les infectés.", 4, 2),
	;
	
	public static final BlockData BLOOD_DATA = Material.REDSTONE_BLOCK.createBlockData();
	
	private static final PotionEffect SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 9999999, 0, false, false, false);
	
	private final String name;
	private final float playerDamage, entityDamage;
	
	private final ItemStack item;
	
	private Knife(Material material, String name, String description, float playerDamage, float entityDamage) {
		this.name = name;
		this.playerDamage = playerDamage;
		this.entityDamage = entityDamage;
		
		item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§b" + name);
		meta.setLore(SpigotUtils.wrapAndAlign(description, 35));
		meta.getPersistentDataContainer().set(WeaponsListener.KNIFE_KEY, PersistentDataType.INTEGER, ordinal());
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
	
	public boolean isItem(ItemStack item) {
		return this == WeaponsListener.getWeapon(item);
	}
	
	public ItemStack getSkinItem(Skin skin) {
		ItemStack item = createItem();
		setSkin(skin, item);
		return item;
	}
	
	public void setSkin(Skin skin, ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		meta.setCustomModelData(skin.getId() + 1);
		item.setItemMeta(meta);
	}
	
	@Override
	public void onEntityHit(EntityDamageByEntityEvent e){
		if (e.getEntity() instanceof Player) {
			e.setDamage(playerDamage);
		}else e.setDamage(entityDamage);
		
		if (e.getDamager()instanceof LivingEntity damager) {
			Location damagerLoc = damager.getEyeLocation();
			RayTraceResult rayTrace = e.getEntity().getBoundingBox().expand(0.4).rayTrace(damagerLoc.toVector(), damagerLoc.getDirection(), 5);
			if (rayTrace != null) {
				spawnBlood(rayTrace.getHitPosition().toLocation(damagerLoc.getWorld()), 6);
			}//else OlympaWarfare.getInstance().sendMessage("§c%s a tapé en-dehors d'une boîte de collision.", e.getDamager().getName());
		}
	}
	
	@Override
	public void itemHeld(Player p, ItemStack item, Weapon previous) {
		p.addPotionEffect(SPEED_EFFECT);
	}
	
	@Override
	public void itemNoLongerHeld(Player p, ItemStack item) {
		p.removePotionEffect(PotionEffectType.SPEED);
	}
	
	public static void spawnBlood(Location location, int count) {
		location.getWorld().spawnParticle(Particle.BLOCK_CRACK, location, count, BLOOD_DATA);
	}
	
}
