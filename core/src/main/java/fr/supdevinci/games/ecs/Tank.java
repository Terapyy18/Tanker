package fr.supdevinci.games.ecs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import fr.supdevinci.games.GameConfig;

public class Tank extends Entity {
    private float health;
    private float maxHealth;
    private float speed;
    private float damage;
    private float fireRate;
    private float armor;
    private float fireCooldown;
    private float angle;

    public Tank(Body body) {
        super(body, GameConfig.PLAYER_WIDTH, GameConfig.PLAYER_HEIGHT);
        this.maxHealth = GameConfig.PLAYER_MAX_HEALTH;
        this.health = maxHealth;
        this.speed = GameConfig.PLAYER_SPEED;
        this.damage = GameConfig.PLAYER_DAMAGE;
        this.fireRate = GameConfig.PLAYER_FIRE_RATE;
        this.armor = 0f;
        this.fireCooldown = 0f;
        this.angle = 0f;
    }

    @Override
    public void update(float delta) {
        if (!alive) return;
        fireCooldown = Math.max(0, fireCooldown - delta);
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

    public boolean canFire() {
        return alive && fireCooldown <= 0;
    }

    public void resetFireCooldown() {
        fireCooldown = fireRate;
    }

    public void takeDamage(float amount) {
        float effectiveDamage = amount * (1f - armor);
        health -= effectiveDamage;
        if (health <= 0) {
            health = 0;
            alive = false;
        }
    }

    public void applyLevelUp(int level) {
        maxHealth *= (1f + GameConfig.LEVEL_HEALTH_BONUS);
        health = maxHealth;
        speed *= (1f + GameConfig.LEVEL_SPEED_BONUS);
        damage *= (1f + GameConfig.LEVEL_DAMAGE_BONUS);
        fireRate *= (1f - GameConfig.LEVEL_FIRE_RATE_BONUS);
        armor = Math.min(0.75f, armor + GameConfig.LEVEL_ARMOR_BONUS);
    }

    @Override
    public void render(ShapeRenderer renderer) {
        if (!alive || body == null) return;
        Vector2 pos = body.getPosition();

        // Tank body
        renderer.setColor(0.2f, 0.7f, 0.3f, 1f);
        renderer.rect(pos.x - width / 2f, pos.y - height / 2f,
            width / 2f, height / 2f, width, height, 1f, 1f, angle);

        // Cannon
        renderer.setColor(0.1f, 0.5f, 0.2f, 1f);
        float cannonLength = width * 0.9f;
        float rad = angle * MathUtils.degreesToRadians;
        renderer.rectLine(pos.x, pos.y,
            pos.x + cannonLength * MathUtils.cos(rad),
            pos.y + cannonLength * MathUtils.sin(rad), 0.3f);
    }

    // Getters and setters
    public float getHealth() { return health; }
    public float getMaxHealth() { return maxHealth; }
    public float getSpeed() { return speed; }
    public float getDamage() { return damage; }
    public float getFireRate() { return fireRate; }
    public float getArmor() { return armor; }
    public float getAngle() { return angle; }
    public void setAngle(float angle) { this.angle = angle; }
}
