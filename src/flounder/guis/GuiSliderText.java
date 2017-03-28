/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package flounder.guis;

import flounder.devices.*;
import flounder.fonts.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;
import flounder.visual.*;

public class GuiSliderText extends ScreenObject {
	private static final TextureObject TEXTURE_BACKGROUND = TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "buttonText.png")).create();

	private TextObject text;
	private GuiObject background;
	private GuiObject slider;

	private boolean updating;
	private float minProgress;
	private float maxProgress;
	private float progress;

	private boolean mouseOver;

	private boolean hasChange;
	private Timer changeTimeout;
	private ScreenListener listenerChange;

	public GuiSliderText(ScreenObject parent, Vector2f position, String string, float minProgress, float maxProgress, float progress, GuiAlign align) {
		super(parent, position, new Vector2f());

		this.text = new TextObject(this, this.getPosition(), string, GuiButtonText.SCALE_NORMAL, FlounderFonts.CANDARA, 0.36f, align);
		this.text.setInScreenCoords(true);
		this.text.setColour(new Colour(1.0f, 1.0f, 1.0f));

		this.background = new GuiObject(this, this.getPosition(), new Vector2f(), TEXTURE_BACKGROUND, 1);
		this.background.setInScreenCoords(true);
		this.background.setColourOffset(new Colour());

		this.slider = new GuiObject(this, this.getPosition(), new Vector2f(), TEXTURE_BACKGROUND, 1);
		this.slider.setInScreenCoords(true);
		this.slider.setColourOffset(new Colour());

		this.updating = false;
		this.minProgress = minProgress;
		this.maxProgress = maxProgress;
		setProgress(progress);

		this.mouseOver = false;

		this.hasChange = false;
		this.changeTimeout = new Timer(0.2f);
		this.listenerChange = null;
	}

	public void addChangeListener(ScreenListener listenerChange) {
		this.listenerChange = listenerChange;
	}

	public float getProgress() {
		return (progress * (maxProgress - minProgress)) + minProgress;
	}

	public void setProgress(float progress) {
		this.progress = (progress - minProgress) / (maxProgress - minProgress);
	}

	@Override
	public void updateObject() {
		if (!isVisible() || getAlpha() == 0.0f) {
			return;
		}

		// Click updates.
		if (FlounderGuis.getSelector().isSelected(text) && getAlpha() == 1.0f && ((updating && FlounderGuis.getSelector().isLeftClick()) || FlounderGuis.getSelector().wasLeftClick())) {
			if (!updating) {
				FlounderSound.playSystemSound(GuiButtonText.SOUND_MOUSE_LEFT);
				updating = true;
			}

			hasChange = true;

			float width = 2.0f * background.getMeshSize().x * background.getScreenDimensions().x / FlounderDisplay.getAspectRatio();
			float positionX = background.getPosition().x;
			float cursorX = FlounderGuis.getSelector().getCursorX() - positionX;
			progress = 2.0f * cursorX / width;
			progress = (progress + 1.0f) * 0.5f;

			FlounderGuis.getSelector().cancelWasEvent();
		} else {
			updating = false;
		}

		// Updates the listener.
		if (hasChange && changeTimeout.isPassedTime()) {
			if (listenerChange != null) {
				listenerChange.eventOccurred();
			}

			hasChange = false;
			changeTimeout.resetStartTime();
		}

		// Mouse over updates.
		if (FlounderGuis.getSelector().isSelected(text) && !mouseOver) {
			FlounderSound.playSystemSound(GuiButtonText.SOUND_MOUSE_HOVER);
			text.setScaleDriver(new SlideDriver(text.getScale(), GuiButtonText.SCALE_SELECTED, GuiButtonText.CHANGE_TIME));
			mouseOver = true;
		} else if (!FlounderGuis.getSelector().isSelected(text) && mouseOver) {
			text.setScaleDriver(new SlideDriver(text.getScale(), GuiButtonText.SCALE_NORMAL, GuiButtonText.CHANGE_TIME));
			mouseOver = false;
		}

		// Update the background colour.
		Colour primary = FlounderGuis.getGuiMaster().getPrimaryColour();
		Colour.interpolate(GuiButtonText.COLOUR_NORMAL, primary, (text.getScale() - GuiButtonText.SCALE_NORMAL) / (GuiButtonText.SCALE_SELECTED - GuiButtonText.SCALE_NORMAL), background.getColourOffset());
		this.slider.getColourOffset().set(1.0f - primary.r, 1.0f - primary.g, 1.0f - primary.b);

		// Update background size.
		background.getDimensions().set(text.getMeshSize());
		background.getDimensions().y = 0.5f * (float) text.getFont().getMaxSizeY();
		Vector2f.multiply(text.getDimensions(), background.getDimensions(), background.getDimensions());
		background.getDimensions().scale(2.0f * text.getScale());
		background.getPositionOffsets().set(text.getPositionOffsets());
		background.getPosition().set(text.getPosition());

		// Update slider size. (This is about the worst looking GUI code, but works well.)
		slider.getDimensions().set(text.getMeshSize());
		slider.getDimensions().y = 0.5f * (float) text.getFont().getMaxSizeY();
		Vector2f.multiply(text.getDimensions(), slider.getDimensions(), slider.getDimensions());
		slider.getDimensions().scale(2.0f * text.getScale());
		slider.getPositionOffsets().set(text.getPositionOffsets());
		slider.getPosition().set(text.getPosition());
		slider.getPositionOffsets().x -= (slider.getDimensions().x / 2.0f);
		slider.getDimensions().x *= progress;
		slider.getPositionOffsets().x += (slider.getDimensions().x / 2.0f);
	}

	public void setText(String string) {
		this.text.setText(string);
	}

	@Override
	public void deleteObject() {
	}
}
