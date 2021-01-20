package animation;

import models.TexturedModel;
import renderEngine.DisplayManager;

public class Animator {

    private Animation animation;

    private TexturedModel currentModel;
    private float currentTime;

    private float msPerFrame;


    public Animator(Animation animation) {
        this.animation = animation;

        this.currentTime = 0.0f;
        this.currentModel = this.animation.getKeyframes().get(0);

        this.msPerFrame = this.animation.getAnimationLength() / this.animation.getKeyframes().size();
    }

    public TexturedModel getCurrentFrame() {
        return this.currentModel;
    }

    public void animate() {
        updateTime();
        setCurrentModel();
    }

    private void updateTime() {
        this.currentTime += DisplayManager.getDelta();

        if (this.currentTime > this.animation.getAnimationLength()) {
            this.currentTime = 0.0f;
        }
    }

    private void setCurrentModel() {
        int frame = (int) (this.currentTime / this.msPerFrame);
        this.currentModel = this.animation.getKeyframes().get(frame);
    }

    public void setAnimationTime(float animationTime) {
        this.animation.setAnimationLength(animationTime);
        this.msPerFrame = this.animation.getAnimationLength() / this.animation.getKeyframes().size();
    }
}
