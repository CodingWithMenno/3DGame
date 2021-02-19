package renderEngine;

import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import shaders.TerrainShader;
import shadows.ShadowBox;
import shadows.ShadowMapMasterRenderer;
import terrains.Biome;
import terrains.Terrain;
import toolbox.Maths;
import user.Settings;

import java.util.List;

public class TerrainRenderer {

    private TerrainShader shader;

    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        this.shader.start();
        this.shader.loadProjectionMatrix(projectionMatrix);
        this.shader.connectTextureUnits();
        this.shader.loadShadowDistanceAndSize(Settings.SHADOW_DISTANCE, Settings.SHADOW_MAP_SIZE);
        this.shader.stop();
    }

    public void render(List<Terrain> terrains, Matrix4f toShadowSpace) {
        this.shader.loadToShadowSpaceMatrix(toShadowSpace);
        for (Terrain terrain : terrains) {
            prepareTerrain(terrain);
            loadModelMatrix(terrain);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindTexturedModel();
        }
    }

    private void prepareTerrain(Terrain terrain) {
        RawModel rawModel = terrain.getModel();
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        bindTextures(terrain);
        this.shader.loadShineVariables(1, 0);
    }

    private void bindTextures(Terrain terrain) {
        List<Biome> biomes = terrain.getBiomes();
        int totalBiomes = biomes.size();

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, biomes.get(0).getGroundTexture().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, totalBiomes > 1 ? biomes.get(1).getGroundTexture().getTextureID() : biomes.get(0).getGroundTexture().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, totalBiomes > 2 ? biomes.get(2).getGroundTexture().getTextureID() : biomes.get(0).getGroundTexture().getTextureID());
        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, totalBiomes > 3 ? biomes.get(3).getGroundTexture().getTextureID() : biomes.get(0).getGroundTexture().getTextureID());

        this.shader.loadBiomeSeparations(biomes);
    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    private void loadModelMatrix(Terrain terrain) {
        Vector3f pos = new Vector3f(terrain.getX(), 0, terrain.getZ());
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(
                pos, pos, 0, 0, 0, 1);
        this.shader.loadTransformationMatrix(transformationMatrix);
    }
}
