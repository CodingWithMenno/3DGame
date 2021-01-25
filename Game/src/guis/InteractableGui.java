package guis;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

public abstract class InteractableGui extends GuiTexture {

    private int currentTexture;
    private int defaultTexture;
    private int clickedTexture;
    private int hoverTexture;

    private boolean isHovering;

    private Vector2f minXY;
    private Vector2f maxXY;

    public InteractableGui(int defaultTexture, int hoverTexture, int clickedTexture, Vector2f position, Vector2f scale) {
        super(defaultTexture, position, scale);

        this.currentTexture = defaultTexture;
        this.defaultTexture = defaultTexture;
        this.hoverTexture = hoverTexture;
        this.clickedTexture = clickedTexture;

        this.isHovering = false;
        this.minXY = new Vector2f(this.position.x - this.scale.x, this.position.y - this.scale.y);
        this.maxXY = new Vector2f(this.position.x + this.scale.x, this.position.y + this.scale.y);
    }

    protected boolean isHoveringAbove() {
        Vector2f mousePos = Input.getRelativeMousePos();

        if (mousePos.x > this.minXY.x && mousePos.x < this.maxXY.x &&
            mousePos.y > this.minXY.y && mousePos.y < this.maxXY.y) {

            this.currentTexture = this.hoverTexture;

            if (!this.isHovering) {
                onEnter();
                this.isHovering = true;
            }

            return true;
        }

        this.currentTexture = this.defaultTexture;

        if (this.isHovering) {
            onExit();
            this.isHovering = false;
        }

        return false;
    }

    public void update() {
        if (isHoveringAbove() && Mouse.isButtonDown(0)) {
            this.currentTexture = this.clickedTexture;
            onClick();
        }
    }

    protected abstract void onClick();
    protected abstract void onEnter();
    protected abstract void onExit();

    public int getTexture() {
        return this.currentTexture;
    }
}
