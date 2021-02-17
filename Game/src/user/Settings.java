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

    //Shadow graphics settings
    public static final int SHADOW_MAP_SIZE = 16384;
    public static final float SHADOW_DISTANCE = 150;

    //Sound
    public static final float BIOME_SOUND = 0.5f;
}
