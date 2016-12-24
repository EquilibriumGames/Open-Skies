package ebon;

import com.codedisaster.steamworks.*;
import ebon.options.*;
import ebon.world.*;
import flounder.devices.*;
import flounder.events.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.inputs.*;
import flounder.logger.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.sounds.*;
import flounder.standard.*;

import static org.lwjgl.glfw.GLFW.*;

public class EbonInterface extends IExtension implements IStandard {
	private KeyButton screenshot;
	private KeyButton fullscreen;
	private KeyButton polygons;
	private CompoundButton toggleMusic;
	private CompoundButton skipMusic;
	private CompoundButton switchCamera;

	private Playlist pausedMusic;

	public EbonInterface() {
		super(FlounderLogger.class, FlounderStandard.class, FlounderEvents.class, FlounderDisplay.class, FlounderGuis.class, FlounderSound.class, EbonWorld.class);
	}

	@Override
	public void init() {
		this.screenshot = new KeyButton(GLFW_KEY_F2);
		this.fullscreen = new KeyButton(GLFW_KEY_F11);
		this.polygons = new KeyButton(GLFW_KEY_P);
		this.toggleMusic = new CompoundButton(new KeyButton(GLFW_KEY_DOWN), new JoystickButton(OptionsControls.JOYSTICK_PORT, OptionsControls.JOYSTICK_MUSIC_PAUSE));
		this.skipMusic = new CompoundButton(new KeyButton(GLFW_KEY_LEFT, GLFW_KEY_RIGHT), new JoystickButton(OptionsControls.JOYSTICK_PORT, OptionsControls.JOYSTICK_MUSIC_SKIP));
		this.switchCamera = new CompoundButton(new KeyButton(GLFW_KEY_C), new JoystickButton(OptionsControls.JOYSTICK_PORT, OptionsControls.JOYSTICK_CAMERA_SWITCH));

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return screenshot.wasDown();
			}

			@Override
			public void onEvent() {
				FlounderDisplay.screenshot();
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return fullscreen.wasDown();
			}

			@Override
			public void onEvent() {
				FlounderDisplay.setFullscreen(!FlounderDisplay.isFullscreen());
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return polygons.wasDown();
			}

			@Override
			public void onEvent() {
				OpenGlUtils.goWireframe(!OpenGlUtils.isInWireframe());
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return toggleMusic.wasDown();
			}

			@Override
			public void onEvent() {
				if (FlounderSound.getMusicPlayer().isPaused()) {
					FlounderSound.getMusicPlayer().unpauseTrack();
				} else {
					FlounderSound.getMusicPlayer().pauseTrack();
				}
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return skipMusic.wasDown();
			}

			@Override
			public void onEvent() {
				EbonSeed.randomize();
				FlounderSound.getMusicPlayer().skipTrack();
			}
		});

		FlounderEvents.addEvent(new IEvent() {
			@Override
			public boolean eventTriggered() {
				return switchCamera.wasDown();
			}

			@Override
			public void onEvent() {

			}
		});

		FlounderBounding.toggle(Ebon.configMain.getBooleanWithDefault("boundings_render", false, FlounderBounding::renders));
		FlounderProfiler.toggle(Ebon.configMain.getBooleanWithDefault("profiler_open", false, FlounderProfiler::isOpen));

		pausedMusic = new Playlist();
		pausedMusic.addMusic(Sound.loadSoundInBackground(new MyFile(MyFile.RES_FOLDER, "music", "era-of-space.wav"), 0.80f));
		pausedMusic.addMusic(Sound.loadSoundInBackground(new MyFile(MyFile.RES_FOLDER, "music", "pyrosanical.wav"), 0.50f));
		pausedMusic.addMusic(Sound.loadSoundInBackground(new MyFile(MyFile.RES_FOLDER, "music", "spacey-ambient.wav"), 0.60f));
		FlounderSound.getMusicPlayer().playMusicPlaylist(pausedMusic, true, 4.0f, 10.0f);

		//	FlounderLogger.log("Starting main menu music.");
		//	FlounderSound.getMusicPlayer().unpauseTrack();

		try {
			if (!SteamAPI.init()) {
				// Steamworks initialization error, e.g. Steam client not running
			}
		} catch (SteamException e) {
			FlounderLogger.exception(e);
		}

		SteamAPI.printDebugInfo(System.out);
	}

	@Override
	public void update() {
		if (SteamAPI.isSteamRunning()) {
			SteamAPI.runCallbacks();
		}
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public void dispose() {
		SteamAPI.shutdown();
	}
}