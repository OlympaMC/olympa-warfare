package fr.olympa.warfare.kits;

import java.util.Arrays;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import fr.olympa.api.spigot.gui.templates.PagedGUI;
import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.OlympaPlayerWarfare;

public class KitListGUI extends PagedGUI<Kits> {
	
	private OlympaPlayerWarfare player;
	
	public KitListGUI(OlympaPlayerWarfare player) {
		super("Choisissez votre kit", DyeColor.RED, Arrays.asList(Kits.values()), 2, false);
		this.player = player;
		setItems();
	}
	
	@Override
	public ItemStack getItemStack(Kits object) {
		return object.getItem(player.getLevel() >= object.getMinLevel());
	}
	
	@Override
	public void click(Kits existing, Player p, ClickType click) {
		if (false && existing.getMinLevel() > player.getLevel()) {
			Prefix.DEFAULT_BAD.sendMessage(p, "Tu n'as pas le niveau requis pour prendre ce kit.");
		}else {
			player.usedKit.set(existing);
			Prefix.ERROR.sendMessage(p, "Les niveaux des kits sont temporairement désactivés.");
			Prefix.DEFAULT_GOOD.sendMessage(p, "Tu as sélectionné le kit %s.", existing.getName());
		}
	}
	
}
