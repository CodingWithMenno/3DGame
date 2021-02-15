package guis;

import org.lwjgl.util.vector.Vector2f;

public class Button extends InteractableGui {

    private GuiAction onClickAction;
    private GuiAction onEnterAction;
    private GuiAction onExitAction;

    public Button(int defaultTexture, Vector2f position, Vector2f scale) {
        super(defaultTexture, position, scale);
    }

    @Override
    protected void onClick() {
        if (this.onClickAction != null) {
            this.onClickAction.doAction();
        }
    }

    @Override
    protected void onEnter() {
        if (this.onEnterAction != null) {
            this.onEnterAction.doAction();
        }
    }

    @Override
    protected void onExit() {
        if (this.onExitAction != null) {
            this.onExitAction.doAction();
        }
    }

    public void setOnClickAction(GuiAction onClickAction) {
        this.onClickAction = onClickAction;
    }

    public void setOnEnterAction(GuiAction onEnterAction) {
        this.onEnterAction = onEnterAction;
    }

    public void setOnExitAction(GuiAction onExitAction) {
        this.onExitAction = onExitAction;
    }
}
