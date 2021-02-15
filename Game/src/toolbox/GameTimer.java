package toolbox;

import renderEngine.DisplayManager;

public class GameTimer {

    private float currentTime;
    private float finalTime;
    private boolean hasFinished;

    public GameTimer(float finalTime) {
        this.currentTime = 0.0f;
        this.finalTime = finalTime;
        this.hasFinished = false;
    }

    public void updateTimer() {
        this.currentTime += DisplayManager.getDelta();

        if (this.currentTime >= this.finalTime) {
            this.hasFinished = true;
        }
    }

    public boolean hasFinished() {
        return hasFinished;
    }
}
