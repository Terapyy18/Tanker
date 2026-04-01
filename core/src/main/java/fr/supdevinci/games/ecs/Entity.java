package fr.supdevinci.games.ecs;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class Entity {
    protected Body body;
    protected boolean alive = true;
    protected float width;
    protected float height;

    public Entity(Body body, float width, float height) {
        this.body = body;
        this.width = width;
        this.height = height;
        if (body != null) {
            body.setUserData(this);
        }
    }

    public abstract void update(float delta);

    public abstract void render(ShapeRenderer renderer);

    public Body getBody() { return body; }
    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public Vector2 getPosition() {
        if (body != null) return body.getPosition();
        return Vector2.Zero;
    }
}
