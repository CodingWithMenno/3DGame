package animation;

import models.TexturedModel;

public class Animator {

    private Animation animation;

    private float currentTime;

    public Animator(Animation animation) {
        this.animation = animation;
        this.currentTime = 0;
    }

    public TexturedModel getCurrentFrame() {
        //TODO return the correct keyframe depending on the time
        return this.animation.getKeyframes().get(0);
    }

    public void animate() {
        updateTime();
    }

    private void updateTime() {
        //TODO
    }
}
