package fr.olympa.warfare.teamdeathmatch;

public enum GameStep {
	
	/*SHRINK_1(),
	SHRINK_2(),*/
	GLOWING(
			4 * 60,
			"Brillance"),
			;
	
	private int wait;
	private String title;
	
	private GameStep(int wait, String title) {
		this.wait = wait;
		this.title = title;
	}
	
	public int getWait() {
		return wait;
	}
	
	public String getTitle() {
		return title;
	}
	
}
