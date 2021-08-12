package fr.olympa.warfare.weapons;

import java.util.Arrays;

public enum Skin {
	
	NORMAL(
			0,
			"normal"),
	GOLD(
			1,
			"doré");

	private int id;
	private String name;
	
	private Skin(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public static Skin getFromId(int id) {
		return Arrays.stream(values()).filter(x -> x.getId() == id).findAny().orElse(NORMAL);
	}
	
}
