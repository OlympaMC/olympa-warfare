package fr.olympa.warfare.teamdeathmatch;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import fr.olympa.api.common.observable.ObservableInt;
import fr.olympa.api.common.observable.ObservableValue;
import fr.olympa.api.spigot.lines.DynamicLine;
import fr.olympa.api.spigot.scoreboard.sign.Scoreboard;
import fr.olympa.core.spigot.OlympaCore;
import fr.olympa.warfare.OlympaPlayerWarfare;
import fr.olympa.warfare.OlympaWarfare;
import fr.olympa.warfare.classes.WarfareClass;

public class TDMPlayer {
	
	public static final DynamicLine<Scoreboard<OlympaPlayerWarfare>> LINE_CLASS = new DynamicLine<>(x -> "§7§lClasse: §a§l" + x.getOlympaPlayer().tdmPlayer.usedClass.mapOr(WarfareClass::getName, "§cnon choisi"));
	public static final DynamicLine<Scoreboard<OlympaPlayerWarfare>> LINE_LIVES = new DynamicLine<>(x -> "§7§lVies: " + x.getOlympaPlayer().tdmPlayer.getLivesString());
	
	private final Player player;
	private final OlympaPlayerWarfare olympaPlayer;
	
	public final ObservableValue<WarfareClass> usedClass = new ObservableValue<>(null);
	public final ObservableInt lives = new ObservableInt(3);
	public final ObservableInt points = new ObservableInt(0);
	
	public Team team = null;
	public boolean isLiving = true;
	public BukkitTask respawn = null;
	
	public TDMPlayer(Player player) {
		this.player = player;
		this.olympaPlayer = OlympaPlayerWarfare.get(player);
		
		olympaPlayer.tdmPlayer = this;
		
		usedClass.observe("scoreboard_update", () -> LINE_CLASS.updateHolder(OlympaWarfare.getInstance().scoreboards.getPlayerScoreboard(olympaPlayer)));
		lives.observe("scoreboard_update", () -> LINE_LIVES.updateHolder(OlympaWarfare.getInstance().scoreboards.getPlayerScoreboard(olympaPlayer)));
		lives.observe("tab_update", () -> OlympaCore.getInstance().getNameTagApi().callNametagUpdate(olympaPlayer));
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public OlympaPlayerWarfare getOlympaPlayer() {
		return olympaPlayer;
	}
	
	public String getLivesString() {
		return lives.get() == 0 ? "§4✖" : "§c" + "❤".repeat(lives.get());
	}
	
	public void cancelRespawn() {
		if (respawn != null) {
			respawn.cancel();
			respawn = null;
		}
	}
	
}
