package fr.olympa.warfare.classes;

import java.util.Arrays;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import fr.olympa.api.spigot.gui.OlympaGUI;
import fr.olympa.api.spigot.gui.templates.PagedView;
import fr.olympa.api.utils.Prefix;
import fr.olympa.warfare.OlympaPlayerWarfare;

public class ClassListGUI extends PagedView<WarfareClass> {
	
	private OlympaPlayerWarfare player;
	
	public ClassListGUI(OlympaPlayerWarfare player) {
		super(DyeColor.RED, Arrays.asList(WarfareClass.values()));
		this.player = player;
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
	
	public OlympaGUI toGUI() {
		return super.toGUI("Choisissez votre classe", 2);
	}
	
}
