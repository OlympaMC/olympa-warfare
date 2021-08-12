package fr.olympa.warfare.spawning;

import java.sql.SQLException;
import java.sql.Types;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.olympa.api.common.sql.SQLColumn;
import fr.olympa.api.common.sql.SQLTable;
import fr.olympa.api.spigot.utils.SpigotUtils;
import fr.olympa.warfare.OlympaWarfare;

public class SpawnPointsManager {
	
	private SQLTable<Location> table;
	
	private List<Location> locations;
	
	public SpawnPointsManager() throws SQLException {
		table = new SQLTable<>("pvpkit_spawnpoints", Arrays.asList(new SQLColumn<Location>("location", "VARCHAR(100)", Types.VARCHAR).setPrimaryKey(SpigotUtils::convertLocationToString)), resultSet -> SpigotUtils.convertStringToLocation(resultSet.getString("location")));
		table.createOrAlter();
		
		locations = table.selectAll(null);
	}
	
	public List<Location> getLocations() {
		return locations;
	}
	
	public Location getRandomLocation() {
		if (locations.isEmpty()) return null;
		return locations.get(ThreadLocalRandom.current().nextInt(locations.size()));
	}
	
	public Location getBestLocation() {
		if (locations.isEmpty()) return null;
		
		List<SimpleEntry<Location, Double>> list = locations.stream()
				.map(location -> new AbstractMap.SimpleEntry<>(location, getClosestEntityDistance(location))) // map locations with their closest entity distance
				.sorted((o1, o2) -> Double.compare(o1.getValue(), o2.getValue())) // sort stream by distances
				.collect(Collectors.toList());
		
		for (int distance = 25; distance >= 0; distance -= 5) {
			int minDistance = distance;
			List<SimpleEntry<Location, Double>> possible = list.stream().filter(x -> x.getValue().doubleValue() > minDistance).collect(Collectors.toList());
			if (!possible.isEmpty()) {
				return possible.get(ThreadLocalRandom.current().nextInt(possible.size())).getKey();
			}
		}
		
		OlympaWarfare.getInstance().sendMessage("§cUn problème est survenu lors de la sélection du point de spawn.");
		return locations.get(0);
	}
	
	private double getClosestEntityDistance(Location location) {
		return location.getWorld().getNearbyEntities(location, 25, 25, 25, x -> x instanceof Player).stream().mapToDouble(entity -> entity.getLocation().distanceSquared(location)).sorted().findFirst().orElse(Double.MAX_VALUE);
	}
	
	public void addSpawnPoint(Location location) throws SQLException {
		table.insert(SpigotUtils.convertLocationToString(location));
		locations.add(location);
	}
	
}
