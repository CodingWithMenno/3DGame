package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;

public class Camera {

	private static float MOUSE_SENSITIVITY = 0.05f;

	private float distanceFromEntity = 50;
	private float angleAroundPlayer = 0;
	private Entity entityToFollow;

	private Vector3f position;
	private float pitch = 15;
	private float yaw;
	private float roll;

	public Camera(Entity entityToFollow) {
		this.entityToFollow = entityToFollow;
		this.position = new Vector3f(100, 35, 50);
	}

	public void move(){
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();

		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);

		this.yaw = 180 - (this.entityToFollow.getRotY() + this.angleAroundPlayer);
	}

	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float theta = this.entityToFollow.getRotY() + this.angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		this.position.x = this.entityToFollow.getPosition().x - offsetX;
		this.position.z = this.entityToFollow.getPosition().z - offsetZ;
		this.position.y = (this.entityToFollow.getPosition().y + verticalDistance) + 4;
	}

	private float calculateHorizontalDistance() {
		return (float) (this.distanceFromEntity * Math.cos(Math.toRadians(this.pitch)));
	}

	private float calculateVerticalDistance() {
		return (float) (this.distanceFromEntity * Math.sin(Math.toRadians(this.pitch)));
	}

	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.05f;
		this.distanceFromEntity -= zoomLevel;
		this.distanceFromEntity = Maths.clamp(this.distanceFromEntity, 8, 56);
	}

	private void calculatePitch() {
		float pitchChange = Mouse.getDY() * MOUSE_SENSITIVITY;
		this.pitch -= pitchChange;
	}

	private void calculateAngleAroundPlayer() {
		float angleChange = Mouse.getDX() * MOUSE_SENSITIVITY * 3f;
		this.angleAroundPlayer -= angleChange;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
}
