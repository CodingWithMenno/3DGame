package terrains;

import toolbox.Maths;

import java.util.Random;

public class HeightGenerator {

    private static final float AMPLITUDE = 40f;
    private static final int OCTAVES = 3;
    private static final float ROUGHNESS = 0.03f;
    private static final float SMOOTH_FACTOR = 6f;

    private Random random;
    private int seed;

    public HeightGenerator() {
        this.random = new Random();
        this.seed = this.random.nextInt(1000000000);
    }

    public float generateHeight(int x, int z) {
        float total = getInterpolatedNoise(x / SMOOTH_FACTOR, z / SMOOTH_FACTOR) * AMPLITUDE;

        float divide = 2f;
        float ampDivide = 3f;
        for (int i = 1; i < OCTAVES; i++) {
            total += getInterpolatedNoise(x / (SMOOTH_FACTOR / divide), z / (SMOOTH_FACTOR / divide)) * AMPLITUDE / ampDivide;
            divide *= ROUGHNESS;
            ampDivide *= ROUGHNESS;
        }

        if (total < -AMPLITUDE) {
            total = -AMPLITUDE;
            total += getInterpolatedNoise(x / (SMOOTH_FACTOR / 4f), z / (SMOOTH_FACTOR / 4f)) * AMPLITUDE / 3f;
        }

        return total;
    }

    private float getInterpolatedNoise(float x, float z) {
        int intX = (int) x;
        int intZ = (int) z;
        float fracX = x - intX;
        float fracZ = z - intZ;

        float v1 = getSmoothNoise(intX, intZ);
        float v2 = getSmoothNoise(intX + 1, intZ);
        float v3 = getSmoothNoise(intX, intZ + 1);
        float v4 = getSmoothNoise(intX + 1, intZ + 1);
        float i1 = interpolate(v1, v2, fracX);
        float i2 = interpolate(v3, v4, fracX);
        return interpolate(i1, i2, fracZ);
    }

    private float interpolate(float a, float b, float blend) {
        double theta = blend * Math.PI;
        float f = (float) (1f - Math.cos(theta)) * 0.5f;
        return a * (1f - f) + b * f;
    }

    private float getSmoothNoise(int x, int z) {
        float corners = (getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1)
                + getNoise(x + 1, z + 1)) / 16f;

        float sides = (getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1) + getNoise(x, z + 1)) / 8f;

        float center = getNoise(x, z) / 4f;

        return corners + sides + center;
    }

    private float getNoise(int x, int z) {
        this.random.setSeed(x * 42069 + z * 690420 + this.seed);
        return this.random.nextFloat() * 2f - 1f;
    }
}
