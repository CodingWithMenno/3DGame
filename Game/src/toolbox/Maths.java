package toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import objects.Camera;

public class Maths {

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f position, float rx, float ry,
			float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);

		if (translation != position) {
			Matrix4f.translate((Vector3f) translation.negate(), matrix, matrix);
			Matrix4f.translate(position, matrix, matrix);
		}

		Matrix4f.scale(new Vector3f(scale,scale,scale), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(camera.getRotX()), new Vector3f(1, 0, 0), viewMatrix,
				viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getRotY()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}

	//Clamps a value between the min and max
	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}

	//Clamps a vector3f between the min and max
	public static Vector3f clamp(Vector3f val, Vector3f min, Vector3f max) {
		val.x = clamp(val.x, min.x, max.x);
		val.y = clamp(val.y, min.y, max.y);
		val.z = clamp(val.z, min.z, max.z);
		return val;
	}

	//Clamps a vector2f between the min and max
	public static Vector2f clamp(Vector2f val, Vector2f min, Vector2f max) {
		val.x = clamp(val.x, min.x, max.x);
		val.y = clamp(val.y, min.y, max.y);
		return val;
	}

	//Linear interpolation
	public static float lerp(float from, float to, float amount) {
		return from + amount * (to - from);
	}

	//Linear interpolation but for vector3f
	public static Vector3f lerp(Vector3f from, Vector3f to, float amount) {
		Vector3f fromVector = new Vector3f(from);
		fromVector.x = lerp(fromVector.x, to.x, amount);
		fromVector.y = lerp(fromVector.y, to.y, amount);
		fromVector.z = lerp(fromVector.z, to.z, amount);

		return fromVector;
	}

	//returns the difference between the 2 values
	public static float difference(float value1, float value2) {
		return Math.abs(value1 - value2);
	}

	//Maps the value with the previous min and max values to the new min and max values
	public static float map(float value, float pMin, float pMax, float nMin, float nMax) {
		return (value - pMin) / (pMax - pMin) * (nMax - nMin) + nMin;
	}

	//Returns the distance between 2 3D points
	public static float getDistanceBetween(Vector3f point1, Vector3f point2) {
		return (float) Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point2.y, 2) + Math.pow(point2.z - point1.z, 2));
	}

	//Returns the 3D vector of all the 3D vectors added
	public static Vector3f add(Vector3f... vectors) {
		Vector3f finalVector = new Vector3f(0, 0, 0);
		for (Vector3f vector : vectors) {
			finalVector = Vector3f.add(finalVector, vector, null);
		}
		return finalVector;
	}

	//Returns the 2D vector of all the 2D vectors added
	public static Vector2f add(Vector2f... vectors) {
		Vector2f finalVector = new Vector2f(0, 0);
		for (Vector2f vector : vectors) {
			finalVector = Vector2f.add(finalVector, vector, null);
		}
		return finalVector;
	}

	//Divides a 3D vector by the given number
	public static Vector3f divide(Vector3f vector, float number) {
		vector.x /= number;
		vector.y /= number;
		vector.z /= number;
		return vector;
	}

	//Divides a 2D vector by the given number
	public static Vector2f divide(Vector2f vector, float number) {
		vector.x /= number;
		vector.y /= number;
		return vector;
	}
}
