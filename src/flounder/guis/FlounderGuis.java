package flounder.guis;

import flounder.devices.*;
import flounder.events.*;
import flounder.framework.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;

/**
 * A module used for that manages GUI textures in a container.
 */
public class FlounderGuis extends Module {
	private static final FlounderGuis INSTANCE = new FlounderGuis();
	public static final String PROFILE_TAB_NAME = "Guis";

	public static final MyFile GUIS_LOC = new MyFile(MyFile.RES_FOLDER, "guis");

	private GuiMaster guiMaster;
	private GuiSelector selector;
	private ScreenObject container;

	/**
	 * Creates a new GUI manager.
	 */
	public FlounderGuis() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderEvents.class, FlounderDisplay.class, FlounderJoysticks.class, FlounderKeyboard.class, FlounderMouse.class, FlounderShaders.class, FlounderSound.class, FlounderTextures.class);
		guiMaster = null;
	}

	@Override
	public void init() {
		this.selector = new GuiSelector();
		this.container = new GuiObjectScreen();
	}

	@Override
	public void update() {
		GuiMaster newManager = (GuiMaster) getExtensionMatch(guiMaster, GuiMaster.class, true);

		if (newManager != null) {
			if (guiMaster != null) {
				guiMaster.dispose();
				guiMaster.setInitialized(false);
			}

			guiMaster = newManager;
		}

		if (guiMaster != null) {
			if (!guiMaster.isInitialized()) {
				guiMaster.init();
				guiMaster.setInitialized(true);
			}
		}

		selector.update();
		container.update();

		if (guiMaster != null) {
			guiMaster.update();
		}
	}

	@Override
	public void profile() {
		FlounderProfiler.add(PROFILE_TAB_NAME, "Selected", guiMaster == null ? "NULL" : guiMaster.getClass());
	}

	/**
	 * Gets the GUI master.
	 *
	 * @return The GUI master.
	 */
	public static GuiMaster getGuiMaster() {
		return INSTANCE.guiMaster;
	}

	/**
	 * Gets the screen container.
	 *
	 * @return The screen container.
	 */
	public static ScreenObject getContainer() {
		return INSTANCE.container;
	}

	/**
	 * Gets the main GUI selector.
	 *
	 * @return The GUI selector.
	 */
	public static GuiSelector getSelector() {
		return INSTANCE.selector;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		if (guiMaster != null) {
			guiMaster.dispose();
			guiMaster.setInitialized(false);
		}

		container.delete();
	}
}
