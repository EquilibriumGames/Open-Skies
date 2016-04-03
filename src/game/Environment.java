package game;

public class Environment {
	private static Fog fog;

	/**
	 * Initializes the start game environment.
	 *
	 * @param fog The fog to be used in the world.
	 */
	public static void init(Fog fog) {
		Environment.fog = fog;
	}

	public static Fog getFog() {
		return fog;
	}
}
