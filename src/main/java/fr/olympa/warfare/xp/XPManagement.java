package fr.olympa.warfare.xp;

import fr.olympa.api.common.observable.Observable.Observer;
import fr.olympa.warfare.OlympaPlayerWarfare;
import net.md_5.bungee.api.ChatColor;

public class XPManagement implements Observer {
	
	public static final int[] XP_PER_LEVEL =
			{
				-1,
				20,
				25,
				30,
				35,
				35,
				40,
				45,
				45,
				50,
				60,
				70,
				80,
				90,
				90,
				95,
				95,
				100,
				110,
				110,
				115,
				115,
				115,
				120,
				120,
				125,
				125,
				130,
				130,
				130,
				130,
				135,
				135,
				135,
				140,
				140,
				150,
				145,
				145,
				150,
				150,
				150,
				155,
				155,
				155,
				160,
				165,
				165,
				165,
				170,
				170,
				170,
				175,
				175,
				180,
				180,
				180,
				185,
				185,
				190,
				200,
				200,
				200,
				200,
				200,
				200,
				200,
				200,
				200,
				200,
				210,
				210,
				210,
				210,
				210,
				210,
				210,
				210,
				210,
				210,
				220,
				220,
				210,
				250,
				240,
				220,
				220,
				220,
				230,
				230,
				230,
				230,
				230,
				230,
				230,
				230,
				230,
				230,
				250,
				500,
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
