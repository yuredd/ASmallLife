package com.yuredd.asmalllife;

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

import com.yuredd.asmalllife.MD5Node;
import com.yuredd.asmalllife.SceneNode;

public class SceneManager
{

/*
|
|
loadStillGeomentry(String file)
loadTexture(String file)
bindTexture(int textureid)

addBoxShape(float lx, float ly, float lz);
addMeshShape(int modelid);
setShapePos(int shapeid, float x, float y, float z);

loadAnimModel(String file)
loadAnimation(String file, int modelid)
setAnimation(int modelid, int animid)
renderActor(int modelid)
|
|*/

  // collision world
  private DiscreteDynamicsWorld dynamicsWorld;
  private ObjectArrayList<CollisionShape> collisionShapes = new ObjectArrayList<CollisionShape>();

  // camera, geometry and textures
  public PerspectiveCamera cam;

	public ObjectArrayList<Mesh> listMesh = new ObjectArrayList<Mesh>();
	public ObjectArrayList<Texture> listTexture = new ObjectArrayList<Texture>();
	public ObjectArrayList<MD5Node> listMD5Node = new ObjectArrayList<MD5Node>();

	public ObjectArrayList<SceneNode> listSceneNode = new ObjectArrayList<SceneNode>();


	public SceneNode getNode(int nodeid) {

				return(listSceneNode.get(nodeid));

	}

	public void renderNode(int nodeid) {

				GL11 gl = Gdx.graphics.getGL11();

				SceneNode node = listSceneNode.get(nodeid);

				gl.glPushMatrix();

				if(node.textureid != -1) {
					bindTexture(node.textureid);
				}

				gl.glMatrixMode(gl.GL_MODELVIEW);

				gl.glMultMatrixf(FloatBuffer.wrap(node.matrix));

				gl.glRotatef(node.rotation.x,1,0,0);
				gl.glRotatef(node.rotation.y,0,1,0);
				gl.glRotatef(node.rotation.z,0,0,1);

				if(node.md5nodeid != -1) {

					renderActor(node.md5nodeid);

				} else if(node.meshid != -1) {

					renderMesh(node.meshid);

				}

				gl.glPopMatrix();

	}

	public void applyImpulse(int nodeid, Vector3f v1, Vector3f v2) {

			SceneNode node = listSceneNode.get(nodeid);

    	CollisionObject obj = dynamicsWorld.getCollisionObjectArray().getQuick(node.shapeid);
      RigidBody body = RigidBody.upcast(obj);

			body.applyImpulse(v1,v2);

	}

	public void updateNode(int nodeid) {

		if(nodeid < listSceneNode.size()) {

			SceneNode node = listSceneNode.get(nodeid);

			if(node.shapeid != -1) {

	    	CollisionObject obj = dynamicsWorld.getCollisionObjectArray().getQuick(node.shapeid);
	      RigidBody body = RigidBody.upcast(obj);

				if (body != null && body.getMotionState() != null) {

					Transform trans = new Transform();
      	  body.getMotionState().getWorldTransform(trans);

					node.position = trans.origin;

					trans.getOpenGLMatrix(node.matrix);

				}

			}

		}

	}

	public int getTotalNodes() {

		return(listSceneNode.size());

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

				}
      }

		// check managed collision
		int totpairs = dynamicsWorld.getPairCache().getNumOverlappingPairs();
