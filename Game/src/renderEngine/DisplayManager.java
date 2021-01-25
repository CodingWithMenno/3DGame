package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;

public class DisplayManager {

	private static final String TITLE = "Poly Hunter";
	private static final int WIDTH = 1920;
	private static final int HEIGHT = 1080;
	private static final int FPS_CAP = 500;
	private static final boolean VSYNC = false;
	private static final int ANTIALIASING_AMOUNT = 8;

	private static final float DEFAULT_WIDTH = 2560f;
	private static final float DEFAULT_HEIGHT = 1440f;
	private static final float SCALED_WIDTH = WIDTH / DEFAULT_WIDTH;
	private static final float SCALED_HEIGHT = HEIGHT / DEFAULT_HEIGHT;

	private static long lastFrameTime;
	private static float delta;

	private static long lastFPS;
	private static long FPS;
	
	public static void createDisplay() {
		ContextAttribs attribs = new ContextAttribs(3,2)
		.withForwardCompatible(true)
		.withProfileCore(true);

		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat().withDepthBits(24).withSamples(ANTIALIASING_AMOUNT), attribs);
			Display.setTitle(TITLE);
			Display.setVSyncEnabled(VSYNC);
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0,0, WIDTH, HEIGHT);
		lastFrameTime = getCurrentTime();

		try {
			Mouse.create();
			//Mouse.setGrabbed(true);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		lastFPS = getCurrentTime();
		FPS = 0;
	}
	
	public static void updateDisplay() {
		Display.sync(FPS_CAP);
		Display.update();

		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;

		if (getCurrentTime() - lastFPS > 1000) {
			Display.setTitle(TITLE + ", FPS: " + FPS);
			FPS = 0;
			lastFPS += 1000;
		}
		FPS++;
	}

	public static float getDelta() {
		return delta;
	}
	
	public static void closeDisplay() {
		Mouse.destroy();
		Display.destroy();
	}

	private static long getCurrentTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

	public static float getWIDTH() {
		return WIDTH;
	}

	public static float getHEIGHT() {
		return HEIGHT;
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
