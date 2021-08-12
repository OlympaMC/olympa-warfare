package fr.olympa.warfare.weapons.guns.bullets;

import java.util.Random;

import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.olympa.warfare.OlympaWarfare;
import fr.olympa.warfare.weapons.guns.Gun;

public abstract class Bullet{
	
	protected static final Random random = new Random();
	
	private final FixedMetadataValue metadata = new FixedMetadataValue(OlympaWarfare.getInstance(), this);
	
	protected final float speed;
	protected final float spread;
	
	public Bullet(Gun gun){
		this.speed = gun.bulletSpeed.getValue();
		this.spread = gun.bulletSpread.getValue();
	}
	
	public Bullet(float speed, float spread) {
		this.speed = speed;
		this.spread = spread;
	}
	
	public void launchProjectile(Player p, Vector direction) {
		Vector velocity = direction.multiply(speed);

		if (spread != 0) {
			float bulletSpreadHalf = spread / 2;
			velocity.add(new Vector(random.nextFloat() * spread - bulletSpreadHalf, random.nextFloat() * spread - bulletSpreadHalf, random.nextFloat() * spread - bulletSpreadHalf));
		}

		boolean highVelocity = speed >= 4.5;
		Projectile projectile = p.launchProjectile(highVelocity ? LlamaSpit.class : Snowball.class, velocity);
		projectile.setMetadata("bullet", metadata);
		projectile.setBounce(false);
		projectile.setPersistent(false);

		if (highVelocity) { // nécessaire ? à supprimer peut-être
			new BukkitRunnable() {
				World world = projectile.getWorld();
				int previousTicksLived;
				
				@Override
				public void run() {
					if (projectile.isValid()) {
						if (projectile.getTicksLived() == previousTicksLived) return;
						previousTicksLived = projectile.getTicksLived();
						Vector velocity = projectile.getVelocity();
						world.spawnParticle(Particle.SMOKE_LARGE, projectile.getLocation(), 0, velocity.getX(), velocity.getY(), velocity.getZ(), /*velocity.normalize().length()*/ 1, null, true);
					}else cancel();
				}
			}.runTaskTimerAsynchronously(OlympaWarfare.getInstance(), 0, 4);
		}
	}
	
	/**
	 * @param e Event appelé quand la balle touche quelque chose/quelqu'un
	 */
	public abstract void hit(ProjectileHitEvent e);
	
	@FunctionalInterface
	public static interface BulletCreator {
		public Bullet create(Gun gun, float playerDamage, float entityDamage);
	}
	
}
