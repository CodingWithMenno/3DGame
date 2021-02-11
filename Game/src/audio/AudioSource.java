package audio;

import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import toolbox.Maths;

public class AudioSource {

    private int sourceId;
    private float volume;

    public AudioSource(float volume) {
        this.sourceId = AL10.alGenSources();
        this.volume = volume;

        AL10.alSourcef(this.sourceId, AL10.AL_GAIN, this.volume);
        AL10.alSourcef(this.sourceId, AL10.AL_PITCH, 1);
        AL10.alSource3f(this.sourceId, AL10.AL_POSITION, 0, 0, 0);

        AL10.alSourcef(this.sourceId, AL10.AL_ROLLOFF_FACTOR, 4);
        AL10.alSourcef(this.sourceId, AL10.AL_REFERENCE_DISTANCE, 6);
        AL10.alSourcef(this.sourceId, AL10.AL_MAX_DISTANCE, 50);
    }

    public void play(int buffer) {
        stop();
        AL10.alSourcei(this.sourceId, AL10.AL_BUFFER, buffer);
        AL10.alSourcePlay(this.sourceId);
    }

    public void pause() {
        AL10.alSourcePause(this.sourceId);
    }

    public void resume() {
        AL10.alSourcePlay(this.sourceId);
    }

    public void stop() {
        AL10.alSourceStop(this.sourceId);
    }

    public void setVolume(float volume) {
        AL10.alSourcef(this.sourceId, AL10.AL_GAIN, volume);
        this.volume = volume;
    }

    public void fadeTo(float finalVolume, float fadeFactor) {
        setVolume(Maths.lerp(this.volume, finalVolume, fadeFactor * DisplayManager.getDelta()));
    }

    public float getVolume() {
        return this.volume;
    }

    public void setPitch(float pitch) {
        AL10.alSourcef(this.sourceId, AL10.AL_PITCH, pitch);
    }

    public void setPosition(Vector3f position) {
        AL10.alSource3f(this.sourceId, AL10.AL_POSITION, position.x, position.y, position.z);
    }

    public void setVelocity(Vector3f velocity) {
        AL10.alSource3f(this.sourceId, AL10.AL_VELOCITY, velocity.x, velocity.y, velocity.z);
    }

    public void setLooping(boolean loop) {
        AL10.alSourcei(this.sourceId, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
    }

    public boolean isPlaying() {
        return AL10.alGetSourcef(this.sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    public void delete() {
        stop();
        AL10.alDeleteSources(this.sourceId);
    }
}
