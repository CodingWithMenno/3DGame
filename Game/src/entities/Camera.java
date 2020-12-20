package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;

public class Camera {

	private static float MOUSE_SENSITIVITY = 0.05f;
	private static float CAMERA_FOLLOW_SPEED = 0.2f;
	private static float AUTO_ZOOM = 0.001f;
	private static float DEFAULT_PITCH = 12;
	private static float MAX_ZOOM_IN = 8;
	private static float MAX_ZOOM_OUT = 56;

	private float distanceFromEntity = 20;
	private float angleAroundPlayer = 0;
	private Entity entityToFollow;

	private Vector3f position;
	private float pitch = DEFAULT_PITCH;
	private float yaw;
	private float roll;

	public Camera(Entity entityToFollow) {
		this.entityToFollow = entityToFollow;
		Vector3f pos = this.entityToFollow.getPosition();
		this.position = new Vector3f(pos.x, pos.y + 8, pos.z - 20);
	}

	public void move(){
		calculateZoom();
		calculatePitch();
		calculateAngleAroundPlayer();

		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();

		this.position = calculateCameraPosition(horizontalDistance, verticalDistance);
//		this.position.x = Maths.lerp(this.position.x, newPos.x, CAMERA_FOLLOW_SPEED);
//		this.position.z = Maths.lerp(this.position.z, newPos.z, CAMERA_FOLLOW_SPEED);
//		this.position.y = Maths.lerp(this.position.y, newPos.y, CAMERA_FOLLOW_SPEED);

		this.yaw = 180 - (this.entityToFollow.getRotY() + this.angleAroundPlayer);
	}

	private Vector3f calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float theta = this.entityToFollow.getRotY() + this.angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		float toPosX = this.entityToFollow.getPosition().x - offsetX;
		float toPosZ = this.entityToFollow.getPosition().z - offsetZ;
		float toPosY = (this.entityToFollow.getPosition().y + verticalDistance) + 4;

		return new Vector3f(toPosX, toPosY, toPosZ);
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
		this.distanceFromEntity = Maths.clamp(this.distanceFromEntity, MAX_ZOOM_IN, MAX_ZOOM_OUT);
	}

	private void calculatePitch() {
		float pitchChange = Mouse.getDY() * MOUSE_SENSITIVITY;
		this.pitch -= pitchChange;

		if (pitchChange == 0 && this.pitch != DEFAULT_PITCH) {
			this.pitch = Maths.lerp(this.pitch, DEFAULT_PITCH, AUTO_ZOOM);
		}

		this.pitch = Maths.clamp(this.pitch, -6, 85);
	}

	private void calculateAngleAroundPlayer() {
		float angleChange = Mouse.getDX() * MOUSE_SENSITIVITY * 3f;
		this.angleAroundPlayer -= angleChange;

		if (angleChange == 0 && this.angleAroundPlayer != 0) {
			this.angleAroundPlayer = Maths.lerp(this.angleAroundPlayer, 0, AUTO_ZOOM);
		}
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
