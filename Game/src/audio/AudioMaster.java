package audio;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.openal.WaveData;

import java.util.ArrayList;
import java.util.List;

public class AudioMaster {

    private static Vector3f listenerPosition;

    private static List<Integer> buffers = new ArrayList<>();

    public static void init() {
        try {
            AL.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        listenerPosition = new Vector3f(0, 0, 0);
    }

    public static void setListenerData(Vector3f position) {
        AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);
        AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);

        listenerPosition = position;
    }

    public static int loadSound(String file) {
        int buffer = AL10.alGenBuffers();
        buffers.add(buffer);

        WaveData waveFile = WaveData.create(file);
        AL10.alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
        waveFile.dispose();

        return buffer;
    }

    public static void cleanUp() {
        for (int buffer : buffers) {
            AL10.alDeleteBuffers(buffer);
        }

        AL.destroy();
    }

    public static Vector3f getListenerPosition() {
        return listenerPosition;
    }
}
