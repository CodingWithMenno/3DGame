package terrains;

import models.RawModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Terrain {

    private static final float SIZE = 1500;

    private static final int VERTEX_COUNT = 128;

    private float x;
    private float z;
    private RawModel model;
    private TerrainTexturePack texturePack;

    private float[][] heights;

    public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack) {
        this.texturePack = texturePack;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = generateTerrain(loader);
    }

    public float getHeightOfTerrain(float wordlX, float worldZ) {
        float terrainX = wordlX - this.x;
        float terrainZ = worldZ - this.z;
        float gridSquareSize = SIZE / ((float) this.heights.length - 1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
        if (gridX >= this.heights.length - 1 || gridZ >= this.heights.length - 1 || gridX < 0 || gridZ < 0) {
            return 0;
        }
        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
        float answer = 0;
        if (xCoord <= (1-zCoord)) {
            answer = Maths
                    .barryCentric(new Vector3f(0, this.heights[gridX][gridZ], 0), new Vector3f(1,
                            this.heights[gridX + 1][gridZ], 0), new Vector3f(0,
                            this.heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        } else {
            answer = Maths
                    .barryCentric(new Vector3f(1, this.heights[gridX + 1][gridZ], 0), new Vector3f(1,
                            this.heights[gridX + 1][gridZ + 1], 1), new Vector3f(0,
                            this. heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }
        return answer;
    }

    private RawModel generateTerrain(Loader loader) {

        HeightGenerator generator = new HeightGenerator();
        int vertexCount = VERTEX_COUNT;

        this.heights = new float[vertexCount][vertexCount];
        int count = vertexCount * vertexCount;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (vertexCount - 1) * (vertexCount - 1)];
        int vertexPointer = 0;
        for(int i = 0; i < vertexCount; i++) {
            for(int j = 0; j < vertexCount; j++) {
                vertices[vertexPointer * 3] = (float) j / ((float)vertexCount - 1) * SIZE;
                float height = getHeight(j, i, generator);
                this.heights[j][i] = height;
                vertices[vertexPointer * 3 + 1] = height;
                vertices[vertexPointer * 3 + 2] = (float) i / ((float)vertexCount - 1) * SIZE;
                Vector3f normal = calculateNormal(j, i, generator);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;
                textureCoords[vertexPointer * 2] = (float) j / ((float)vertexCount - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) i / ((float)vertexCount - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz = 0; gz < vertexCount - 1; gz++) {
            for(int gx = 0; gx < vertexCount - 1; gx++) {
                int topLeft = (gx * vertexCount) + gz;
                int topRight = topLeft + 1;
                int bottomLeft = ((gx + 1) * vertexCount) + gz;
                int bottomRight = bottomLeft + 1;
                if (gx % 2 == 0) {
                    pointer = storeQuad1(indices, pointer, topLeft, topRight, bottomLeft, bottomRight, gz % 2 == 0);
                } else {
                    pointer = storeQuad2(indices, pointer, topLeft, topRight, bottomLeft, bottomRight, gz %  2 == 0);
                }
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    private int storeQuad1(int[] indices, int pointer, int topLeft, int topRight, int bottomLeft, int bottomRight,
                           boolean mixed) {
        indices[pointer++] = topLeft;
        indices[pointer++] = bottomLeft;
        indices[pointer++] = mixed ? topRight : bottomRight;
        indices[pointer++] = bottomRight;
        indices[pointer++] = topRight;
        indices[pointer++] = mixed ? bottomLeft : topLeft;
        return pointer;
    }

    private int storeQuad2(int[] indices, int pointer, int topLeft, int topRight, int bottomLeft, int bottomRight,
                           boolean mixed) {
        indices[pointer++] = topRight;
        indices[pointer++] = topLeft;
        indices[pointer++] = mixed ? bottomRight : bottomLeft;
        indices[pointer++] = bottomLeft;
        indices[pointer++] = bottomRight;
        indices[pointer++] = mixed ? topLeft : topRight;
        return pointer;
    }

    private Vector3f calculateNormal(int x, int y, HeightGenerator generator) {
        float heightL = getHeight(x - 1, y, generator);
        float heightR = getHeight(x + 1, y, generator);
        float heightD = getHeight(x, y - 1, generator);
        float heightU = getHeight(x, y + 1, generator);
        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }

    private float getHeight(int x, int z, HeightGenerator generator) {
        return generator.generateHeight(x, z);
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public RawModel getModel() {
        return model;
    }

    public static float getSIZE() {
        return SIZE;
    }

    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }
}
