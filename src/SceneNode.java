package com.yuredd.asmalllife;

import javax.vecmath.Vector3f;

public class SceneNode {

	public int md5nodeid = -1;
	public int meshid = -1;
	public int textureid = -1;
	public int shapeid = -1;
	public float[] matrix = {
								1f, 0f, 0f, 0f,
								0f, 1f, 0f, 0f,
								0f, 0f, 1f, 0f,
								0f, 0f, 0f, 1f
	};
	public Vector3f position = new Vector3f(0f, 0f, 0f);
	public Vector3f rotation = new Vector3f(0f, 0f, 0f);

}
