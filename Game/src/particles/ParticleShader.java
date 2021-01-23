package particles;

import org.lwjgl.util.vector.Matrix4f;

import org.lwjgl.util.vector.Vector2f;
import shaders.ShaderProgram;

public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/particles/particleVertexShader.glsl";
	private static final String FRAGMENT_FILE = "src/particles/particleFragmentshader.glsl";

	private int location_modelViewMatrix;
	private int location_projectionMatrix;
	private int location_texOffset1;
	private int location_texOffset2;
	private int location_texCoordInfo;

	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void setAllUniformLocations() {
		this.location_modelViewMatrix = super.getUniformLocation("modelViewMatrix");
		this.location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		this.location_texOffset1 = super.getUniformLocation("texOffset1");
		this.location_texOffset2 = super.getUniformLocation("texOffset2");
		this.location_texCoordInfo = super.getUniformLocation("texCoordInfo");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	protected void loadTextureCoordsInfo(Vector2f offset1, Vector2f offset2, float numberOfRows, float blend) {
		super.loadVector2D(this.location_texOffset1, offset1);
		super.loadVector2D(this.location_texOffset2, offset2);
		super.loadVector2D(this.location_texCoordInfo, new Vector2f(numberOfRows, blend));
	}

	protected void loadModelViewMatrix(Matrix4f modelView) {
		super.loadMatrix(this.location_modelViewMatrix, modelView);
	}

	protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(this.location_projectionMatrix, projectionMatrix);
	}

}
