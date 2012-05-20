package com.yuredd.asmalllife;

import com.yuredd.asmalllife.SceneManager;

import com.badlogic.gdx.ApplicationListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.GL11;

public class ASmallLife implements ApplicationListener
{

	SceneManager sm = new SceneManager();

	// camera, geometry and textures
	private PerspectiveCamera cam;
	private int walls;
	private int floor;

	private int crate;
	private int wallstex;
	private int cratetex;
	private int floortex;
	private int giggiotex;

	float movex = 0f;
	float movey = 0f;
	float movez = 0f;
	float rotx = 0f;
	float roty = 0f;


	private void handleInput() {

		movex = 0f;
		movey = 0f;
		movez = 0f;

    CollisionObject obj = dynamicsWorld.getCollisionObjectArray().getQuick(1);
    RigidBody body = RigidBody.upcast(obj);

		// rileva gli eventi di tastiera
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) movex = 0.1f;
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) movex = -0.1f;
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) movez = -0.1f;
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) movez = 0.1f;

		if(movex == 0 && movez > 0) {
			roty = 0;
		} else if(movex == 0 && movez < 0) {
			roty = 180;
		} else if(movex > 0 && movez == 0) {
			roty = 90;
		} else if(movex < 0 && movez == 0) {
			roty = 270;

		} else if(movex > 0 && movez > 0) {
			roty = 45;
		} else if(movex < 0 && movez < 0) {
			roty = 180+45;
		} else if(movex > 0 && movez < 0) {
			roty = 90+45;
		} else if(movex < 0 && movez > 0) {
			roty = 270+45;
		}

		body.applyImpulse(new Vector3f(movex,0f,movez), new Vector3f(0f,0f,0f));

	}



  public void addCrate(float x, float y, float z) {

		int tid = sm.addBoxShape(0.5f, 0.5f, 0.5f);
		sm.setShapePos(tid, x, y, z);

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
		walls = sm.loadStillGeometry("data/level1.obj");
		floor = sm.loadStillGeometry("data/level1floor.obj");
		crate = sm.loadStillGeometry("data/objecttest.obj");

		sm.loadAnimModel("data/giggio.md5mesh");
		sm.loadAnimation("data/giggio-grat.md5anim", 0);
		sm.loadAnimation("data/giggio-walk.md5anim", 0);

		wallstex = sm.loadTexture("data/level1.png");
		floortex = sm.loadTexture("data/marble-pink.png");
		cratetex = sm.loadTexture("data/textest.png");
		giggiotex = sm.loadTexture("data/giggio.png");


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

//		eroe
//		addBox(0,2f,0, 0.5f,1.5f,0.4f, 0.4f,0f);
		addBox(0,2f,0, 0.4f,0.5f,0.4f, 0.4f,0f);


		addCrate(5,1f,5);
		addCrate(6.1f,1f,5);
		addCrate(7.2f,1f,5);

		addCrate(5.5f,2f,5);
		addCrate(6.6f,2f,5);

		addCrate(6.1f,3f,5);


		addCrate(8f,1f,5);
		addCrate(8f,2f,5);
		addCrate(8f,3f,5);
		addCrate(8f,4f,5);
		addCrate(8f,5f,5);
		addCrate(8f,6f,5);
		addCrate(8f,7f,5);
		addCrate(8f,8f,5);
		addCrate(8f,9f,5);
		addCrate(8f,10f,5);
		addCrate(8f,11f,5);
		addCrate(8f,12f,5);
		addCrate(8f,13f,5);
		addCrate(8f,14f,5);
		addCrate(8f,15f,5);
		addCrate(8f,16f,5);
		addCrate(8f,17f,5);
		addCrate(8f,18f,5);
		addCrate(8f,19f,5);
		addCrate(8f,20f,5);


		addCrate(1f,4f,8);
		addCrate(3f,4f,2);
		addCrate(-4f,4f,3);
		addCrate(3f,4f,6);
		addCrate(0f,4f,1);
		addCrate(3f,4f,2);
		addCrate(4f,4f,5);


		addMesh(sm.listMesh.get(walls), 0, 0, 0);


		// OpenGL enable functions
		GL11 gl = Gdx.graphics.getGL11(); 
		gl.glEnable(gl.GL_TEXTURE_2D);  
		gl.glEnable(gl.GL_DEPTH_TEST);
		gl.glEnable(gl.GL_BLEND);

// wireframe mode
// gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_LINE);

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

		GL11 gl = Gdx.graphics.getGL11(); 
		float[] tmp = new float[16];

		handleInput();
		updatePhysics();
		cam.update();
		cam.apply(gl);

		gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);

// ===================================
// test mouse pointer ================
// ===================================

		Vector3 vec = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		cam.unproject(vec);
//		cratetex.bind();
		gl.glPushMatrix();
		gl.glTranslatef(vec.x, vec.y, vec.z);
//		crate.render(gl.GL_TRIANGLES);
		gl.glPopMatrix();
// ===================================				

		sm.bindTexture(floortex);
		sm.renderMesh(floor);

		sm.bindTexture(wallstex);
		sm.renderMesh(walls);

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
					gl.glRotatef(roty,0f,0f,1f);
					sm.bindTexture(giggiotex);


					if(movex == 0 && movez == 0) {

						sm.setAnimation(0,0);

					} else {

						sm.setAnimation(0,1);

					}

					sm.renderActor(0);
					sm.bindTexture(cratetex);

				} else {

					sm.bindTexture(cratetex);
					sm.renderMesh(crate);

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

