package fr.olympa.warfare.xp;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import fr.olympa.api.common.observable.Observable.Observer;
import fr.olympa.api.spigot.utils.SpigotUtils;
import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.OlympaPlayerWarfare;
import fr.olympa.warfare.OlympaWarfare;
import net.md_5.bungee.api.ChatColor;

public class LevelManagement implements Observer {

	private OlympaPlayerWarfare player;

	public LevelManagement(OlympaPlayerWarfare player) {
		this.player = player;
	}

	@Override
	public void changed() throws Exception {
		Player p = (Player) player.getPlayer();
		int newLevel = player.getLevel();
		Prefix.DEFAULT_GOOD.sendMessage(p, "Félicitations ! §lTu passes au niveau §2%d§a§l !", newLevel);
		if (newLevel % 5 == 0) {
			ChatColor color = XPManagement.getLevelColor(newLevel);
			String message = "§d§k||§r " + color + p.getName() + "§7 passe au niveau " + color + newLevel + "§7 !";
			Bukkit.getOnlinePlayers().stream().filter(x -> x != p).forEach(x -> x.sendMessage(message));
		}

		Runnable launchFirework = () -> {
			Firework firework = p.getWorld().spawn(p.getLocation(), Firework.class);
			FireworkMeta meta = firework.getFireworkMeta();
			meta.setPower(0);
			meta.addEffect(FireworkEffect.builder().with(Type.BURST).withColor(Color.LIME).withFade(Color.GREEN).withTrail().build());
			firework.setFireworkMeta(meta);
		};
		
		SpigotUtils.runPrimaryThread(launchFirework, OlympaWarfare.getInstance());
	}

}
