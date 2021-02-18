package objects;

import objects.GameObject;
import org.lwjgl.util.vector.Vector3f;

public class Light extends GameObject {

    private Vector3f colour;
    private Vector3f attenuation;

    public Light(Vector3f position, Vector3f colour) {
        super(position, 0, 0, 0);
        this.colour = colour;
        this.attenuation = new Vector3f(1, 0, 0);
    }

    public Light(Vector3f position, Vector3f colour, Vector3f attenuation) {
        super(position, 0, 0, 0);
        this.colour = colour;
        this.attenuation = attenuation;
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }
}
