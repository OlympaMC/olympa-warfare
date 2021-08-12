package fr.olympa.warfare.ranking;

import java.sql.SQLException;

import org.bukkit.Location;

import fr.olympa.api.spigot.ranking.AbstractSQLRank;

public class BestKillStreakRank extends AbstractSQLRank {
	
	public BestKillStreakRank(Location location) throws SQLException {
		super("total_kill", location, 10, true);
	}
	
	@Override
	public String getHologramName() {
		return "§c§lMeilleur kill streak";
	}
	
	@Override
	public String getMessageName() {
		return "du meilleur kill streak";
	}
	
	@Override
	protected String getColumn() {
		return "kill_streak_max";
	}
	
}
