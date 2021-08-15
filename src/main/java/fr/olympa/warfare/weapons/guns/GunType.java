package fr.olympa.warfare.weapons.guns;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.olympa.api.spigot.utils.SpigotUtils;
import fr.olympa.warfare.OlympaPlayerWarfare;
import fr.olympa.warfare.OlympaWarfare;
import fr.olympa.warfare.WarfarePermissions;
import fr.olympa.warfare.weapons.ItemStackable;
import fr.olympa.warfare.weapons.Skin;
import fr.olympa.warfare.weapons.ZTASound;
import fr.olympa.warfare.weapons.guns.AttributeModifier.Operation;
import fr.olympa.warfare.weapons.guns.Gun.GunAccuracy;
import fr.olympa.warfare.weapons.guns.Gun.GunMode;
import fr.olympa.warfare.weapons.guns.bullets.Bullet;
import fr.olympa.warfare.weapons.guns.bullets.Bullet.BulletCreator;
import fr.olympa.warfare.weapons.guns.bullets.BulletEffect.BulletEffectCreator;
import fr.olympa.warfare.weapons.guns.bullets.BulletExplosive;
import fr.olympa.warfare.weapons.guns.bullets.BulletSimple;

public enum GunType implements ItemStackable {
	
	REM_870(
			"Remington 870 Express",
			"Fusil à pompe de facture classique.",
			2,
			Material.WOODEN_HOE,
			AmmoType.CARTRIDGE,
			4,
			30,
			18,
			true,
			CommonGunConstants.KNOCKBACK_LOW,
			CommonGunConstants.BULLET_SPEED_LOW,
			GunAccuracy.MEDIUM,
			null,
			4,
			5,
			BulletSimple::new,
			1,
			GunMode.SINGLE,
			null,
			CommonGunConstants.SOUND_VOLUME_MEDIUM,
			ZTASound.GUN_PUMP,
			null),
	AK_20(
			"AK-20",
			"Fustil Kalashnikov ultra-moderne. Sa cadence de tir rapide et la pénétration de ses balles en font une excellente arme pour se défendre des autres joueurs.",
			3,
			Material.GOLDEN_PICKAXE,
			AmmoType.HANDWORKED,
			20,
			6,
			70,
			false,
			CommonGunConstants.KNOCKBACK_LOW,
			CommonGunConstants.BULLET_SPEED_MEDIUM,
			GunAccuracy.HIGH,
			null,
			6,
			3,
			BulletSimple::new,
			1,
			GunMode.SEMI_AUTOMATIC,
			null,
			CommonGunConstants.SOUND_VOLUME_MEDIUM,
			ZTASound.GUN_AUTO,
			null),
	BARRETT(
			"Barrett M109",
			"Fusil sniper anti-matériel. D'une efficacité redoutable contre les infectés comme contre les joueurs lorsque utilisé à grande distance.",
			5,
			Material.IRON_HOE,
			AmmoType.HEAVY,
			1,
			-1,
			90,
			false,
			CommonGunConstants.KNOCKBACK_HIGH,
			CommonGunConstants.BULLET_SPEED_ULTRA_HIGH,
			GunAccuracy.EXTREME,
			new AttributeModifier(Gun.ZOOM_UUID, "zoom", Operation.ADD_MULTIPLICATOR, -3),
			20,
			22,
			new BulletEffectCreator(new PotionEffect(PotionEffectType.SLOW, 40, 1)),
			1,
			GunMode.SINGLE,
			null,
			CommonGunConstants.SOUND_VOLUME_ULTRA_HIGH,
			ZTASound.GUN_BARRETT,
			null),
	BAZOOKA(
			"Bazooka",
			"§c⚠ ARME DE TEST - NE DOIT EN AUCUN CAS ÊTRE SOUS LA POSSESSION D'UN JOUEUR ⚠",
			10,
			Material.GOLDEN_HOE,
			AmmoType.HEAVY,
			2,
			40,
			70,
			false,
			CommonGunConstants.KNOCKBACK_HIGH,
			CommonGunConstants.BULLET_SPEED_LOW,
			GunAccuracy.MEDIUM,
			null,
			0,
			0,
			(gun, playerDamage, entityDamage) -> new BulletExplosive(gun, 5),
			1,
			GunMode.SINGLE,
			null,
			CommonGunConstants.SOUND_VOLUME_MEDIUM,
			ZTASound.GUN_BARRETT,
			null),
	BENELLI(
			"Benelli M5 Super",
			"Fustil de combat rapproché. Très efficace contre les infectés.",
			2,
			Material.STONE_SHOVEL,
			AmmoType.CARTRIDGE,
			8,
			15,
			20,
			true,
			CommonGunConstants.KNOCKBACK_LOW,
			CommonGunConstants.BULLET_SPEED_LOW,
			GunAccuracy.LOW,
			null,
			5,
			8,
			new BulletEffectCreator(new PotionEffect(PotionEffectType.WITHER, 40, 1)),
			1,
			GunMode.SEMI_AUTOMATIC,
			null,
			CommonGunConstants.SOUND_VOLUME_MEDIUM,
			ZTASound.GUN_PUMP,
			null),
	COBRA(
			"Colt King Cobra",
			"Arme de poing polyvalente.",
			2,
			Material.STONE_AXE,
			AmmoType.HEAVY,
			6,
			15,
			80,
			false,
			CommonGunConstants.KNOCKBACK_LOW,
			CommonGunConstants.BULLET_SPEED_HIGH,
			GunAccuracy.HIGH,
			null,
			4,
			4,
			new BulletEffectCreator(new PotionEffect(PotionEffectType.SLOW, 40, 1)),
			1,
			GunMode.SINGLE,
			null,
			CommonGunConstants.SOUND_VOLUME_MEDIUM,
			ZTASound.GUN_GENERIC,
			null),
	DRAGUNOV(
			"SV Dragunov",
			"Fusil de précision russe. D'une efficacité incontestée contre les joueurs, son absence totale de discrétion peut malgré tout être handicapante.",
			4,
			Material.GOLDEN_HOE,
			AmmoType.HANDWORKED,
			5,
			42,
			60,
			false,
			CommonGunConstants.KNOCKBACK_HIGH,
			CommonGunConstants.BULLET_SPEED_ULTRA_HIGH,
			GunAccuracy.EXTREME,
			new AttributeModifier(Gun.ZOOM_UUID, "zoom", Operation.ADD_MULTIPLICATOR, -1),
			18,
			14,
			new BulletEffectCreator(new PotionEffect(PotionEffectType.SLOW, 20, 1)),
			1,
			GunMode.SEMI_AUTOMATIC,
			null,
			CommonGunConstants.SOUND_VOLUME_ULTRA_HIGH,
			ZTASound.GUN_BARRETT,
			null),
	G19(
			"Glock 19",
			"Revolver compact, très fiable, moyennement puissant.",
			2,
			Material.GOLDEN_AXE,
			AmmoType.HANDWORKED,
			15,
			10,
			40,
			false,
			CommonGunConstants.KNOCKBACK_ULTRA_LOW,
			CommonGunConstants.BULLET_SPEED_MEDIUM,
			GunAccuracy.HIGH,
			null,
			4,
			4,
			BulletSimple::new,
			1,
			GunMode.SEMI_AUTOMATIC,
			null,
			CommonGunConstants.SOUND_VOLUME_LOW,
			ZTASound.GUN_GENERIC,
			null),
	KSG(
			"Kel-Tec KSG",
			"Fustil de combat rapproché, dommageant lors des mêlées grâce à ses deux tubes de munitions.",
			2,
			Material.IRON_SHOVEL,
			AmmoType.CARTRIDGE,
			10,
			14,
			100,
			false,
			CommonGunConstants.KNOCKBACK_LOW,
			CommonGunConstants.BULLET_SPEED_ULTRA_LOW,
			GunAccuracy.MEDIUM,
			null,
			3,
			6,
			new BulletEffectCreator(new PotionEffect(PotionEffectType.WITHER, 40, 1)),
			2,
			GunMode.SEMI_AUTOMATIC,
			null,
			CommonGunConstants.SOUND_VOLUME_MEDIUM,
			ZTASound.GUN_PUMP,
			null),
	LUPARA(
			"Lupara",
			"Fusil de chasse à canon scié.",
			1,
			Material.GOLDEN_SHOVEL,
			AmmoType.CARTRIDGE,
			1,
			-1,
			11,
			false,
			CommonGunConstants.KNOCKBACK_ULTRA_LOW,
			CommonGunConstants.BULLET_SPEED_LOW,
			GunAccuracy.LOW,
			null,
			5,
			8,
			new BulletEffectCreator(new PotionEffect(PotionEffectType.WITHER, 40, 1)),
			2,
			GunMode.AUTOMATIC,
			null,
			CommonGunConstants.SOUND_VOLUME_LOW,
			ZTASound.GUN_PUMP,
			null),
	M16(
			"M16",
			"Fusil d'assaut très utilisé par les forces militaires.",
			3,
			Material.WOODEN_PICKAXE,
			AmmoType.HEAVY,
			20,
			5,
			60,
			false,
			CommonGunConstants.KNOCKBACK_MEDIUM,
			CommonGunConstants.BULLET_SPEED_HIGH,
			GunAccuracy.HIGH,
			null,
			4,
			5,
			BulletSimple::new,
			1,
			GunMode.AUTOMATIC,
			GunMode.BLAST,
			CommonGunConstants.SOUND_VOLUME_MEDIUM,
			ZTASound.GUN_AUTO,
			null),
	M1897(
			"Model 1897",
			"Fusil à pompe à faible portée. Puissant contre les joueurs, il l'est encore plus contre les infectés.",
			2,
			Material.WOODEN_SHOVEL,
			AmmoType.CARTRIDGE,
			5,
			30,
			15,
			true,
			CommonGunConstants.KNOCKBACK_MEDIUM,
			CommonGunConstants.BULLET_SPEED_ULTRA_LOW,
			GunAccuracy.LOW,
			null,
			7,
			10,
			new BulletEffectCreator(new PotionEffect(PotionEffectType.WITHER, 40, 1)),
			5,
			GunMode.SINGLE,
			null,
			CommonGunConstants.SOUND_VOLUME_MEDIUM,
			ZTASound.GUN_PUMP,
			null),
	M1911(
			"Colt M1911",
			"Pistolet semi-automatique précis à moyenne portée.",
			1,
			Material.WOODEN_AXE,
			AmmoType.LIGHT,
			7,
			10,
			60,
			false,
			CommonGunConstants.KNOCKBACK_ULTRA_LOW,
			CommonGunConstants.BULLET_SPEED_MEDIUM,
			GunAccuracy.HIGH,
			null,
			3,
			3,
			BulletSimple::new,
			1,
			GunMode.SEMI_AUTOMATIC,
			null,
			CommonGunConstants.SOUND_VOLUME_LOW,
			ZTASound.GUN_GENERIC,
			null),
	P22(
			"Walther P22",
			"Pistolet semi-automatique allemand peu puissant.",
			2,
			Material.IRON_AXE,
			AmmoType.LIGHT,
			10,
			10,
			30,
			false,
			CommonGunConstants.KNOCKBACK_ULTRA_LOW,
			CommonGunConstants.BULLET_SPEED_MEDIUM,
			GunAccuracy.HIGH,
			null,
			4,
			4,
			BulletSimple::new,
			1,
			GunMode.SEMI_AUTOMATIC,
			GunMode.BLAST,
			CommonGunConstants.SOUND_VOLUME_LOW,
			ZTASound.GUN_GENERIC,
			null),
	SDMR(
			"SDM-R",
			"Fustil de précision dérivé du M16.",
			4,
			Material.IRON_PICKAXE,
			AmmoType.HEAVY,
			20,
			6,
			50,
			false,
			CommonGunConstants.KNOCKBACK_MEDIUM,
			CommonGunConstants.BULLET_SPEED_HIGH,
			GunAccuracy.HIGH,
			null,
			5,
			6,
			BulletSimple::new,
			1,
			GunMode.AUTOMATIC,
			null,
			CommonGunConstants.SOUND_VOLUME_MEDIUM,
			ZTASound.GUN_AUTO,
			null),
	SKORPION(
			"Skorpion VZ64",
			"Pistolet-mitrailleur compact à cadence de tir élevée. Extrêmement perforant chez les infectés.",
			3,
			Material.MAGMA_CREAM,
			AmmoType.HANDWORKED,
			24,
			4,
			50,
			false,
			CommonGunConstants.KNOCKBACK_ULTRA_LOW,
			CommonGunConstants.BULLET_SPEED_LOW,
			GunAccuracy.MEDIUM,
			null,
			4,
			7,
			BulletSimple::new,
			1,
			GunMode.AUTOMATIC,
			null,
			CommonGunConstants.SOUND_VOLUME_MEDIUM,
			ZTASound.GUN_AUTO,
			null),
	STONER(
			"Stoner 24",
			"Mitraillette lourde à grande capacité.",
			5,
			Material.IRON_HORSE_ARMOR,
			AmmoType.HEAVY,
			70,
			3,
			70,
			false,
			CommonGunConstants.KNOCKBACK_LOW,
			CommonGunConstants.BULLET_SPEED_MEDIUM,
			GunAccuracy.MEDIUM,
			null,
			2,
			3,
			new BulletEffectCreator(new PotionEffect(PotionEffectType.SLOW, 40, 1)),
			1,
			GunMode.AUTOMATIC,
			null,
			CommonGunConstants.SOUND_VOLUME_MEDIUM,
			ZTASound.GUN_GENERIC,
			new PotionEffect(PotionEffectType.SLOW, 99999999, 1)),
	UZI(
			"UZI",
			"Pistolet-mitrailleur extrêmement rapide, à courte portée. Très efficace dans des lieux étroits contre des infectés.",
			2,
			Material.SLIME_BALL,
			AmmoType.LIGHT,
			25,
			3,
			50,
			false,
			CommonGunConstants.KNOCKBACK_ULTRA_LOW,
			CommonGunConstants.BULLET_SPEED_ULTRA_LOW,
			GunAccuracy.MEDIUM,
			null,
			2,
			6,
			BulletSimple::new,
			1,
			GunMode.AUTOMATIC,
			null,
			CommonGunConstants.SOUND_VOLUME_MEDIUM,
			ZTASound.GUN_AUTO,
			null),
			;
	
