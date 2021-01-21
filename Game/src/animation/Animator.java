package animation;

import models.TexturedModel;
import renderEngine.DisplayManager;

public class Animator {

    private Animation animation;

    private TexturedModel currentModel;
    private float currentTime;
    private float msPerFrame;

    private boolean isPaused;


    public Animator(Animation animation) {
        this.animation = animation;

        this.currentTime = 0.0f;
        this.currentModel = this.animation.getKeyframes().get(0);
        this.msPerFrame = this.animation.getAnimationLength() / this.animation.getKeyframes().size();

        this.isPaused = false;
    }

    public void animate() {
        if (!this.isPaused) {
            updateTime();
            setCurrentModel();
        }
    }

    private void updateTime() {
        this.currentTime += DisplayManager.getDelta();

        if (this.currentTime > this.animation.getAnimationLength()) {
            this.currentTime = 0.0f;
        }
    }

    private void setCurrentModel() {
        try {
            int frame = (int) (this.currentTime / this.msPerFrame);
            this.currentModel = this.animation.getKeyframes().get(frame);
        } catch (IndexOutOfBoundsException e) {
            this.currentModel = this.animation.getKeyframes() .get(0);
        }
    }

    public void setAnimationTime(float animationTime) {
        this.animation.setAnimationLength(animationTime);
        this.msPerFrame = this.animation.getAnimationLength() / this.animation.getKeyframes().size();
    }

    public void pauseAnimation() {
        this.isPaused = true;
    }

    public void resumeAnimation() {
        this.isPaused = false;
    }

    public TexturedModel getCurrentFrame() {
        return this.currentModel;
    }
}
