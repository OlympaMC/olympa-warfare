package fr.olympa.warfare.weapons.guns.bullets;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import fr.olympa.warfare.weapons.Knife;
import fr.olympa.warfare.weapons.WeaponsListener;
import fr.olympa.warfare.weapons.guns.Gun;
import net.minecraft.server.v1_16_R3.DamageSource;

public class BulletSimple extends Bullet{
	
	public static final int NO_DAMAGE_TICKS = 1;

	private float playerDamage, entityDamage;
	
	public BulletSimple(Gun gun, float playerDamage, float entityDamage){
		super(gun);
		this.playerDamage = playerDamage;
		this.entityDamage = entityDamage;
	}
	
	public BulletSimple(float speed, float spread, float playerDamage, float entityDamage) {
		super(speed, spread);
		this.playerDamage = playerDamage;
		this.entityDamage = entityDamage;
	}
	
	@Override
	public void hit(ProjectileHitEvent e){
		Player shooter = (Player) e.getEntity().getShooter();
		if (e.getHitEntity() != null && e.getHitEntity() instanceof LivingEntity) {
			LivingEntity hitEntity = (LivingEntity) e.getHitEntity();
			WeaponsListener.cancelDamageEvent = true;
			if (hitEntity instanceof ArmorStand) return;
			if (hitEntity.isInvulnerable()) return;
			float damage = hitEntity instanceof Player ? playerDamage : entityDamage;

			boolean npc = /*CitizensAPI.getNPCRegistry().isNPC(shooter)*/ false;
			boolean stats = !hitEntity.hasMetadata("training") && !npc;
			Location blood;
			if (isHeadShot(e.getEntity(), hitEntity)) {
				damage *= 1.5;
				shooter.playSound(shooter.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 1);
				blood = hitEntity.getEyeLocation();
				//if (stats) OlympaPlayerZTA.get(shooter).headshots.increment();
			}else {
				blood = hitEntity.getLocation().add(0, 1, 0);
				//if (stats) OlympaPlayerZTA.get(shooter).otherShots.increment();
			}
			Knife.spawnBlood(blood, 5);
			damage(hitEntity, shooter, e.getEntity(), damage);
		}
	}
	
	public boolean isHeadShot(Projectile proj, LivingEntity entity) {
		BoundingBox bb = BoundingBox.of(entity.getEyeLocation(), 0.5, 0.5, 0.5);
		for (double i = 0.0D; i <= super.speed; i += 0.8D) {
			Vector finalLoc = proj.getLocation().toVector();
			Vector direction = proj.getVelocity().normalize();
			direction.multiply(i);
			finalLoc.add(direction);
			if (bb.contains(finalLoc)) return true;
		}
		return false;
	}

	public void damage(LivingEntity entity, LivingEntity damager, Entity projectile, float damage) {
		damage = damage + random.nextFloat() - 0.5f;
		((CraftLivingEntity) entity).getHandle().damageEntity(DamageSource.projectile(((CraftEntity) projectile).getHandle(), ((CraftEntity) damager).getHandle()), damage);
		//((CraftLivingEntity) entity).getHandle().damageEntity(DamageSource.mobAttack(((CraftLivingEntity) damager).getHandle()).c(), damage);
		//entity.damage(damage, damager);
		entity.setNoDamageTicks(NO_DAMAGE_TICKS);
	}

}
