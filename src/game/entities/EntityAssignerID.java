package game.entities;

/**
 * Generates unique IDs for entity components. This should only be used to initialize constants, and should not be used in running code.
 */
public class EntityAssignerID {
	private static int currentId = 0;

	/**
	 * Returns a new integer with each call.
	 *
	 * @return A new, unique integer ID.
	 */
	public static int getId() {
		return currentId++;
	}
}
