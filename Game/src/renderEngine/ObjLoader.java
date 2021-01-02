package renderEngine;

import models.RawModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ObjLoader {

    private static Vector3f lastDimensions;

    public static RawModel loadObjModel(String fileName, Loader loader) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(new File("res/" + fileName + ".obj"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(fileReader);
        String line = "";

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        float[] verticesArray = null;
        float[] normalsArray = null;
        float[] textureArray = null;
        int[] indicesArray = null;

        try {

            while (true) {
                line = reader.readLine();
                String[] currentLine = line.split(" ");

                if (line.startsWith("v ")) {
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);
                } else if (line.startsWith("vt ")) {
                    Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                }  else if (line.startsWith("f ")) {
                    textureArray = new float[vertices.size() * 2];
                    normalsArray = new float[vertices.size() * 3];
                    break;
                }
            }

            while (line != null) {
                if (!line.startsWith("f ")) {
                    line = reader.readLine();
                    continue;
                }

                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");

                processVertex(vertex1, indices, textures, normals, textureArray, normalsArray);
                processVertex(vertex2, indices, textures, normals, textureArray, normalsArray);
                processVertex(vertex3, indices, textures, normals, textureArray, normalsArray);
                line = reader.readLine();
            }
            reader.close();

        } catch (Exception e) {
            //Always goes in here
        }

        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[indices.size()];

        int vertexPointer = 0;
        for (Vector3f vertex : vertices) {
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }

        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }


        calculateDimensions(verticesArray);

        return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray);
    }

    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures,
                                      List<Vector3f> normals, float[] textureArray, float[] normalsArray) {

        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);
        Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
        textureArray[currentVertexPointer * 2] = currentTex.x;
        textureArray[currentVertexPointer * 2 + 1] = 1 - currentTex.y;
        Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
        normalsArray[currentVertexPointer * 3] = currentNorm.x;
        normalsArray[currentVertexPointer * 3 + 1] = currentNorm.y;
        normalsArray[currentVertexPointer * 3 + 2] = currentNorm.z;
    }

    private static void calculateDimensions(float[] verticesArray) {
        Vector3f lastMinDimensions = new Vector3f(0, 0, 0);
        Vector3f lastMaxDimensions = new Vector3f(0, 0, 0);

        lastMaxDimensions.x = verticesArray[0];
        lastMinDimensions.x = verticesArray[0];
        for (int x = 0; x < (verticesArray.length / 3); x += 3) {
            if (verticesArray[x] > lastMaxDimensions.x) {
                lastMaxDimensions.x = verticesArray[x];
                continue;
            }

            if (verticesArray[x] < lastMinDimensions.x) {
                lastMinDimensions.x = verticesArray[x];
            }
        }

        lastMaxDimensions.y = verticesArray[0];
        lastMinDimensions.y = verticesArray[0];
        for (int y = 1; y < (verticesArray.length / 3); y += 3) {
            if (verticesArray[y] > lastMaxDimensions.y) {
                lastMaxDimensions.y = verticesArray[y];
                continue;
            }

            if (verticesArray[y] < lastMinDimensions.y) {
                lastMinDimensions.y = verticesArray[y];
            }
        }

        lastMaxDimensions.z = verticesArray[0];
        lastMinDimensions.z = verticesArray[0];
        for (int z = 2; z < (verticesArray.length / 3); z += 3) {
            if (verticesArray[z] > lastMaxDimensions.z) {
                lastMaxDimensions.z = verticesArray[z];
                continue;
            }

            if (verticesArray[z] < lastMinDimensions.z) {
                lastMinDimensions.z = verticesArray[z];
            }
        }

        lastDimensions = Vector3f.sub(lastMaxDimensions, lastMinDimensions, null);
    }

    public static Vector3f getLastDimensions() {
        return lastDimensions;
    }
}
