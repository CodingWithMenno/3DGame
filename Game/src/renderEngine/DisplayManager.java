package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import user.Settings;

public class DisplayManager {

	private static final String TITLE = "Poly Hunter";

	private static final float DEFAULT_WIDTH = 2560f;
	private static final float DEFAULT_HEIGHT = 1440f;
	private static final float SCALED_WIDTH = Settings.WIDTH / DEFAULT_WIDTH;
	private static final float SCALED_HEIGHT = Settings.HEIGHT / DEFAULT_HEIGHT;

	private static long lastFrameTime;
	private static float delta;

	private static long lastFPS;
	private static long FPS;
	
	public static void createDisplay() {
		ContextAttribs attribs = new ContextAttribs(3,2)
		.withForwardCompatible(true)
		.withProfileCore(true);

		try {
			Display.setDisplayMode(new DisplayMode(Settings.WIDTH, Settings.HEIGHT));
			Display.create(new PixelFormat().withDepthBits(24).withSamples(Settings.ANTIALIASING_AMOUNT), attribs);
			Display.setTitle(TITLE);
			Display.setResizable(true);
			Display.setVSyncEnabled(Settings.VSYNC);
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0,0, Settings.WIDTH, Settings.HEIGHT);
		lastFrameTime = getCurrentTime();

		try {
			Mouse.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		lastFPS = getCurrentTime();
		FPS = 0;
	}
	
	public static void updateDisplay() {
		//Updating the display
		Display.sync(Settings.FPS_CAP);
		Display.update();

		//Updating the delta time
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;

		//Calculating the fps
		if (getCurrentTime() - lastFPS > 1000) {
			Display.setTitle(TITLE + ", FPS: " + FPS);
			FPS = 0;
			lastFPS += 1000;
		}
		FPS++;
	}
	
	public static void closeDisplay() {
		Mouse.destroy();
		Display.destroy();
	}

	private static long getCurrentTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	public static float getDelta() {
		return delta;
	}

	public static float getWIDTH() {
		return Settings.WIDTH;
	}

	public static float getHEIGHT() {
		return Settings.HEIGHT;
	}

	public static float getScaledWidth() {
		return SCALED_WIDTH;
	}

	public static float getScaledHeight() {
		return SCALED_HEIGHT;
	}

	public static float getDefaultWidth() {
		return DEFAULT_WIDTH;
	}

	public static float getDefaultHeight() {
		return DEFAULT_HEIGHT;
	}
}
