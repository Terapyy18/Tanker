package fr.supdevinci.games.ecs;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import fr.supdevinci.games.GameConfig;

public class Bullet extends Entity {
    private final float damage;
    private final boolean playerBullet;
    private float lifetime;

    public Bullet(Body body, float damage, boolean playerBullet) {
        super(body, GameConfig.BULLET_RADIUS * 2, GameConfig.BULLET_RADIUS * 2);
        this.damage = damage;
        this.playerBullet = playerBullet;
        this.lifetime = GameConfig.BULLET_LIFETIME;
    }

    @Override
    public void update(float delta) {
        if (!alive) return;
        lifetime -= delta;
        if (lifetime <= 0) {
            alive = false;
        }
    }

    @Override
    public void render(ShapeRenderer renderer) {
        if (!alive || body == null) return;
        Vector2 pos = body.getPosition();

        if (playerBullet) {
            renderer.setColor(0.9f, 0.95f, 1.0f, 1f);
        } else {
            renderer.setColor(1.0f, 0.3f, 0.2f, 1f);
        }
        renderer.circle(pos.x, pos.y, GameConfig.BULLET_RADIUS, 8);
    }

    public float getDamage() { return damage; }
    public boolean isPlayerBullet() { return playerBullet; }
    public float getLifetime() { return lifetime; }
}
