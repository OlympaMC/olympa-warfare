package fr.olympa.warfare.teamdeathmatch;

public enum GameStep {
	
	/*SHRINK_1(),
	SHRINK_2(),*/
	GLOWING(
			10 * 60 * 20,
			"Brillance"),
			;
	
	private long wait;
	private String title;
	
	private GameStep(long wait, String title) {
		this.wait = wait;
		this.title = title;
	}
	
	public long getWait() {
		return wait;
	}
	
	public String getTitle() {
		return title;
	}
	
}
