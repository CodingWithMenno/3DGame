package gameLoop;

public interface Scene {
    void setup();
    void resume();
    void pause();
    void update();
    void render();
    void cleanUp();
}
