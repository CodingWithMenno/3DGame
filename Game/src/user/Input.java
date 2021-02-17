package user;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import renderEngine.DisplayManager;

public class Input {

    //Player controls
    public static int FORWARD = Keyboard.KEY_W;
    public static int BACKWARDS = Keyboard.KEY_S;
    public static int LEFT = Keyboard.KEY_A;
    public static int RIGHT = Keyboard.KEY_D;
    public static int JUMP = Keyboard.KEY_SPACE;

    //Camera controls
    public static float SENSITIVITY = 0.1f;
    public static int FREE_CAMERA_ANGLE = Keyboard.KEY_LCONTROL;

    public static Vector2f getRelativeMousePos() {
        float x = (Mouse.getX() / DisplayManager.getScaledWidth() / DisplayManager.getDefaultWidth()) * 2f - 1f;
        float y = (Mouse.getY() / DisplayManager.getScaledHeight() / DisplayManager.getDefaultHeight()) * 2f - 1f;

        return new Vector2f(x, y);
    }

}
