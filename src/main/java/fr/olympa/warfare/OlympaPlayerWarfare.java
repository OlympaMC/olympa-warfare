package fr.olympa.warfare;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import fr.olympa.api.common.observable.ObservableDouble;
import fr.olympa.api.common.observable.ObservableInt;
import fr.olympa.api.common.provider.AccountProviderAPI;
import fr.olympa.api.common.provider.OlympaPlayerObject;
import fr.olympa.api.common.sql.SQLColumn;
import fr.olympa.core.spigot.OlympaCore;
import fr.olympa.warfare.teamdeathmatch.TDMPlayer;
import fr.olympa.warfare.xp.LevelManagement;
import fr.olympa.warfare.xp.XPManagement;

public class OlympaPlayerWarfare extends OlympaPlayerObject {

	private static final SQLColumn<OlympaPlayerWarfare> COLUMN_LEVEL = new SQLColumn<OlympaPlayerWarfare>("level", "SMALLINT UNSIGNED NULL DEFAULT 1", Types.SMALLINT).setUpdatable();
	private static final SQLColumn<OlympaPlayerWarfare> COLUMN_XP = new SQLColumn<OlympaPlayerWarfare>("xp", "INTEGER UNSIGNED NULL DEFAULT 0", Types.INTEGER).setUpdatable();
	private static final SQLColumn<OlympaPlayerWarfare> COLUMN_KILLS = new SQLColumn<OlympaPlayerWarfare>("kills", "INTEGER UNSIGNED NULL DEFAULT 0", Types.INTEGER).setUpdatable();

	public static final List<SQLColumn<OlympaPlayerWarfare>> COLUMNS = Arrays.asList(COLUMN_LEVEL, COLUMN_XP, COLUMN_KILLS);

	private ObservableInt level = new ObservableInt(1);
	private ObservableDouble xp = new ObservableDouble(0);
	private ObservableInt kills = new ObservableInt(0);

	public TDMPlayer tdmPlayer;
	
	public OlympaPlayerWarfare(UUID uuid, String name, String ip) {
		super(uuid, name, ip);
	}

	@Override
	public void loaded() {
		level.observe("datas", () -> COLUMN_LEVEL.updateAsync(this, level.get(), null, null));
		level.observe("levelManagement", new LevelManagement(this));
		level.observe("tab_update", () -> OlympaCore.getInstance().getNameTagApi().callNametagUpdate(this));
		level.observe("xp_bar", this::updateXPBar);
		xp.observe("datas", () -> COLUMN_XP.updateAsync(this, xp.get(), null, null));
		xp.observe("xpManagement", new XPManagement(this));
		xp.observe("xp_bar", this::updateXPBar);
		kills.observe("datas", () -> COLUMN_KILLS.updateAsync(this, kills.get(), null, null));
		kills.observe("ranking", () -> OlympaWarfare.getInstance().totalKillRank.handleNewScore(getName(), (Player) getPlayer(), kills.get()));
	}

	public int getLevel() {
		return level.get();
	}

	public void setLevel(int level) {
		this.level.set(Math.min(Math.max(level, 1), XPManagement.XP_PER_LEVEL.length - 1));
	}

	public double getXP() {
		return xp.get();
	}

	public void setXP(double xp) {
		this.xp.set(Math.min(Math.max(xp, 0), Short.MAX_VALUE));
	}

	public void updateXPBar() {
		Player p = (Player) getPlayer();
		p.setLevel(level.get());
		float xpRatio = xp.getAsFloat() / XPManagement.getXPToLevelUp(level.get());
		if (xpRatio <= 1)
			p.setExp(xpRatio);
	}

	public ObservableInt getKills() {
		return kills;
	}

	@Override
	public void loadDatas(ResultSet resultSet) throws SQLException {
		level.set(resultSet.getInt("level"));
		xp.set(resultSet.getInt("xp"));
		kills.set(resultSet.getInt("kills"));
		updateXPBar();
	}

	public static OlympaPlayerWarfare get(Player p) {
		return AccountProviderAPI.getter().get(p.getUniqueId());
	}

}
