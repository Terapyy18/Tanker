package fr.supdevinci.games.ecs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import fr.supdevinci.games.physics.PhysicsConstants;

public class TankPhysics {
    private final Body body;
    private float speed;
    private float width;
    private float height;

    public TankPhysics(Body body, float speed, float width, float height) {
        this.body = body;
        this.speed = speed;
        this.width = width;
        this.height = height;
    }

    public void update(float delta) {
        handleMovement();
    }

    private void handleMovement() {
        if (body == null) return;
        float vx = 0, vy = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.Z)) vy = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) vy = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.Q)) vx = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) vx = 1;

        Vector2 velocity = new Vector2(vx, vy);
        if (velocity.len2() > 0) velocity.nor();
        velocity.scl(speed);
        body.setLinearVelocity(velocity);
    }

    public void updateHitbox(float newSize) {
        if (newSize != width && body != null) {
            width = newSize;
            height = newSize;
            if (body.getFixtureList().size > 0) {
                body.destroyFixture(body.getFixtureList().first());
            }
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width / 2f, height / 2f);
            FixtureDef fd = new FixtureDef();
            fd.shape = shape;
            fd.density = 1f;
            fd.friction = 0.3f;
            fd.restitution = 0.1f;
            fd.filter.categoryBits = PhysicsConstants.CATEGORY_PLAYER;
            fd.filter.maskBits = PhysicsConstants.MASK_PLAYER;
            body.createFixture(fd);
            shape.dispose();
        }
    }

    public Body getBody() { return body; }
    public void setSpeed(float speed) { this.speed = speed; }
    public float getSpeed() { return speed; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}
