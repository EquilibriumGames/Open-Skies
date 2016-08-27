package game.uis.screens;

import flounder.engine.*;
import flounder.events.*;
import flounder.fonts.*;
import flounder.guis.*;
import game.*;
import game.uis.*;

import java.util.*;

public class ScreenPlay extends GuiComponent {
	private MainSlider mainSlider;

	public ScreenPlay(MainSlider mainSlider) {
		this.mainSlider = mainSlider;

		createTitleText("Play");

		float currentY = -0.15f;
		createSingleplayerOption(MainSlider.BUTTONS_X_POS, currentY += MainSlider.BUTTONS_Y_SEPARATION);
		createMultiplayerOption(MainSlider.BUTTONS_X_POS, currentY += MainSlider.BUTTONS_Y_SEPARATION);

		createBackOption(MainSlider.BUTTONS_X_POS, 1.0f);

		super.show(false);

		FlounderEngine.getEvents().addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return ScreenPlay.super.isShown() && MainSlider.BACK_KEY.wasDown();
			}

			@Override
			public void onEvent() {
				mainSlider.closeSecondaryScreen();
			}
		});
	}

	private void createTitleText(String title) {
		Text titleText = MainSlider.createTitleText(title, this);
	}

	private void createSingleplayerOption(float xPos, float yPos) {
		GuiTextButton button = MainSlider.createButton("Singleplayer", xPos, yPos, MainSlider.BUTTONS_X_WIDTH, MainSlider.BUTTONS_Y_SIZE, MainSlider.FONT_SIZE, this);
		button.addLeftListener(() -> {
			((MainGame) FlounderEngine.getGame()).generateWorlds();
			((MainGame) FlounderEngine.getGame()).generatePlayer();
			mainSlider.getSuperMenu().display(false);
			mainSlider.sliderStartMenu(false);
			mainSlider.closeSecondaryScreen();
		});
	}

	private void createMultiplayerOption(float xPos, float yPos) {
		GuiTextButton button = MainSlider.createButton("Multiplayer", xPos, yPos, MainSlider.BUTTONS_X_WIDTH, MainSlider.BUTTONS_Y_SIZE, MainSlider.FONT_SIZE, this);
		//button.addLeftListener(() -> mainSlider.getMenuStart().getSuperMenu().display(false));
	}

	private void createBackOption(float xPos, float yPos) {
		GuiTextButton button = MainSlider.createButton("Back", xPos, yPos, MainSlider.BUTTONS_X_WIDTH, MainSlider.BUTTONS_Y_SIZE, MainSlider.FONT_SIZE, this);
		button.addLeftListener(mainSlider::closeSecondaryScreen);
	}

	@Override
	protected void updateSelf() {
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
	}
}
