package water;

import entities.Light;
import org.lwjgl.util.vector.Matrix4f;
import shaders.ShaderProgram;
import toolbox.Maths;
import entities.Camera;

public class WaterShader extends ShaderProgram {

	private final static String VERTEX_FILE = "src/water/waterVertexShader.glsl";
	private final static String FRAGMENT_FILE = "src/water/waterFragmentShader.glsl";

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;
	private int location_reflectionTexture;
	private int location_refractionTexture;
	private int location_dudvMap;
	private int location_moveFactor;
	private int location_cameraPosition;
	private int location_normalMap;
	private int location_lightPosition;
	private int location_lightColour;
	private int location_depthMap;
	private int location_near;
	private int location_far;

	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}

	@Override
	protected void setAllUniformLocations() {
		this.location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		this.location_viewMatrix = super.getUniformLocation("viewMatrix");
		this.location_modelMatrix = super.getUniformLocation("modelMatrix");
		this.location_reflectionTexture = super.getUniformLocation("reflectionTexture");
		this.location_refractionTexture = super.getUniformLocation("refractionTexture");
		this.location_dudvMap = super.getUniformLocation("dudvMap");
		this.location_moveFactor = super.getUniformLocation("moveFactor");
		this.location_cameraPosition = super.getUniformLocation("cameraPosition");
		this.location_normalMap = super.getUniformLocation("normalMap");
		this.location_lightPosition = super.getUniformLocation("lightPosition");
		this.location_lightColour = super.getUniformLocation("lightColour");
		this.location_depthMap = super.getUniformLocation("depthMap");
		this.location_near = super.getUniformLocation("near");
		this.location_far = super.getUniformLocation("far");
	}

	public void loadRenderDistances(float near, float far) {
		super.loadFloat(this.location_near, near);
		super.loadFloat(this.location_far, far);
	}

	public void loadLight(Light sun) {
		super.loadVector(this.location_lightColour, sun.getColour());
		super.loadVector(this.location_lightPosition, sun.getPosition());
	}

	public void loadMoveFactor(float factor) {
		super.loadFloat(this.location_moveFactor, factor);
	}

	public void connectTextureUnits() {
		super.loadInt(this.location_reflectionTexture, 0);
		super.loadInt(this.location_refractionTexture, 1);
		super.loadInt(this.location_dudvMap, 2);
		super.loadInt(this.location_normalMap, 3);
		super.loadInt(this.location_depthMap, 4);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(this.location_projectionMatrix, projection);
	}

	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(this.location_viewMatrix, viewMatrix);
		super.loadVector(this.location_cameraPosition, camera.getPosition());
	}

	public void loadModelMatrix(Matrix4f modelMatrix) {
		super.loadMatrix(this.location_modelMatrix, modelMatrix);
	}

}