//		System.out.println("Pairs: " + totpairs);
	}


	public int addSceneNode() {

		SceneNode tmpnode = new SceneNode();
		listSceneNode.add(tmpnode);

		return(listSceneNode.size()-1);

	}

	public void loadStillGeometry(int nodeid, String file) {

		//loads obj

		Mesh tmpmesh;
		tmpmesh = ObjLoader.loadObj(Gdx.files.internal(file).read(), true);
		listMesh.add(tmpmesh);

		listSceneNode.get(nodeid).meshid = listMesh.size()-1;

	}

	public void loadTexture(int nodeid, String file) {

		//loads images

		Texture tmptexture;
		tmptexture = new Texture(Gdx.files.internal(file));
		tmptexture.setWrap( TextureWrap.Repeat, TextureWrap.Repeat );
		listTexture.add(tmptexture);

		listSceneNode.get(nodeid).textureid = listTexture.size()-1;

	}

	public void loadAnimModel(int nodeid, String file) {

		//loads md5mesh

		listMD5Node.add(new MD5Node(file));

		listSceneNode.get(nodeid).md5nodeid = listMD5Node.size()-1;

	}

	public void loadAnimation(int nodeid, String file) {

		//loads md5anim

		listMD5Node.get(listSceneNode.get(nodeid).md5nodeid).loadAnimation(file);

	}

	public void setNodeAnimation(int nodeid, int animid) {

		setAnimation(getNode(nodeid).md5nodeid, animid);

	}

	public void setAnimation(int modelid, int animid) {

		listMD5Node.get(modelid).setAnimation(animid);

	}

	private void renderMesh(int meshid) {

		GL11 gl = Gdx.graphics.getGL11();
		listMesh.get(meshid).render(gl.GL_TRIANGLES);

	}

	private void bindTexture(int textureid) {

		listTexture.get(textureid).bind();

	}

	private void renderActor(int modelid) {

		listMD5Node.get(modelid).render();

	}


	public void addBoxShape(int nodeid, Vector3f len) {

		listSceneNode.get(nodeid).shapeid = createBoxShape(len, 1f, new Vector3f(0.3f,0.3f,0.3f));

	}


	public void addActorShape(int nodeid, Vector3f len) {

		listSceneNode.get(nodeid).shapeid = createBoxShape(len, 1f, new Vector3f(0f,0f,0f));

	}


	private int createBoxShape(Vector3f len, float mass, Vector3f inertia) {

		CollisionShape shape = new BoxShape(new Vector3f(len.x, len.y, len.z));
//		CollisionShape shape = new SphereShape(0.25f);
		collisionShapes.add(shape);
    Transform shapeTransform = new Transform();
		Vector3f localInertia = new Vector3f(inertia.x, inertia.y, inertia.z);
//		shape.calculateLocalInertia(mass, localInertia);
    shapeTransform.setIdentity();
    shapeTransform.origin.set(new Vector3f(0, 0, 0));
		DefaultMotionState myMotionState = new DefaultMotionState(shapeTransform);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(
          1f, myMotionState, shape, localInertia);
		RigidBody body = new RigidBody(rbInfo);
		body.setActivationState(body.DISABLE_DEACTIVATION);
		dynamicsWorld.addRigidBody(body);

		return(dynamicsWorld.getCollisionObjectArray().size()-1);

	}

	private int createBoxShape(Vector3f len) {

		return(createBoxShape(len, 1f, new Vector3f(0.3f, 0.3f, 0.3f)));

	}

	public void addMeshShape(int nodeid) {

		listSceneNode.get(nodeid).shapeid = createMeshShape(listSceneNode.get(nodeid).meshid);

	}

	private int createMeshShape(int modelid) {

		Mesh mesh = listMesh.get(modelid);

		TriangleIndexVertexArray jBulletMeshData = new TriangleIndexVertexArray();

    IndexedMesh jBulletIndexedMesh = new IndexedMesh();
    jBulletIndexedMesh.triangleIndexBase = ByteBuffer.allocate( mesh.getNumVertices() * 4 );
    jBulletIndexedMesh.vertexBase = ByteBuffer.allocate( mesh.getNumVertices() * 3 * 4 );

    FloatBuffer vertices = mesh.getVerticesBuffer();
    vertices.rewind();

    jBulletIndexedMesh.numVertices = mesh.getNumVertices();
    jBulletIndexedMesh.vertexStride = 12; //3 verts * 4 bytes per.

		float t1,t2,t3;
		int i;

    for ( i = 0; i < mesh.getNumVertices(); i++ ) {
      t1 = vertices.get();
      jBulletIndexedMesh.vertexBase.putFloat( t1 );
      t2 = vertices.get();
      jBulletIndexedMesh.vertexBase.putFloat( t2 );
      t3 = vertices.get();
      jBulletIndexedMesh.vertexBase.putFloat( t3 );

      t3 = vertices.get(); t3 = vertices.get(); t3 = vertices.get();

      t3 = vertices.get(); t3 = vertices.get();

    }

    jBulletIndexedMesh.numTriangles = mesh.getNumVertices() / 3;
    jBulletIndexedMesh.triangleIndexStride = 12; //3 index entries * 4 bytes each.
    for ( i = 0; i < mesh.getNumVertices(); i++ ) {

    	jBulletIndexedMesh.triangleIndexBase.putInt( i );

    }

    jBulletMeshData.addIndexedMesh( jBulletIndexedMesh );
		BvhTriangleMeshShape shape;

		shape = new BvhTriangleMeshShape(jBulletMeshData,true);
		shape.recalcLocalAabb();

		collisionShapes.add(shape);

    Transform shapeTransform = new Transform();
		Vector3f localInertia = new Vector3f(0, 0, 0);
    shapeTransform.setIdentity();
    shapeTransform.origin.set(new Vector3f(0, 0, 0));
		DefaultMotionState myMotionState = new DefaultMotionState(shapeTransform);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(
          0f, myMotionState, shape, localInertia);
		RigidBody body = new RigidBody(rbInfo);
		dynamicsWorld.addRigidBody(body);

		return(dynamicsWorld.getCollisionObjectArray().size()-1);

	}

	public void setModelRot(int nodeid, int axis, float value) {

		SceneNode node = listSceneNode.get(nodeid);

if(axis == 1) {
		node.rotation.x = value;
} else if(axis == 2) {
		node.rotation.y = value;
} else if(axis == 3) {
		node.rotation.z = value;
}

	}

	public void setModelPos(int nodeid, Vector3f pos) {

		SceneNode node = listSceneNode.get(nodeid);

    CollisionObject obj = dynamicsWorld.getCollisionObjectArray().getQuick(node.shapeid);
    RigidBody body = RigidBody.upcast(obj);	
		body.translate(new Vector3f(pos.x, pos.y, pos.z));

	}

	public void initPhysics() {
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


	}

}

