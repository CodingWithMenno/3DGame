package user;

import org.lwjgl.input.Keyboard;

public class Settings {

    //Overall graphic settings
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    public static final int FPS_CAP = 2000;
    public static final boolean VSYNC = true;
    public static final int ANTIALIASING_AMOUNT = 8;

    //Water graphics settings
    public static final boolean USE_WATER_REFLECTION = true;
    public static final int REFLECTION_WIDTH = 1920;
    public static final int REFLECTION_HEIGHT = 1080;
    public static final int REFRACTION_WIDTH = 1920;
    public static final int REFRACTION_HEIGHT = 1080;

    //Sound
    public static final float BIOME_SOUND = 0.5f;

    //Player controls
    public static int FORWARD = Keyboard.KEY_W;
    public static int BACKWARDS = Keyboard.KEY_S;
    public static int LEFT = Keyboard.KEY_A;
    public static int RIGHT = Keyboard.KEY_D;
    public static int JUMP = Keyboard.KEY_SPACE;

    //Camera controls
    public static float SENSITIVITY = 0.1f;
    public static int FREE_CAMERA_ANGLE = Keyboard.KEY_LCONTROL;
}
