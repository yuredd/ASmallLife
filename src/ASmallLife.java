package com.yuredd.asmalllife;
import com.badlogic.gdx.ApplicationListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import com.badlogic.gdx.math.Vector3;
import java.nio.FloatBuffer;
import javax.vecmath.Vector3f;

import com.badlogic.gdx.graphics.g3d.loaders.md5.MD5Animation;
import com.badlogic.gdx.graphics.g3d.loaders.md5.MD5AnimationInfo;
import com.badlogic.gdx.graphics.g3d.loaders.md5.MD5Joints;
import com.badlogic.gdx.graphics.g3d.loaders.md5.MD5Loader;
import com.badlogic.gdx.graphics.g3d.loaders.md5.MD5Model;
import com.badlogic.gdx.graphics.g3d.loaders.md5.MD5Renderer;

import com.badlogic.gdx.math.collision.BoundingBox;

public class ASmallLife implements ApplicationListener
{

	// collision world
	DiscreteDynamicsWorld dynamicsWorld;
	ObjectArrayList<CollisionShape> collisionShapes = new ObjectArrayList<CollisionShape>();

	// camera, geometry and textures
	private PerspectiveCamera cam;
	private Mesh walls;
	private Mesh floor;
	private Mesh hero;

	private MD5Model giggio;
	private MD5Joints giggioskeleton;
	private MD5Animation giggiograt;
	private MD5AnimationInfo giggiogratinfo;
	private MD5Animation giggiowalk;
	private MD5AnimationInfo giggiowalkinfo;
	private MD5Renderer renderer;
	private Mesh crate;
	private Texture wallstex;
	private Texture cratetex;
	private Texture floortex;
	private Texture giggiotex;

	private int animchoose = 1;

	private void handleInput() {

		float movex = 0f;
		float movey = 0f;
		float movez = 0f;
		float rotx = 0f;
		float roty = 0f;

    CollisionObject obj = dynamicsWorld.getCollisionObjectArray().getQuick(1);
    RigidBody body = RigidBody.upcast(obj);

		// rileva gli eventi di tastiera
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) movex = 0.1f;
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) movex = -0.1f;
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) movez = -0.1f;
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) movez = 0.1f;

		if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)) animchoose = 1;
		if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)) animchoose = 2;

		body.applyImpulse(new Vector3f(movex,0f,movez), new Vector3f(0f,0f,0f));

	}

  public void addCrate(float x, float y, float z) {

		addBox(x,y,z,0.5f,0.5f,0.5f,0.5f);

	}

  public void addBox(float x, float y, float z, float lenx, float leny, float lenz, float mass) {

		CollisionShape shape = new BoxShape(new Vector3f(lenx, leny, lenz));
//		CollisionShape shape = new SphereShape(0.25f);
		collisionShapes.add(shape);
    Transform shapeTransform = new Transform();
		Vector3f localInertia = new Vector3f(0, 0, 0);
		shape.calculateLocalInertia(mass, localInertia);
    shapeTransform.setIdentity();
    shapeTransform.origin.set(new Vector3f(x, y, z));
		DefaultMotionState myMotionState = new DefaultMotionState(shapeTransform);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(
          mass, myMotionState, shape, localInertia);
		RigidBody body = new RigidBody(rbInfo);
		body.setActivationState(body.DISABLE_DEACTIVATION);
		dynamicsWorld.addRigidBody(body);

	}

  @Override
  public void create() {

		System.out.println("Loading Physics...");

		// inizializza physics
		CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
    Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
    int maxProxies = 1024;
    AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(
        dispatcher, overlappingPairCache, solver,
        collisionConfiguration);

		System.out.println("Loading Files...");

		//carica mesh e tex
		walls = ObjLoader.loadObj(Gdx.files.internal("data/level1.obj").read(), true);
		floor = ObjLoader.loadObj(Gdx.files.internal("data/level1floor.obj").read(), true);

		hero = ObjLoader.loadObj(Gdx.files.internal("data/herotest.obj").read(), true);
		crate = ObjLoader.loadObj(Gdx.files.internal("data/objecttest.obj").read(), true);

		BoundingBox bb = crate.calculateBoundingBox();
		System.out.println("BB crate " + bb.min.x + "::" + bb.min.y + "::" + bb.min.z);
		System.out.println("BB crate " + bb.max.x + "::" + bb.max.y + "::" + bb.max.z);

		giggio = MD5Loader.loadModel(Gdx.files.internal("data/giggio.md5mesh").read(), false);
		giggiograt = MD5Loader.loadAnimation(Gdx.files.internal("data/giggio.md5anim").read());
		giggiowalk = MD5Loader.loadAnimation(Gdx.files.internal("data/walk.md5anim").read());

		giggioskeleton = new MD5Joints();
		giggioskeleton.joints = new float[giggiograt.frames[0].joints.length];

		giggiogratinfo = new MD5AnimationInfo(giggiograt.frames.length,giggiograt.secondsPerFrame);
		giggiowalkinfo = new MD5AnimationInfo(giggiowalk.frames.length,giggiowalk.secondsPerFrame);

		renderer = new MD5Renderer(giggio,false,true);
		renderer.setSkeleton(giggio.baseSkeleton);

		wallstex = new Texture(Gdx.files.internal("data/paper-grey.png"));
		floortex = new Texture(Gdx.files.internal("data/marble-pink.png"));

		cratetex = new Texture(Gdx.files.internal("data/textest.png"));

		giggiotex = new Texture(Gdx.files.internal("data/giggio.png"));


		//crea pavimento per physics
		CollisionShape groundShape = new BoxShape(new Vector3f(150.f, 1f, 150.f));
		collisionShapes.add(groundShape);
    Transform groundTransform = new Transform();
    groundTransform.setIdentity();
    groundTransform.origin.set(new Vector3f(0.f, -0.5f, 0.f));
		DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(
          0f, myMotionState, groundShape, new Vector3f(0, 0, 0));
		RigidBody body = new RigidBody(rbInfo);
		dynamicsWorld.addRigidBody(body);


		//creazione oggetti

//		addCrate(0,1f,0); eroe
		addBox(0,2f,0, 0.5f,1.5f,0.5f, 0.5f);


		addCrate(5,1f,2);
		addCrate(6.1f,1f,2);
		addCrate(7.2f,1f,2);

		addCrate(5.5f,2f,2);
		addCrate(6.6f,2f,2);

		addCrate(6.1f,3f,2);


		addCrate(8f,1f,2);
		addCrate(8f,2f,2);
		addCrate(8f,3f,2);
		addCrate(8f,4f,2);
		addCrate(8f,5f,2);
		addCrate(8f,6f,2);
		addCrate(8f,7f,2);
		addCrate(8f,8f,2);
		addCrate(8f,9f,2);
		addCrate(8f,10f,2);
		addCrate(8f,11f,2);
		addCrate(8f,12f,2);
		addCrate(8f,13f,2);
		addCrate(8f,14f,2);
		addCrate(8f,15f,2);
		addCrate(8f,16f,2);
		addCrate(8f,17f,2);
		addCrate(8f,18f,2);
		addCrate(8f,19f,2);
		addCrate(8f,20f,2);

		// OpenGL enable functions
		GL11 gl = Gdx.graphics.getGL11(); 
		gl.glEnable(gl.GL_TEXTURE_2D);  
		gl.glEnable(gl.GL_DEPTH_TEST);
		gl.glEnable(gl.GL_BLEND);

		System.out.println("Starting...");
  }

	public void updatePhysics() {
		dynamicsWorld.stepSimulation(1.f / 60.f, 10);

      for (int j=dynamicsWorld.getNumCollisionObjects()-1; j>=0; j--)
      {
        CollisionObject obj = dynamicsWorld.getCollisionObjectArray().getQuick(j);
        RigidBody body = RigidBody.upcast(obj);
        if (body != null && body.getMotionState() != null) {
          Transform trans = new Transform();
          body.getMotionState().getWorldTransform(trans);
					if(j == 1) {

						// if it's the player, update position and camera
						cam.position.x = trans.origin.x;
						cam.position.y = trans.origin.y +7f;
						cam.position.z = trans.origin.z -4f;
						cam.lookAt(trans.origin.x,trans.origin.y,trans.origin.z);

					}
				}
      }

		// check managed collision
		int totpairs = dynamicsWorld.getPairCache().getNumOverlappingPairs();
//		System.out.println("Pairs: " + totpairs);
	}

  @Override
  public void render() {

//		FloatBuffer floatBuffer = BufferUtil.newFloatBuffer(16);
		GL11 gl = Gdx.graphics.getGL11(); 
		float[] tmp = new float[16];

		handleInput();
		updatePhysics();
		cam.update();
		cam.apply(gl);

		gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);


		

		floortex.bind();
					floor.render(gl.GL_TRIANGLES);
		wallstex.bind();
					walls.render(gl.GL_TRIANGLES);

