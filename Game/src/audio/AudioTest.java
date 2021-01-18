package audio;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class AudioTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        AudioMaster.init();
        AudioMaster.setListenerData(new Vector3f(0, 0, -1));
        AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);

        int buffer = AudioMaster.loadSound("audio/Bounce.wav");
        Source source = new Source();
        source.setLooping(true);
        source.play(buffer);

        float xPos = 5;
        source.setPosition(new Vector3f(xPos, 0, 0));

        char c = ' ';
        while (c != 'q') {
            xPos -= 0.02f;
            source.setPosition(new Vector3f(xPos, 0, 0));
            System.out.println(xPos);
            Thread.sleep(10);
        }

        source.delete();
        AudioMaster.cleanUp();
    }
}
