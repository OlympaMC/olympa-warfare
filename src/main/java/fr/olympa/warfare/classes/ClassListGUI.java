package fr.olympa.warfare.classes;

import java.util.Arrays;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import fr.olympa.api.spigot.gui.templates.PagedGUI;
import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.OlympaPlayerWarfare;

public class ClassListGUI extends PagedGUI<WarfareClass> {
	
	private OlympaPlayerWarfare player;
	
	public ClassListGUI(OlympaPlayerWarfare player) {
		super("Choisissez votre classe", DyeColor.RED, Arrays.asList(WarfareClass.values()), 2, false);
		this.player = player;
		setItems();
	}
	
	@Override
	public ItemStack getItemStack(WarfareClass object) {
		return object.getItem(player.getLevel() >= object.getMinLevel());
	}
	
	@Override
	public void click(WarfareClass existing, Player p, ClickType click) {
		if (false && existing.getMinLevel() > player.getLevel()) {
			Prefix.DEFAULT_BAD.sendMessage(p, "Tu n'as pas le niveau requis pour prendre cette classe.");
		}else {
			player.tdmPlayer.usedClass.set(existing);
			Prefix.ERROR.sendMessage(p, "Les niveaux des classes sont temporairement désactivés.");
			Prefix.DEFAULT_GOOD.sendMessage(p, "Tu as sélectionné la classe %s.", existing.getName());
		}
	}
	
}