//		level.render(gl.GL_TRIANGLES);

    for (int j=dynamicsWorld.getNumCollisionObjects()-1; j>=1; j--) {
    	CollisionObject obj = dynamicsWorld.getCollisionObjectArray().getQuick(j);
      RigidBody body = RigidBody.upcast(obj);
      if (body != null && body.getMotionState() != null) {

        Transform trans = new Transform();
        body.getMotionState().getWorldTransform(trans);

				gl.glPushMatrix();


				gl.glMatrixMode(gl.GL_MODELVIEW);

				trans.getOpenGLMatrix(tmp);
				gl.glMultMatrixf(FloatBuffer.wrap(tmp));
				if(j == 1) {

				gl.glRotatef(-90f,1f,0f,0f);

					giggiotex.bind();

if(animchoose == 1) {
					giggiogratinfo.update(Gdx.graphics.getDeltaTime());
					MD5Animation.interpolate(giggiograt.frames[giggiogratinfo.getCurrentFrame()], giggiograt.frames[giggiogratinfo.getNextFrame()], 
					 giggioskeleton, giggiogratinfo.getInterpolation());
} else if(animchoose == 2) {
					giggiowalkinfo.update(Gdx.graphics.getDeltaTime());
					MD5Animation.interpolate(giggiowalk.frames[giggiowalkinfo.getCurrentFrame()], giggiowalk.frames[giggiowalkinfo.getNextFrame()], 
					 giggioskeleton, giggiowalkinfo.getInterpolation());
}
					renderer.setSkeleton(giggioskeleton);
					renderer.render();

					cratetex.bind();

				} else {
					crate.render(gl.GL_TRIANGLES);
				}

				gl.glPopMatrix();

			}
		}
  }

  @Override
  public void resume() {

  }
  
  @Override
  public void resize(int width, int height) {
		float aspectRatio = (float) width / (float) height;
		cam = new PerspectiveCamera(67, 2f * aspectRatio, 2f);
  }
  
  @Override
  public void pause() {

  }

  @Override
  public void dispose() {
  
  }




}

