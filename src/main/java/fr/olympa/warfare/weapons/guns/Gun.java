package fr.olympa.warfare.weapons.guns;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import fr.olympa.core.spigot.OlympaCore;
import fr.olympa.warfare.OlympaWarfare;
import fr.olympa.warfare.weapons.Skin;
import fr.olympa.warfare.weapons.Weapon;
import fr.olympa.warfare.weapons.guns.bullets.Bullet;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Gun implements Weapon {

	public static final List<ChatColor> TIERS = Arrays.asList(ChatColor.GREEN, ChatColor.AQUA, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.GOLD);
	public static final UUID ZOOM_UUID = UUID.fromString("8a1c6742-3f54-44c2-ac6f-90fa7491ebef");
	
	private static DecimalFormat timeFormat = new DecimalFormat("#0.0");

	protected final int id;
	protected final GunType type;
	
	protected Skin skin = Skin.NORMAL;
	
	protected int beforeTrainingAmmos = -1;
	
	protected int ammos = 0;
	protected boolean ready = false;
	protected boolean zoomed = false;
	protected boolean secondaryMode = false;
	protected BukkitTask reloading = null;

	public float damageAdded = 0;
	public float damageCaC = 0;
	public AttributeModifier zoomModifier = null;
	public final Attribute maxAmmos;
	public final Attribute chargeTime;
	public final Attribute bulletSpeed;
	public final Attribute bulletSpread;
	public final Attribute knockback;
	public final Attribute fireRate;
	public final Attribute fireVolume;
	
	public float customDamagePlayer, customDamageEntity;

	Gun(int id, GunType type) {
		this.id = id;
		this.type = type;
		
		maxAmmos = new Attribute(type.getMaxAmmos());
		chargeTime = new Attribute(type.getChargeTime());
		bulletSpeed = new Attribute(type.getBulletSpeed());
		bulletSpread = new Attribute(type.getAccuracy().getBulletSpread());
		knockback = new Attribute(type.getKnockback());
		fireRate = new Attribute(type.getFireRate());
		fireVolume = new Attribute(type.getFireVolume());
	}
	
	public int getID() {
		return id;
	}
	
	public GunType getType() {
		return type;
	}
	
	public void saveBeforeTrainingAmmos() {
		beforeTrainingAmmos = ammos;
	}
	
	public void restoreBeforeTrainingAmmos(Player p, ItemStack item) {
		if (beforeTrainingAmmos != -1) {
			cancelReload(p, item);
			ammos = beforeTrainingAmmos;
			beforeTrainingAmmos = -1;
			if (ammos != 0) ready = true;
			updateItemName(item);
		}
	}
	
	public ItemStack createItemStack() {
		return createItemStack(true);
	}

	public ItemStack createItemStack(boolean accessories) {
		ItemStack item = new ItemStack(type.getMaterial());
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.values());
		meta.getPersistentDataContainer().set(getKey(), PersistentDataType.INTEGER, getID());
		meta.setCustomModelData(1);
		meta.setLore(type.getLore());
		item.setItemMeta(meta);
		updateItemName(item);
		return item;
	}

	public void updateItemName(ItemStack item) {
		try {
			ItemMeta im = item.getItemMeta();
			im.setDisplayName("§e" + (!type.hasSecondaryMode() ? "" : secondaryMode ? "◁▶ " : "◀▷ ") + type.getName() + " [" + ammos + "/" + (int) maxAmmos.getValue() + "] " + (ready ? "●" : "○") + (reloading == null ? "" : " recharge"));
			item.setItemMeta(im);
		}catch (Exception ex) {
			OlympaWarfare.getInstance().sendMessage("§cUne erreur est survenue lors de la mise à jour d'un item d'arme.");
			ex.printStackTrace();
		}
	}

	public void updateItemCustomModel(ItemStack item) {
		try {
			ItemMeta im = item.getItemMeta();
			im.setCustomModelData(skin.getId() * 2 + (zoomed ? 2 : 1));
			item.setItemMeta(im);
		}catch (Exception ex) {
			OlympaWarfare.getInstance().sendMessage("§cUne erreur est survenue lors de la mise à jour d'un item d'arme.");
			ex.printStackTrace();
		}
	}

	public NamespacedKey getKey() {
		return GunRegistry.GUN_KEY;
	}
	
	public ItemStack getSkinItem(Skin skin) {
		Skin oldSkin = this.skin;
		this.skin = skin;
		ItemStack item = createItemStack(false);
		this.skin = oldSkin;
		return item;
	}
	
	public void setSkin(Skin skin, ItemStack item) {
		this.skin = skin;
		updateItemCustomModel(item);
	}
	
	@Override
	public void onEntityHit(EntityDamageByEntityEvent e) {
		Player damager = (Player) e.getDamager();
		if (damageCaC == 0) {
			onInteract(new PlayerInteractEvent(damager, Action.LEFT_CLICK_AIR, damager.getInventory().getItemInMainHand(), null, null));
			e.setCancelled(true);
		}else {
			e.setDamage(damageCaC);
		}
	}

	@Override
	public void itemHeld(Player p, ItemStack item, Weapon previous) {
		showAmmos(p);
		p.setCooldown(item.getType(), 0);
		if (type.hasHeldEffect()) p.addPotionEffect(type.getHeldEffect());
		int readyTime = -1;
		float thisPotential = fireRate.getValue();
		if (thisPotential <= 0) {
			if (ammos == 0) return;
			thisPotential = chargeTime.getValue();
		}
		if (previous instanceof Gun prev && !prev.ready) {
			float prevPotential = prev.fireRate.getValue();
			if (prevPotential <= 0) prevPotential = prev.chargeTime.getValue();
			readyTime = (int) Math.min(prevPotential, thisPotential);
		}
		if (readyTime == -1 && !ready) readyTime = (int) thisPotential;
		if (readyTime != -1) {
			ready = false;
			updateItemName(item);
			setCooldown(p, readyTime);
			task = new BukkitRunnable() {
				@Override
				public void run() {
					setReady(p, item);
					cancel();
				}
				@Override
				public synchronized void cancel() throws IllegalStateException {
					super.cancel();
					task = null;
				}
			}.runTaskLater(OlympaWarfare.getInstance(), readyTime);
		}
	}

	private void setCooldown(Player p, int readyTime) {
		if (readyTime > 5) p.setCooldown(type.getMaterial(), readyTime);
	}

	@Override
	public void itemNoLongerHeld(Player p, ItemStack item) {
		if (zoomed) toggleZoom(p, item);
		if (reloading != null) cancelReload(p, item);
		if (type.hasHeldEffect()) p.removePotionEffect(type.getHeldEffect().getType());
		if (task != null) {
			task.cancel();
			task = null;
		}
	}

	public void drop(Player p, ItemStack item) {
		if (Bukkit.isPrimaryThread()) {
			reload(p, item);
		}else
			Bukkit.getScheduler().runTask(OlympaWarfare.getInstance(), () -> reload(p, item));
	}

	private BukkitTask task;
	private long lastClick;

	@Override
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = e.getItem();
		e.setCancelled(true);
		
		lastClick = System.currentTimeMillis();
		
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) { // clic droit : tir
			if (reloading != null) {
				if (type.isOneByOneCharge() && ammos > 0) {
					cancelReload(p, item);
				}else return;
			}
			if (ammos == 0) { // tentative de tir alors que le barillet est vide
				reload(p, item);
			}else if (ready && isFireEnabled(p) && task == null) {
				if (getCurrentMode() == GunMode.BLAST) {
					ready = false;
					int rate = (int) (fireRate.getValue() / 2);
					task = new BukkitRunnable() {
						byte left = 3;
						@Override
						public void run() {
							if (left-- > 0) {
								fire(p);
								updateItemName(item);
								if (ammos == 0) {
									cancel();
								}
								if (left == 0) p.setCooldown(type.getMaterial(), rate * 3);
							}else if (left == -4) {
								setReady(p, item);
								cancel();
							}
						}
						@Override
						public synchronized void cancel() throws IllegalStateException {
							super.cancel();
							task = null;
						}
					}.runTaskTimer(OlympaWarfare.getInstance(), 0, rate);
				}else if (fireRate.getValue() == -1) {
					ready = false;
					fire(p);
					updateItemName(item);
				}else {
					ready = false;
					int rate = (int) fireRate.getValue();
					task = new BukkitRunnable() {
						@Override
						public void run() {
							if (ammos == 0) {
								updateItemName(item);
								cancel();
								return;
							}
							if (System.currentTimeMillis() - lastClick < 210) {
								fire(p);
								setCooldown(p, rate);
								updateItemName(item);
							}else {
								setReady(p, item);
								cancel();
							}
						}

						@Override
						public synchronized void cancel() throws IllegalStateException {
							super.cancel();
							task = null;
						}
					}.runTaskTimer(OlympaWarfare.getInstance(), 0, rate);
				}
			}
		}else if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) { // clic gauche : tir
			if (reloading == null) secondaryClick(p, item);
		}
	}
	
	private void setReady(Player p, ItemStack item) {
		ready = true;
		playReadySound(p.getLocation());
		updateItemName(item);
	}

	public boolean isFireEnabled(Player p) {
		GunFlag gunFlag = getGunFlag(p);
		if (gunFlag == null) return false;
		return gunFlag.isFireEnabled(p, true);
	}
	
	public GunFlag getGunFlag(Player p) {
		return OlympaCore.getInstance().getRegionManager().getMostImportantFlag(p.getLocation(), GunFlag.class);
	}

	private void secondaryClick(Player p, ItemStack item) {
		GunAction action = getSecondClickAction();
		if (action == null) return;
		switch (action) {
		case CHANGE_MODE:
			secondaryMode = !secondaryMode;
			updateItemName(item);
			break;
		case ZOOM:
			toggleZoom(p, item);
			break;
		}
	}

	private void fire(Player p) {
		Bullet bullet = type.createBullet(this, (customDamagePlayer == 0 ? type.getPlayerDamage() : customDamagePlayer) + damageAdded, (customDamageEntity == 0 ? type.getEntityDamage() : customDamageEntity) + damageAdded);
		for (int i = 0; i < type.getFiredBullets(); i++) {
			bullet.launchProjectile(p, p.getLocation().getDirection());
		}
		
		float knockback = this.knockback.getValue();
		if (knockback != 0) {
			if (p.isSneaking()) knockback /= 2;
			Vector velocity = p.getLocation().getDirection().multiply(-knockback).add(p.getVelocity());
			velocity.setY(velocity.getY() / 3);
			p.setVelocity(velocity);
		}
		ammos--;
		
		playFireSound(p.getLocation());
		float distance = (fireVolume.getValue() - 0.5f) * 10;
		for (Entity en : p.getWorld().getNearbyEntities(p.getLocation(), distance, distance, distance, x -> x instanceof Zombie)) {
			Zombie zombie = (Zombie) en;
			if (zombie.getTarget() == null && ThreadLocalRandom.current().nextBoolean()) zombie.setTarget(p);
		}
	}

	public void showAmmos(Player p) {
		int availableAmmos = shouldTakeItems(p) ? type.getAmmoType().getAmmos(p) : -1;
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(type.getAmmoType().getColoredName() + "§7: " + (availableAmmos == -1 ? "§c∞" : (availableAmmos == 0 ? "§c0" : availableAmmos))));
	}
	
	private void cancelReload(Player p, ItemStack item) {
		if (reloading != null) {
			reloading.cancel();
			reloading = null;
		}
		updateItemName(item);
		//p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent());
		showAmmos(p);
	}
	
	protected boolean shouldTakeItems(Player p) {
		GunFlag gunFlag = getGunFlag(p);
		return p.getGameMode() != GameMode.CREATIVE && (gunFlag == null || !gunFlag.isFreeAmmos());
	}

	public boolean reload(Player p, ItemStack item) {
		if (reloading != null) return false;
		if (zoomed) toggleZoom(p, item);

		int max = (int) maxAmmos.getValue();
		if (max <= ammos) return false;

		int toCharge;
		int availableAmmos = shouldTakeItems(p) ? type.getAmmoType().getAmmos(p) : Integer.MAX_VALUE;
		if (availableAmmos == 0) {
			playOutOfAmmosSound(p.getLocation());
			showAmmos(p);
			return false;
		}
		if (type.isOneByOneCharge()) {
			toCharge = 1;
		}else toCharge = Math.min((int) Math.ceil((max - ammos) / (double) type.getAmmoType().getAmmosPerItem()), availableAmmos);

		reloading = Bukkit.getScheduler().runTaskTimerAsynchronously(OlympaWarfare.getInstance(), new Runnable() {
			final short animationMax = 13;
			final char character = '░'; //'█';

			short time = (short) chargeTime.getValue();
			float add = (float) animationMax / (float) time;
			float current = 0;

			@Override
			public void run() {
				if (time == 0) {
					ammos = shouldTakeItems(p) ? Math.min(ammos + type.getAmmoType().removeAmmos(p, toCharge) * type.getAmmoType().getAmmosPerItem(), max) : Math.min(max, ammos + (type.isOneByOneCharge() ? 1 : max));
					if (ammos != 0) ready = true;
					playChargeCompleteSound(p.getLocation());

					if (type.isOneByOneCharge() && maxAmmos.getValue() > ammos) {
						reloading.cancel();
						reloading = null;
						if (!reload(p, item)) { // relancer une charge
							updateItemName(item); // update si plus assez de munitions = recharge terminée
						}
						return;
					}
					cancelReload(p, item);
					return;
				}
				StringBuilder status = new StringBuilder("§bRechargement... ");
				boolean changed = false;
				for (int i = 0; i < animationMax; i++) {
					if (!changed && i >= current) {
						status.append("§c");
						changed = true;
					}
					status.append(character);
				}
				status.append("§b " + timeFormat.format(time / 20D) + "s");
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(status.toString()));
				current += add;
				time--;
			}
		}, 0, 1);

		updateItemName(item);
		playChargeSound(p.getLocation());
		return true;
	}

	private void toggleZoom(Player p, ItemStack item) {
		AttributeInstance attribute = p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED);
		if (zoomed) {
			attribute.removeModifier(getZoomModifier().getBukkitModifier());
		}else {
			try {
				attribute.addModifier(getZoomModifier().getBukkitModifier());
			}catch (IllegalArgumentException ex) {
				OlympaWarfare.getInstance().sendMessage("§cZoom déjà appliqué sur un gun.");
			}
		}
		zoomed = !zoomed;
		updateItemCustomModel(item);
	}

	public GunMode getCurrentMode() {
		return secondaryMode ? type.getSecondaryMode() : type.getPrimaryMode();
	}

	/**
	 * @return Action effectuée lors du clic secondaire
	 */
	public GunAction getSecondClickAction() {
		return type.hasSecondaryMode() ? GunAction.CHANGE_MODE : hasZoom() ? GunAction.ZOOM : null;
	}
	
	public boolean hasZoom() {
		return type.hasZoom() || (zoomModifier != null);
	}
	
	public AttributeModifier getZoomModifier() {
		return zoomModifier == null ? type.getZoomModifier() : zoomModifier;
	}

	/**
	 * Jouer le son de tir
	 * @param lc location où est jouée le son
	 */
	public void playFireSound(Location lc) {
		lc.getWorld().playSound(lc, type.getFireSound().getSound(), SoundCategory.PLAYERS, fireVolume.getValue() + 0.5f, 1);
	}

	/**
	 * Jouer le son de recharge
	 * @param lc location où est jouée le son
	 */
	public void playChargeSound(Location lc) {
		lc.getWorld().playSound(lc, Sound.BLOCK_PISTON_EXTEND, SoundCategory.PLAYERS, 1, 1);
	}

	/**
	 * Jouer le son de recharge complète
	 * @param lc location où est jouée le son
	 */
	public void playChargeCompleteSound(Location lc) {
		lc.getWorld().playSound(lc, Sound.BLOCK_PISTON_CONTRACT, SoundCategory.PLAYERS, 1, 1);
	}

	/**
	 * Jouer le son de paré au tir
	 * @param lc location où est jouée le son
	 */
	public void playReadySound(Location lc) {
		lc.getWorld().playSound(lc, Sound.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.PLAYERS, 0.5f, 1);
	}

	/**
	 * Jouer le son de chargeur vide
	 * @param lc location où est jouée le son
	 */
	public void playOutOfAmmosSound(Location lc) {
		lc.getWorld().playSound(lc, Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 0.8f, 1);
	}
	
	public enum GunAction {
		ZOOM, CHANGE_MODE;
	}

	public enum GunMode {
		SINGLE("tir unique"),
		SEMI_AUTOMATIC("semi-automatique"),
		AUTOMATIC("automatique"),
		BLAST("rafales");

		private String name;

		private GunMode(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public enum GunAccuracy {
		EXTREME("Extrême", 0), HIGH("Bonne", 0.05f), MEDIUM("Moyenne", 0.2f), LOW("Faible", 0.7f);

		private String name;
		private float spread;

		private GunAccuracy(String name, float spread) {
			this.name = name;
			this.spread = spread;
		}

		public String getName() {
			return name;
		}

		public float getBulletSpread() {
			return spread;
		}

	}

}