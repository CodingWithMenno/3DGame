package animation;

import models.TexturedModel;

import java.util.*;

public class AnimatedModel {

    private List<Animator> animators;

    private int currentAnimation;

    public AnimatedModel(Animation... animations) {
        this.currentAnimation = 0;

        this.animators = new ArrayList<>();
        for (Animation animation : animations) {
            this.animators.add(new Animator(animation));
        }
    }

    public void updateCurrentAnimation() {
        this.animators.get(this.currentAnimation).animate();
    }

    public void setCurrentAnimation(int currentAnimation) {
        this.currentAnimation = currentAnimation;
    }

    public TexturedModel getCurrentModel() {
        return this.animators.get(this.currentAnimation).getCurrentFrame();
    }

    public void setAnimationTimeOf(int animation, float animationTime) {
        this.animators.get(animation).setAnimationTime(animationTime);
    }
}
