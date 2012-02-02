package com.dozingcatsoftware.bouncy.elements;

import static com.dozingcatsoftware.bouncy.util.MathUtils.asFloat;

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

	Body structureBody;
	Body paddleBody;
	Collection bodySet;
	
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
		structureBody = Box2DFactory.createThinWall(world, x1, y1, x2, y2, 0.5f);
		bodySet = Collections.singleton(structureBody);
	}

	@Override
	public Collection<Body> getBodies() {
		return bodySet;
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
	}

}
