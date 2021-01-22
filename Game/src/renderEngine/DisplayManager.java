package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;

public class DisplayManager {

	private static final String TITLE = "Poly Hunting";
	private static final int WIDTH = 1920;
	private static final int HEIGHT = 1080;
	private static final int FPS_CAP = 250;

	private static final int ANTIALIASING_AMOUNT = 8;

	private static long lastFrameTime;
	private static float delta;
	
	public static void createDisplay() {
		ContextAttribs attribs = new ContextAttribs(3,2)
		.withForwardCompatible(true)
		.withProfileCore(true);

		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat().withDepthBits(24).withSamples(ANTIALIASING_AMOUNT), attribs);
			Display.setTitle(TITLE);
			Display.setVSyncEnabled(true);
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0,0, WIDTH, HEIGHT);
		lastFrameTime = getCurrentTime();

		try {
			Mouse.create();
			Mouse.setGrabbed(true);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateDisplay() {
		Display.sync(FPS_CAP);
		Display.update();

		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
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
}
