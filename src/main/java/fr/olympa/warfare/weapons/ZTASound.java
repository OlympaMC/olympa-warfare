package fr.olympa.warfare.weapons;

import java.util.concurrent.ThreadLocalRandom;

public enum ZTASound {
	GUN_PUMP("zta.guns.pump"),
	GUN_AUTO("zta.guns.auto", "zta.guns.auto_far"),
	GUN_BARRETT("zta.guns.barrett"),
	GUN_GENERIC("zta.guns.generic", "zta.guns.generic_far"),
	EXPLOSION_CIVIL("zta.explosions.civil"),
	ZOMBIE_AMBIENT("entity.zombie.ambient"),
	HELICO_LANDING("zta.quests.helicopter_landing");

	private final String sound, farSound;

	ZTASound(String sound) {
		this(sound, sound);
	}

	ZTASound(String sound, String farSound) {
		this.sound = sound;
		this.farSound = farSound;
	}

	public String getSound() {
		return sound;
	}

	public String getFarSound() {
		return farSound;
	}

	public static ZTASound getRandom() {
		return ZTASound.values()[ThreadLocalRandom.current().nextInt(ZTASound.values().length)];
	}

}