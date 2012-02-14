package com.dozingcatsoftware.bouncy.elements;

import static com.dozingcatsoftware.bouncy.util.MathUtils.asFloat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.World;
import com.dozingcatsoftware.bouncy.Field;
import com.dozingcatsoftware.bouncy.IFieldRenderer;

/** Elevator are a system to lift up ball at the top of the field.
 *  An elevator is made of on wall and paddle rotating arround this wall.
 *  
 *  We find the following parameters:
 *  
 *  Position: [start x, start y, end x, end y] so far position should be fully vertical (so same x)
 *  Number of Paddle, nb_paddle : n (Only 1 paddle is authorized at this time).
 *  speed: s (Speed of the motor)
 * 
 * @author valeuf */

public class ElevatorElement extends FieldElement {
	
	/** A paddle is a platform able to lift a ball upward.
	 * Paddle are connected to the middle of the elevator and move arround
	 * @author valeuf */
	public class Paddle{
		Body paddleBody;
		//Middle position(bad to use cryptic name)
		float mx, my;
		
		//Length of the paddle
		float length;
		
		//Position of the elevator (to determine the movement
		float top, down;
		
		//Pseudo-state machine
		boolean goingUp;
		boolean rotating;
		
		public Paddle(float mx, float my, float length, World world, boolean goingUp, float down, float top){
			this.mx = mx;
			this.my = my;
			

			PolygonShape wallshape = new PolygonShape();
			Vector2 v = new Vector2(0.5f, 0.0f);
			wallshape.setAsBox(0.5f, 0.05f, v, 0.0f);
			for(int i=0; i<wallshape.getVertexCount();i++){
				wallshape.getVertex(i, v);
				Gdx.app.log("V"+i+":", v.toString());
			}
			BodyDef bd = new BodyDef();
			bd.position.set(this.mx, this.my);
			this.paddleBody = world.createBody(bd);
			paddleBody.createFixture(wallshape, 0.5f);
			paddleBody.setType(BodyType.KinematicBody);
			if (!goingUp)
				paddleBody.setTransform(this.mx, this.my, 3.14f);
			

			this.goingUp = goingUp;
			this.rotating = false;
			if (goingUp)
				paddleBody.setLinearVelocity(0.0f, 1.2f);
			else
				paddleBody.setLinearVelocity(0.0f, -1.2f);
			
			this.top = top;
			this.down = down;
			this.length = length;
		}
		
		public void tick(){
			//If it goes up and does not rotate yet and should rotate
			if(this.goingUp && !(rotating) && paddleBody.getPosition().y >= this.top){
				this.rotating=true;
				paddleBody.setAngularVelocity((float) -3.14/2);
				paddleBody.setLinearVelocity(0.0f, 0.0f);
			}
			else if(this.goingUp && rotating && paddleBody.getAngle()<=-3.14/2){
				paddleBody.setAngularVelocity((float) 3.14/2);
			}
			//If it was going up and rotated 180 degree, it should go down
			else if(this.goingUp && rotating && paddleBody.getAngle()>=3.14){
				this.rotating=false;
				this.goingUp=false;
				paddleBody.setAngularVelocity(0.0f);
				paddleBody.setLinearVelocity(0.0f, -1.2f);
			}
			//If it goes down and does not rotate yet and should rotate
			else if(!this.goingUp && !(rotating) && paddleBody.getPosition().y <= this.down){
				this.rotating=true;
				paddleBody.setAngularVelocity((float) 3.14/2);
				paddleBody.setLinearVelocity(0.0f, 0.0f);
			}
			
			//If it was going down and rotated 180 degree, it should go up
			else if(!this.goingUp && rotating && paddleBody.getAngle()>=2*3.14){
				this.rotating=false;
				this.goingUp=true;
				paddleBody.setAngularVelocity(0.0f);
				paddleBody.setTransform(paddleBody.getPosition().x, paddleBody.getPosition().y, 0.0f);
				paddleBody.setLinearVelocity(0.0f, 1.2f);
			}
		}
		
		public void draw(IFieldRenderer renderer){
			// draw single line segment from anchor point
			Vector2 position = paddleBody.getPosition();
			float angle = paddleBody.getAngle();
			float x1 = position.x;
			float y1 = position.y;
			float x2 = (position.x + length * (float)Math.cos(angle));
			float y2 = position.y + length * (float)Math.sin(angle);
			renderer.drawLine(x1, y1, x2, y2, redColorComponent(DEFAULT_WALL_RED), greenColorComponent(DEFAULT_WALL_GREEN),
					blueColorComponent(DEFAULT_WALL_BLUE));
		}
		
	}

	Body structureBody;
	
	List bodyList = new ArrayList();
	List<Paddle> paddleList = new ArrayList();
	
	float x1, y1, x2, y2;
	float nb_paddle;
	float speed;
	
	@Override
	public void finishCreate(Map params, World world) {
		List pos = (List)params.get("position");
		this.x1 = asFloat(pos.get(0));
		this.y1 = asFloat(pos.get(1));
		this.x2 = asFloat(pos.get(2));
		this.y2 = asFloat(pos.get(3));
		float nb_paddle = asFloat(params.get("nb_paddle"));
		float speed = asFloat(params.get("speed"));
		
		//Create the structure
		assert(x1==x2);
		structureBody = Box2DFactory.createThinWall(world, x1, y1, x2, y2, 0.5f);
		bodyList.add(structureBody);
		
		//Dispatch paddle every 5
		//First find the number of paddle
		int nbPaddle= (int) Math.floor((y2-y1)/5.0);
		for(int i=1;i<nbPaddle;i++){
			paddleList.add(new Paddle(x1, y1+i*5, 1.0f, world, true, y1, y2));
			paddleList.add(new Paddle(x1, y1+i*5, 1.0f, world, false, y1, y2));
		}
		paddleList.add(new Paddle(x1, y1, 1.0f, world, true, y1, y2));
		paddleList.add(new Paddle(x1, y1+nbPaddle*5, 1.0f, world, false, y1, y2));
	}

	@Override
	public Collection<Body> getBodies() {
		return bodyList;
	}
	
	@Override
	public boolean shouldCallTick () {
		// At this time we will update the position of the elevator at each frame
		return true;
	}

	@Override
	public void tick (Field field) {
		//We will tick each paddle
		for (Paddle p : paddleList){
			p.tick();
		}
	}

	@Override
	public void draw(IFieldRenderer renderer) {
		renderer.drawLine(x1, y1, x2, y2, redColorComponent(DEFAULT_WALL_RED), greenColorComponent(DEFAULT_WALL_GREEN),
				blueColorComponent(DEFAULT_WALL_BLUE));
		for (Paddle p : paddleList){
			p.draw(renderer);
		}
	}

}
