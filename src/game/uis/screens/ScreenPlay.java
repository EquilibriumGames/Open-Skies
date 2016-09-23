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

		createTitleText(GuiAlign.LEFT, "Play");

		float currentY = -0.15f;
		createSingleplayerOption(GuiAlign.LEFT, currentY += MainSlider.BUTTONS_Y_SEPARATION);
		createMultiplayerOption(GuiAlign.LEFT, currentY += MainSlider.BUTTONS_Y_SEPARATION);

		createBackOption(GuiAlign.LEFT, 1.0f);

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

	private void createTitleText(GuiAlign guiAlign, String title) {
		Text titleText = MainSlider.createTitleText(title, guiAlign, this);
	}

	private void createSingleplayerOption(GuiAlign guiAlign, float yPos) {
		GuiTextButton button = MainSlider.createButton("Singleplayer", guiAlign, yPos, this);
		button.addLeftListener(() -> {
			((MainGame) FlounderEngine.getGame()).generateWorlds();
			((MainGame) FlounderEngine.getGame()).generatePlayer();
			mainSlider.getSuperMenu().display(false);
			mainSlider.sliderStartMenu(false);
			mainSlider.closeSecondaryScreen();
		});
	}

	private void createMultiplayerOption(GuiAlign guiAlign, float yPos) {
		GuiTextButton button = MainSlider.createButton("Multiplayer", guiAlign, yPos, this);
		//button.addLeftListener(() -> mainSlider.getMenuStart().getSuperMenu().display(false));
	}

	private void createBackOption(GuiAlign guiAlign, float yPos) {
		GuiTextButton button = MainSlider.createButton("Back", guiAlign, yPos, this);
		button.addLeftListener(mainSlider::closeSecondaryScreen);
	}

	@Override
	protected void updateSelf() {
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
	}
}
