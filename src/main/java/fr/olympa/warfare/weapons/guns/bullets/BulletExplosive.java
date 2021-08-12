package fr.olympa.warfare.weapons.guns.bullets;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ProjectileHitEvent;

import fr.olympa.warfare.weapons.guns.Gun;

public class BulletExplosive extends Bullet{
	
	private float power;
	
	public BulletExplosive(Gun gun, float power) {
		super(gun);
		this.power = power;
	}
	
	public void hit(ProjectileHitEvent e){
		Location lc;
		if (e.getHitEntity() != null) {
			lc = e.getHitEntity().getLocation();
		}else {
			lc = e.getHitBlock().getLocation();
			if (e.getHitBlockFace() != null) lc.add(e.getHitBlockFace().getModX(), e.getHitBlockFace().getModY(), e.getHitBlockFace().getModZ());
		}
		lc.getWorld().createExplosion(lc.getX(), lc.getY(), lc.getZ(), power, false, false, (Entity) e.getEntity().getShooter());
	}
	
}
