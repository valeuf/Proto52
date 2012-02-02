package com.dozingcatsoftware.bouncy.elements;

import static com.dozingcatsoftware.bouncy.util.MathUtils.asFloat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
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
		
		//Position of the paddle (bad to use cryptic name)
		float px1, px2, py1, py2;
		
		public Paddle(float mx, float my, World world){
			this.mx = mx;
			this.my = my;
			
			this.px1 = mx;
			this.py1 = my;
			this.px2 = mx+1;
			this.py2 = my;
			
			paddleBody = Box2DFactory.createThinWall(world, px1, py1, px2, py2, 0.5f);
			
		}
		
		public void draw(IFieldRenderer renderer){
			renderer.drawLine(px1, py1, px2, py2, redColorComponent(DEFAULT_WALL_RED), greenColorComponent(DEFAULT_WALL_GREEN),
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
		
		paddleList.add(new Paddle(x1, (y1+y2)/2,world));
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
	public void draw(IFieldRenderer renderer) {
		renderer.drawLine(x1, y1, x2, y2, redColorComponent(DEFAULT_WALL_RED), greenColorComponent(DEFAULT_WALL_GREEN),
				blueColorComponent(DEFAULT_WALL_BLUE));
		for (Paddle p : paddleList){
			p.draw(renderer);
		}
	}

}
