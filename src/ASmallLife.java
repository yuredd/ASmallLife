package com.yuredd.asmalllife;

import com.yuredd.asmalllife.SceneManager;
import com.yuredd.asmalllife.SceneNode;

import com.badlogic.gdx.ApplicationListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.GL11;

import javax.vecmath.Vector3f;
import com.badlogic.gdx.math.Vector3;

public class ASmallLife implements ApplicationListener
{

	SceneManager sm = new SceneManager();

	private int walls;
	private int floor;
	private int crate;
	private int giggio;
	private int rappo;

	float movex = 0f;
	float movey = 0f;
	float movez = 0f;
	float roty = 0f;

	private void handleInput() {

		movex = 0f;
		movey = 0f;
		movez = 0f;

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

		if(movex != 0 || movez != 0) {
			sm.setNodeAnimation(giggio,1);
		} else {
			sm.setNodeAnimation(giggio,0);
		}

		sm.setModelRot(giggio, 3, roty);

		sm.applyImpulse(giggio, new Vector3f(movex,0f,movez), new Vector3f(0f,0f,0f));

	}


  @Override
  public void create() {

		System.out.println("======= Loading Physics...");

		sm.initPhysics();

		System.out.println("======= Loading Files...");

		//carica mesh e tex

		System.out.println("Loading Walls...");

		walls = sm.addSceneNode();
		sm.loadStillGeometry(walls, "data/level1.obj");
		sm.loadTexture(walls, "data/level1.png");
		sm.addMeshShape(walls);

		System.out.println("wall data: " + sm.getNode(walls).meshid + ", " + sm.getNode(walls).textureid + ", " + sm.getNode(walls).shapeid);

		System.out.println("Loading Floors...");

		floor = sm.addSceneNode();
		sm.loadStillGeometry(floor, "data/level1floor.obj");
		sm.loadTexture(floor, "data/marble-pink.png");


		System.out.println("Loading Crate...");

		crate = sm.addSceneNode();
		sm.loadStillGeometry(crate, "data/objecttest.obj");
		sm.loadTexture(crate, "data/textest.png");
		sm.addBoxShape(crate, new Vector3f(0.5f, 0.5f, 0.5f));

		System.out.println("Loading Giggio...");

		giggio = sm.addSceneNode();
		sm.loadAnimModel(giggio, "data/giggio.md5mesh");
		sm.loadAnimation(giggio, "data/giggio-grat.md5anim");
		sm.loadAnimation(giggio, "data/giggio-walk.md5anim");
		sm.loadTexture(giggio, "data/giggio.png");
		sm.addActorShape(giggio, new Vector3f(0.4f, 0.5f, 0.4f));
		sm.setModelPos(giggio, new Vector3f(0f,2f,0f));
		sm.setModelRot(giggio, 1, -90f);

		System.out.println("Loading Rappo...");

		rappo = sm.addSceneNode();
		sm.loadAnimModel(rappo, "data/rappo.md5mesh");
		sm.loadAnimation(rappo, "data/rappo-prendi.md5anim");
		sm.loadTexture(rappo, "data/rappo.png");
		sm.addActorShape(rappo, new Vector3f(0.4f, 0.5f, 0.4f));
		sm.setModelPos(rappo, new Vector3f(0f,2f,2f));
		sm.setModelRot(rappo, 1, -90f);

		System.out.println("Loaded " + sm.getTotalNodes() + " nodes, ready.");

		// OpenGL enable functions
		GL11 gl = Gdx.graphics.getGL11(); 
		gl.glEnable(gl.GL_TEXTURE_2D);  
		gl.glEnable(gl.GL_DEPTH_TEST);
		gl.glEnable(gl.GL_BLEND);

// wireframe mode
// gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_LINE);

		System.out.println("Starting...");
  }

  @Override
  public void render() {

		GL11 gl = Gdx.graphics.getGL11(); 
		float[] tmp = new float[16];

		handleInput();
		sm.updatePhysics();

		sm.cam.position.x = sm.getNode(giggio).position.x;
		sm.cam.position.y = sm.getNode(giggio).position.y +7f;
		sm.cam.position.z = sm.getNode(giggio).position.z -4f;
		sm.cam.lookAt(sm.getNode(giggio).position.x,sm.getNode(giggio).position.y,sm.getNode(giggio).position.z);

		sm.cam.update();
		sm.cam.apply(gl);

		gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);

// ===================================
// test mouse pointer ================
// ===================================

//		Vector3 vec = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
//		sm.cam.unproject(vec);
//		cratetex.bind();
//		gl.glPushMatrix();
//		gl.glTranslatef(vec.x, vec.y, vec.z);
//		crate.render(gl.GL_TRIANGLES);
//		gl.glPopMatrix();
// ===================================				






// ===================================
// render loop =======================
// ===================================

    for (int j= sm.getTotalNodes()-1; j>=0; j--) {

			sm.updateNode(j);
			sm.renderNode(j);

		}

// ===================================				

  }

  @Override
  public void resume() {

  }
  
  @Override
  public void resize(int width, int height) {
		float aspectRatio = (float) width / (float) height;
		sm.cam = new PerspectiveCamera(67, 2f * aspectRatio, 2f);
  }
  
  @Override
  public void pause() {

  }

  @Override
  public void dispose() {
  
  }




}

