package flounder.engine.entrance;

public abstract class IManagerGUI {
	/**
	 * Creates the main GUI system.
	 */
	public abstract void init();

	/**
	 * Checks inputs and updates Guis.
	 */
	public abstract void update();

	/**
	 * @return If the main menu is open.
	 */
	public abstract boolean isMenuIsOpen();

	/**
	 * Forces the main GUI to open.
	 */
	public abstract void openMenu();

	/**
	 * @return If the main menu's blur factor.
	 */
	public abstract float getBlurFactor();
}