	private final String name;
	private final String description;
	private final Material material;
	private final AmmoType ammoType;
	private final int maxAmmos;
	private final int fireRate;
	private final int chargeTime;
	private final boolean oneByOneCharge;
	private final float knockback;
	private final float bulletSpeed;
	private final GunAccuracy accuracy;
	private final AttributeModifier zoomModifier;
	private final float playerDamage;
	private final float entityDamage;
	private final BulletCreator bulletCreator;
	private final int firedBullets;
	private final GunMode primaryMode;
	private final GunMode secondaryMode;
	private final float fireVolume;
	private final ZTASound fireSound;
	private final PotionEffect heldEffect;
	
	private final List<String> lore;
	private final ItemStack demoItem;
	
	private GunType(String name, String description, int tier, Material material, AmmoType ammoType, int maxAmmos, int fireRate, int chargeTime, boolean oneByOneCharge, float knockback, float bulletSpeed, GunAccuracy accuracy, AttributeModifier zoomModifier, int playerDamage, int entityDamage, BulletCreator bulletCreator, int firedBullets, GunMode primaryMode, GunMode secondaryMode, float fireVolume, ZTASound fireSound, PotionEffect heldEffect) {
		this.name = name;
		this.description = description;
		this.material = material;
		this.ammoType = ammoType;
		this.maxAmmos = maxAmmos;
		this.fireRate = fireRate;
		this.chargeTime = chargeTime;
		this.oneByOneCharge = oneByOneCharge;
		this.knockback = knockback;
		this.bulletSpeed = bulletSpeed;
		this.accuracy = accuracy;
		this.zoomModifier = zoomModifier;
		this.playerDamage = playerDamage;
		this.entityDamage = entityDamage;
		this.bulletCreator = bulletCreator;
		this.firedBullets = firedBullets;
		this.primaryMode = primaryMode;
		this.secondaryMode = secondaryMode;
		this.fireVolume = fireVolume;
		this.fireSound = fireSound;
		this.heldEffect = heldEffect;
		
		lore = new ArrayList<>(SpigotUtils.wrapAndAlign(description, 35));
		lore.add("");
		lore.add("§8§lMunitions > " + ammoType.getColoredName() + " §8(x" + maxAmmos + ")");
		
		demoItem = new ItemStack(material);
		ItemMeta meta = demoItem.getItemMeta();
		meta.setDisplayName("§e" + name);
		meta.setLore(lore);
		meta.addItemFlags(ItemFlag.values());
		meta.setCustomModelData(1);
		demoItem.setItemMeta(meta);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getId() {
		return name();
	}
	
	public String getDescription() {
		return description;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	/**
	 * @return Type de munition
	 */
	public AmmoType getAmmoType() {
		return ammoType;
	}
	
	/**
	 * @return Maximum de munitions
	 */
	public int getMaxAmmos() {
		return maxAmmos;
	}
	
	/**
	 * @return Temps (en ticks) entre chaque coup de feu
	 */
	public int getFireRate() {
		return fireRate;
	}
	
	/**
	 * @return Temps (en ticks) avant la recharge complète
	 */
	public int getChargeTime() {
		return chargeTime;
	}
	
	/**
	 * @return <tt>true</tt> si la charge se fait munition par munition
	 */
	public boolean isOneByOneCharge() {
		return oneByOneCharge;
	}
	
	/**
	 * @return Puissance de recul en m/s
	 */
	public float getKnockback() {
		return knockback;
	}
	
	/**
	 * @return Vitesse de la balle en m/s
	 */
	public float getBulletSpeed() {
		return bulletSpeed;
	}
	
	/**
	 * @return Rayon du dispersement des balles (en m)
	 */
	public GunAccuracy getAccuracy() {
		return accuracy;
	}
	
	/**
	 * @return Mode de tir secondaire
	 */
	public AttributeModifier getZoomModifier() {
		return zoomModifier;
	}
	
	public boolean hasZoom() {
		return zoomModifier != null;
	}
	
	/**
	 * @return Dommages donnés aux joueurs par la balle tirée
	 */
	public float getPlayerDamage() {
		return playerDamage;
	}
	
	/**
	 * @return Dommages donnés aux entités par la balle tirée
	 */
	public float getEntityDamage() {
		return entityDamage;
	}
	
	public Bullet createBullet(Gun gun, float playerDamage, float entityDamage) {
		return bulletCreator.create(gun, playerDamage, entityDamage);
	}
	
	public int getFiredBullets() {
		return firedBullets;
	}
	
	/**
	 * @return Mode de tir principal
	 */
	public GunMode getPrimaryMode() {
		return primaryMode;
	}
	
	/**
	 * @return Mode de tir secondaire
	 */
	public GunMode getSecondaryMode() {
		return secondaryMode;
	}
	
	public boolean hasSecondaryMode() {
		return secondaryMode != null;
	}
	
	/**
	 * @return Volume lors du tir de la balle (distance = 16*x)
	 */
	public float getFireVolume() {
		return fireVolume;
	}
	
	public ZTASound getFireSound() {
		return fireSound;
	}
	
	public PotionEffect getHeldEffect() {
		return heldEffect;
	}
	
	public boolean hasHeldEffect() {
		return heldEffect != null;
	}
	
	@Override
	public ItemStack getDemoItem() {
		return demoItem;
	}
	
	@Override
	public void giveItems(Player p) {
		Gun gun = OlympaWarfare.getInstance().gunRegistry.createGun(this);
		if (WarfarePermissions.GROUP_SURVIVANT.hasPermission(OlympaPlayerWarfare.get(p))) {
			gun.setSkin(Skin.GOLD, demoItem);
		}
		p.getInventory().addItem(gun.createItemStack());
	}
	
	public List<String> getLore(){
		return lore;
	}
	
}
