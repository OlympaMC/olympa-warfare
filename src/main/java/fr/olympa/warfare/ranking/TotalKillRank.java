package fr.olympa.warfare.ranking;

import java.sql.SQLException;

import org.bukkit.Location;

import fr.olympa.api.spigot.ranking.AbstractSQLRank;

public class TotalKillRank extends AbstractSQLRank {
	
	public TotalKillRank(Location location) throws SQLException {
		super("total_kill", location, 10, true);
	}
	
	@Override
	public String getHologramName() {
		return "§e§lKills totaux";
	}
	
	@Override
	public String getMessageName() {
		return "des kills";
	}
	
	@Override
	protected String getColumn() {
		return "kills";
	}
	
}
