package fr.olympa.warfare.xp;

import fr.olympa.api.common.observable.Observable.Observer;
import fr.olympa.warfare.OlympaPlayerWarfare;
import net.md_5.bungee.api.ChatColor;

public class XPManagement implements Observer {
	
	public static final int[] XP_PER_LEVEL =
			{
				-1,
				10,
				15,
				15,
				20,
				25,
				20,
				30,
				40,
				40,
				50,
				50,
				50,
				60,
				60,
				50,
				50,
				50,
				50,
				50,
				50,
				60,
				60,
				60,
				60,
				60,
				60,
				60,
				60,
				60,
				60,
				70,
				70,
				70,
				70,
				70,
				150,
				70,
				70,
				70,
				70,
				80,
				80,
				80,
				80,
				80,
				80,
				80,
				80,
				80,
				80,
				90,
				90,
				90,
				90,
				90,
				90,
				90,
				80,
				90,
				100,
				100,
				100,
				100,
				100,
				100,
				100,
				100,
				100,
				100,
				110,
				110,
				110,
				110,
				110,
				110,
				110,
				110,
				110,
				110,
				120,
				120,
				110,
				150,
				140,
				120,
				120,
				120,
				130,
				130,
				130,
				130,
				130,
				130,
				130,
				130,
				130,
				130,
				150,
				200,
				Short.MAX_VALUE };
	
	private OlympaPlayerWarfare player;
	
	public XPManagement(OlympaPlayerWarfare player) {
		this.player = player;
	}
	
	@Override
	public void changed() throws Exception {
		int xpToLevelUp = getXPToLevelUp(player.getLevel());
		if (player.getXP() >= xpToLevelUp) {
			player.setLevel(player.getLevel() + 1);
			player.setXP(player.getXP() - xpToLevelUp);
		}
	}
	
	public static int getXPToLevelUp(int level) {
		return XP_PER_LEVEL[level];
	}
	
	public static String formatExperience(double xp) {
		return xp >= Short.MAX_VALUE ? "∞" : Integer.toString((int) xp);
	}
	
	public static ChatColor getLevelColor(int level) {
		if (level >= 70) return ChatColor.GOLD;
		if (level >= 50) return ChatColor.LIGHT_PURPLE;
		if (level >= 30) return ChatColor.BLUE;
		if (level >= 10) return ChatColor.GREEN;
		return ChatColor.GRAY;
	}
	
	public static String getLevelPrefix(int level) {
		return getLevelColor(level).toString() + "[lvl " + level + "]";
	}
	
}
