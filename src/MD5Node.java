package com.yuredd.asmalllife;
import com.badlogic.gdx.ApplicationListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;

import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector3;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.IndexedMesh;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import javax.vecmath.Vector3f;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import com.sun.opengl.util.BufferUtil;


import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;

import com.badlogic.gdx.graphics.g3d.loaders.md5.MD5Animation;
import com.badlogic.gdx.graphics.g3d.loaders.md5.MD5AnimationInfo;
import com.badlogic.gdx.graphics.g3d.loaders.md5.MD5Joints;
import com.badlogic.gdx.graphics.g3d.loaders.md5.MD5Loader;
import com.badlogic.gdx.graphics.g3d.loaders.md5.MD5Model;
import com.badlogic.gdx.graphics.g3d.loaders.md5.MD5Renderer;

import com.badlogic.gdx.math.collision.BoundingBox;

public class MD5Node {

	MD5Model model;
	MD5Joints skeleton;
	MD5Renderer renderer;
	ObjectArrayList<MD5Animation> anims = new ObjectArrayList<MD5Animation>();
	ObjectArrayList<MD5AnimationInfo> animinfos = new ObjectArrayList<MD5AnimationInfo>();
	int curanim = 0;

	public MD5Node(String file) {

		MD5Loader loader = new MD5Loader();
		model = loader.loadModel(Gdx.files.internal(file).read(), false);
		renderer = new MD5Renderer(model,false,true);
		renderer.setSkeleton(model.baseSkeleton);

	}

	public int loadAnimation(String file) {

		anims.add(MD5Loader.loadAnimation(Gdx.files.internal(file).read()));			
		skeleton = new MD5Joints();
		skeleton.joints = new float[anims.get(anims.size()-1).frames[0].joints.length];
		animinfos.add(new MD5AnimationInfo(anims.get(anims.size()-1).frames.length,anims.get(anims.size()-1).secondsPerFrame));
		return anims.size()-1;

	}

	public void setAnimation(int animid) {

		curanim = animid;

	}

	public void render() {

		animinfos.get(curanim).update(Gdx.graphics.getDeltaTime());
		MD5Animation.interpolate(anims.get(curanim).frames[animinfos.get(curanim).getCurrentFrame()],
		 anims.get(curanim).frames[animinfos.get(curanim).getNextFrame()], 
		 skeleton, animinfos.get(curanim).getInterpolation());
		renderer.setSkeleton(skeleton);
		renderer.render();

	}
}
