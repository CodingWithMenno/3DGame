package shaders;

import objects.Light;
import org.lwjgl.util.vector.Matrix4f;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import toolbox.Maths;

import objects.Camera;

import java.util.List;

public class StaticShader extends ShaderProgram{

	private static final int MAX_LIGHTS = 5;

	private static final String VERTEX_FILE = "src/shaders/vertexShader.glsl";
	private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.glsl";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightColour[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_skyColour;
	private int location_numberOfRows;
	private int location_offset;
	private int location_density;
	private int location_plane;
	private int location_toShadowMapSpace;
	private int location_shadowMap;
	private int location_shadowDistance;
	private int location_shadowMapSize;


	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void setAllUniformLocations() {
		this.location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		this.location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		this.location_viewMatrix = super.getUniformLocation("viewMatrix");
		this.location_shineDamper = super.getUniformLocation("shineDamper");
		this.location_reflectivity = super.getUniformLocation("reflectivity");
		this.location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		this.location_skyColour = super.getUniformLocation("skyColour");
		this.location_numberOfRows = super.getUniformLocation("numberOfRows");
		this.location_offset = super.getUniformLocation("offset");
		this.location_density = super.getUniformLocation("density");
		this.location_plane = super.getUniformLocation("plane");
		this.location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
		this.location_shadowMap = super.getUniformLocation("shadowMap");
		this.location_shadowDistance = super.getUniformLocation("shadowDistance");
		this.location_shadowMapSize = super.getUniformLocation("shadowMapSize");

		this.location_lightPosition = new int[MAX_LIGHTS];
		this.location_lightColour = new int[MAX_LIGHTS];
		this.location_attenuation = new int[MAX_LIGHTS];
		for (int i = 0; i < MAX_LIGHTS; i++) {
			this.location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			this.location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
			this.location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
	}

	public void connectTextureUnits() {
		super.loadInt(this.location_shadowMap, 5);
	}

	public void loadShadowDistanceAndSize(float distance, float size) {
		super.loadFloat(this.location_shadowDistance, distance);
		super.loadFloat(this.location_shadowMapSize, size);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(this.location_transformationMatrix, matrix);
	}

	public void loadToShadowSpaceMatrix(Matrix4f matrix) {
		super.loadMatrix(this.location_toShadowMapSpace, matrix);
	}

	public void loadClipPlane(Vector4f plane) {
		super.loadVector(this.location_plane, plane);
	}

	public void loadDensity(float density) {
		super.loadFloat(this.location_density, density);
	}

	public void loadNumberOfRows(int numberOfRows) {
		super.loadFloat(this.location_numberOfRows, numberOfRows);
	}

	public void loadOffset(float x, float y) {
		super.loadVector2D(this.location_offset, new Vector2f(x, y));
	}

	public void loadSkyColour(float r, float g, float b) {
		super.loadVector(this.location_skyColour, new Vector3f(r, g, b));
	}

	public void loadFakeLightingVariable(boolean useFakeLighting) {
		super.loadBoolean(this.location_useFakeLighting, useFakeLighting);
	}

	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(this.location_shineDamper, damper);
		super.loadFloat(this.location_reflectivity, reflectivity);
	}

	public void loadLights(List<Light> lights) {
		for (int i = 0; i < MAX_LIGHTS; i++) {
			if (i < lights.size()) {
				super.loadVector(this.location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector(this.location_lightColour[i], lights.get(i).getColour());
				super.loadVector(this.location_attenuation[i], lights.get(i).getAttenuation());
			} else {
				super.loadVector(this.location_lightPosition[i], new Vector3f(0, 0, 0));
				super.loadVector(this.location_lightColour[i], new Vector3f(0, 0, 0));
				super.loadVector(this.location_attenuation[i], new Vector3f(1, 0, 0));
			}
		}
	}
	
	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(this.location_viewMatrix, viewMatrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(this.location_projectionMatrix, projection);
	}
	
	

}
