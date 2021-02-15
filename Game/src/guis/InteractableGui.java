package guis;

import user.Input;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

public abstract class InteractableGui extends GuiElement {

    private int defaultTexture;
    private int clickedTexture;
    private int hoverTexture;

    private boolean isHovering;
    private boolean isClicking;

    private Vector2f minXY;
    private Vector2f maxXY;

    public InteractableGui(int defaultTexture, Vector2f position, Vector2f scale) {
        super(defaultTexture, position, scale);

        this.defaultTexture = defaultTexture;
        this.hoverTexture = 0;
        this.clickedTexture = 0;

        this.isHovering = false;
        this.isClicking = false;

        this.minXY = new Vector2f(this.position.x - this.scale.x, this.position.y - this.scale.y);
        this.maxXY = new Vector2f(this.position.x + this.scale.x, this.position.y + this.scale.y);
    }

    private boolean isHoveringAbove() {
        Vector2f mousePos = Input.getRelativeMousePos();

        if (mousePos.x >= this.minXY.x && mousePos.x <= this.maxXY.x &&
            mousePos.y >= this.minXY.y && mousePos.y <= this.maxXY.y) {

            if (this.hoverTexture != 0) {
                this.texture = this.hoverTexture;
            } else {
                this.texture = this.defaultTexture;
            }

            if (!this.isHovering) {
                onEnter();
                this.isHovering = true;
            }

            return true;
        }

        this.texture = this.defaultTexture;

        if (this.isHovering) {
            onExit();
            this.isHovering = false;
        }

        return false;
    }

    public void update() {
       if (isHoveringAbove() && Mouse.isButtonDown(0)) {
           if (this.clickedTexture != 0) {
               this.texture = this.clickedTexture;
           } else {
               this.texture = this.defaultTexture;
           }

           if (!this.isClicking) {
               onClick();
               this.isClicking = true;
           }
       } else {
           if (this.isClicking) {
               this.isClicking = false;
           }
       }
    }

    protected abstract void onClick();
    protected abstract void onEnter();
    protected abstract void onExit();

    public void setClickedTexture(int clickedTexture) {
        this.clickedTexture = clickedTexture;
    }

    public void setHoverTexture(int hoverTexture) {
        this.hoverTexture = hoverTexture;
    }

    public void updateHitbox() {
        this.minXY = new Vector2f(this.position.x - this.scale.x, this.position.y - this.scale.y);
        this.maxXY = new Vector2f(this.position.x + this.scale.x, this.position.y + this.scale.y);
    }
}
