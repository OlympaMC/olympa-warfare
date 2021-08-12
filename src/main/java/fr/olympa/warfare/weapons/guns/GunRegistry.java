package fr.olympa.warfare.weapons.guns;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import fr.olympa.warfare.OlympaWarfare;

public class GunRegistry {
	
	public static final NamespacedKey GUN_KEY = new NamespacedKey(OlympaWarfare.getInstance(), "gunRegistry");
	
	public final Map<Integer, Gun> registry = new ConcurrentHashMap<>(200);
	
	private int lastID = 0;
	
	/**
	 * Chercher dans le registre l'objet correspondant à l'ID
	 * @param id ID de l'objet
	 * @return objet correspondant à l'ID spécifié
	 */
	public Gun getGun(int id) {
		return registry.get(id);
	}
	
	public int getRegistrySize() {
		return registry.size();
	}
	
	/**
	 * Chercher dans le registre l'objet correspondant à l'item
	 * @param is Item pour lequel récupérer l'objet. Doit contenir une ligne de lore <code>[Ixxxxxx]</code>
	 * @return Objet correspondant à l'immatriculation de l'item. Peut renvoyer <i>null</i>.
	 */
	public Gun getGun(ItemStack is) {
		if (is == null) return null;
		if (!is.hasItemMeta()) return null;
		ItemMeta im = is.getItemMeta();
		
		int id = im.getPersistentDataContainer().getOrDefault(GUN_KEY, PersistentDataType.INTEGER, -1);
		return id != 1 ? registry.get(id) : null;
	}
	
	public void ifGun(ItemStack item, Consumer<Gun> consumer) {
		Gun gun = getGun(item);
		if (gun != null) consumer.accept(gun);
	}
	
	public synchronized Gun createGun(GunType type) throws SQLException {
		int id = lastID++;
		Gun gun = new Gun(id, type);
		registry.put(id, gun);
		return gun;
	}
	
}
