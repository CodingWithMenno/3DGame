package audio;

import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class AudioTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        AudioMaster.init();
        AudioMaster.setListenerData(new Vector3f(0, 0, -1));
        AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);

        int buffer = AudioMaster.loadSound("audio/sounds/Water.wav");
        AudioSource audioSource = new AudioSource(1);
        audioSource.setLooping(true);
        audioSource.play(buffer);

        float xPos = 5;
        audioSource.setPosition(new Vector3f(xPos, 0, 0));

        char c = ' ';
        while (c != 'q') {
            xPos -= 0.02f;
            audioSource.setPosition(new Vector3f(xPos, 0, 0));
            System.out.println(xPos);
            Thread.sleep(10);
        }

        audioSource.delete();
        AudioMaster.cleanUp();
    }
}
