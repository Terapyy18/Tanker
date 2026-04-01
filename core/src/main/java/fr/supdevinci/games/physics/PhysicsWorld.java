package fr.supdevinci.games.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsWorld {
    private final World world;
    private float accumulator = 0f;

    public PhysicsWorld() {
        this.world = new World(new Vector2(0, 0), true);
    }

    public void step(float delta) {
        accumulator += Math.min(delta, 0.25f);
        while (accumulator >= PhysicsConstants.TIME_STEP) {
            world.step(PhysicsConstants.TIME_STEP, PhysicsConstants.VELOCITY_ITERATIONS, PhysicsConstants.POSITION_ITERATIONS);
            accumulator -= PhysicsConstants.TIME_STEP;
        }
    }

    public World getWorld() {
        return world;
    }

    public void dispose() {
        world.dispose();
    }
}
