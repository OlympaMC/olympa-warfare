package fr.olympa.warfare;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.weapons.BulletRemovalHandler;
import io.netty.channel.Channel;

public class WarfareListener implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		OlympaWarfare.getInstance().getTask().runTask(() -> {
			p.setResourcePack(OlympaWarfare.getInstance().resourcePackCommand.url, OlympaWarfare.getInstance().resourcePackCommand.hash);
		});
		Channel channel = ((CraftPlayer) p).getHandle().playerConnection.networkManager.channel;
		channel.pipeline().addBefore("packet_handler", "bullet_remove", new BulletRemovalHandler());
	}
	
	@EventHandler
	public void onResourcePack(PlayerResourcePackStatusEvent e) {
		Player p = e.getPlayer();
		switch (e.getStatus()) {
		case ACCEPTED:
			Prefix.DEFAULT.sendMessage(p, "§eChargement du pack de resources §6§lOlympa ZTA§e...");
			break;
		case DECLINED:
			Prefix.BAD.sendMessage(p, "Tu as désactivé l'utilisation du pack de resources. Pour plus de fun et une meilleure expérience de jeu, accepte-le depuis ton menu Multijoueur !");
			break;
		case FAILED_DOWNLOAD, SUCCESSFULLY_LOADED:
			if (e.getStatus() == Status.FAILED_DOWNLOAD) {
				Prefix.ERROR.sendMessage(p, "Une erreur est survenue lors du téléchargement du pack de resources. Reconnectez-vous pour réessayer !");
			}else {
				Prefix.DEFAULT_GOOD.sendMessage(p, "Le pack de resources §6§lOlympa ZTA§a est désormais chargé ! Bon jeu !");
			}
			break;
		default:
			break;
		}
	}
	
}
