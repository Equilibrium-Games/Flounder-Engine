package flounder.models;

import flounder.factory.*;
import flounder.physics.*;
import flounder.processing.*;

public class ModelObject extends FactoryObject {
	private float[] vertices;
	private float[] textures;
	private float[] normals;
	private float[] tangents;
	private int[] indices;
	private boolean smoothShading;

	private String name;
	private boolean loaded;

	private AABB aabb;
	private QuickHull hull;

	private int vaoID;
	private int vaoLength;

	public ModelObject() {
		super();
		this.vertices = null;
		this.textures = null;
		this.normals = null;
		this.tangents = null;
		this.indices = null;
		this.smoothShading = false;

		this.name = null;
		this.loaded = false;
	}

	protected void loadData(float[] vertices, float[] textureCoords, float[] normals, float[] tangents, int[] indices, boolean smoothShading, AABB aabb, QuickHull hull, String name) {
		this.vertices = vertices;
		this.textures = textureCoords;
		this.normals = normals;
		this.tangents = tangents;
		this.indices = indices;
		this.smoothShading = smoothShading;

		this.name = name;
		this.loaded = true;

		this.aabb = aabb;
		this.hull = hull;
	}

	protected void loadGL(int vaoID, int vaoLength) {
		this.vaoID = vaoID;
		this.vaoLength = vaoLength;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getTextures() {
		return textures;
	}

	public float[] getNormals() {
		return normals;
	}

	public float[] getTangents() {
		return tangents;
	}

	public int[] getIndices() {
		return indices;
	}

	public boolean isSmoothShading() {
		return smoothShading;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	public AABB getAABB() {
		return aabb;
	}

	public QuickHull getHull() {
		return hull;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVaoLength() {
		return vaoLength;
	}

	/**
	 * Deletes the model from OpenGL memory.
	 */
	public void delete() {
		FlounderProcessors.sendRequest(new ModelDeleteRequest(vaoID));
		this.loaded = false;
	}
}
