package fr.olympa.warfare.weapons.guns.bullets;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import fr.olympa.warfare.weapons.guns.Gun;

public class BulletEffect extends BulletSimple{
	
	private PotionEffect effect;
	
	public BulletEffect(Gun gun, float playerDamage, float entityDamage, PotionEffect effect){
		super(gun, playerDamage, entityDamage);
		this.effect = effect;
	}
	
	@Override
	public void damage(LivingEntity entity, LivingEntity damager, Entity projectile, float damage) {
		super.damage(entity, damager, projectile, damage);
		entity.addPotionEffect(effect);
	}
	
	public static class BulletEffectCreator implements BulletCreator {
		
		private PotionEffect effect;
		
		public BulletEffectCreator(PotionEffect effect) {
			this.effect = effect;
		}
		
		@Override
		public Bullet create(Gun gun, float playerDamage, float entityDamage) {
			return new BulletEffect(gun, playerDamage, entityDamage, effect);
		}
		
	}
	
}
