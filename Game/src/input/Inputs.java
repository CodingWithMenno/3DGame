package input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import renderEngine.DisplayManager;

public class Inputs {

    //Player controls
    public static final int FORWARD = Keyboard.KEY_W;
    public static final int BACKWARDS = Keyboard.KEY_S;
    public static final int LEFT = Keyboard.KEY_A;
    public static final int RIGHT = Keyboard.KEY_D;
    public static final int JUMP = Keyboard.KEY_SPACE;


    public static Vector2f getRelativeMousePos() {
        float x = (Mouse.getX() / DisplayManager.getScaledWidth() / DisplayManager.getDefaultWidth()) * 2f - 1f;
        float y = (Mouse.getY() / DisplayManager.getScaledHeight() / DisplayManager.getDefaultHeight()) * 2f - 1f;

        return new Vector2f(x, y);
    }
}
