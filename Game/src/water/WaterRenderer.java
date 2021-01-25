package water;

import java.util.List;

import entities.Light;
import models.RawModel;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import toolbox.Maths;
import entities.Camera;

public class WaterRenderer {

	private static final String DUDV_MAP = "water/WaterDUDV";
	private static final String NORMAL_MAP = "water/WaterNormal";
	private static final float WAVE_SPEED = 0.01f;

	private RawModel quad;
	private WaterShader shader;
	private WaterFrameBuffers buffers;

	private float moveFactor = 0;

	private int dudvTexture;
	private int normalMap;

	public WaterRenderer(Loader loader, WaterShader shader, Matrix4f projectionMatrix, WaterFrameBuffers buffers) {
		this.shader = shader;
		this.buffers = buffers;
		this.dudvTexture = loader.loadTexture(DUDV_MAP);
		this.normalMap = loader.loadTexture(NORMAL_MAP);
		this.shader.start();
		this.shader.loadRenderDistances(MasterRenderer.NEAR_PLANE, MasterRenderer.FAR_PLANE);
		this.shader.connectTextureUnits();
		this.shader.loadProjectionMatrix(projectionMatrix);
		this.shader.stop();
		setUpVAO(loader);
	}

	public void render(List<WaterTile> water, Camera camera, Light sun) {
		prepareRender(camera, sun);
		for (WaterTile tile : water) {
			Matrix4f modelMatrix =
					Maths.createTransformationMatrix(new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0, WaterTile.TILE_SIZE);
			this.shader.loadModelMatrix(modelMatrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, this.quad.getVertexCount());
		}
		unbind();
	}
	
	private void prepareRender(Camera camera, Light sun) {
		this.shader.start();
		this.shader.loadViewMatrix(camera);

		this.moveFactor += WAVE_SPEED * DisplayManager.getDelta();
		this.moveFactor %= 1;
		this.shader.loadMoveFactor(this.moveFactor);

		this.shader.loadLight(sun);

		GL30.glBindVertexArray(this.quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.buffers.getReflectionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.buffers.getRefractionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.dudvTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.normalMap);
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.buffers.getRefractionDepthTexture());

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private void unbind() {
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		this.shader.stop();
	}

	private void setUpVAO(Loader loader) {
		float[] vertices = { -1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1 };
		this.quad = loader.loadToVAO(vertices);
	}

}
