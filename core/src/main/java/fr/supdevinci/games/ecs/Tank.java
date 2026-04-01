package fr.supdevinci.games.ecs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import fr.supdevinci.games.GameConfig;
import fr.supdevinci.games.physics.PhysicsConstants;

public class Tank extends Entity {
    private float health;
    private float maxHealth;
    private float speed;
    private float damage;
    private float fireRate;
    private float armor;
    private float fireCooldown;
    private float angle;
    private float heavyFireCooldown;
    private float heavyFireRate;
    private boolean heavyUnlocked;
    private int customLevel = 1;
    private boolean burstFireUnlocked;
    private float burstActiveTimer;
    private boolean burstCooling;

    private Texture[] textures;

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
        this.heavyFireCooldown = 0f;
        this.heavyFireRate = GameConfig.HEAVY_FIRE_RATE;
        this.heavyUnlocked = false;
        this.burstFireUnlocked = false;
        this.burstActiveTimer = 0f;
        this.burstCooling = false;
        
        this.textures = new Texture[3];
        if (Gdx.app != null) {
            this.textures[0] = new Texture(Gdx.files.internal("tank_1.png"));
            this.textures[1] = new Texture(Gdx.files.internal("Tank2.png"));
            this.textures[2] = new Texture(Gdx.files.internal("Tank3.png"));
        }
    }

    @Override
    public void update(float delta) {
        if (!alive) return;
        fireCooldown = Math.max(0, fireCooldown - delta);
        if (heavyUnlocked) {
            heavyFireCooldown = Math.max(0, heavyFireCooldown - delta);
        }
        if (burstFireUnlocked) {
            if (burstCooling) {
                burstActiveTimer -= delta * 0.5f; // takes 2s to recharge 1s of burst
                if (burstActiveTimer <= 0) {
                    burstActiveTimer = 0;
                    burstCooling = false;
                }
            } else if (fireCooldown <= 0) {
                burstActiveTimer = Math.max(0, burstActiveTimer - delta);
            }
        }
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
        if (burstFireUnlocked && burstCooling) return false;
        return alive && fireCooldown <= 0;
    }

    public void resetFireCooldown() {
        fireCooldown = fireRate;
        if (burstFireUnlocked) {
            burstActiveTimer += fireRate;
            if (burstActiveTimer >= 1.0f) {
                burstCooling = true;
            }
        }
    }

    public boolean canFireHeavy() {
        return alive && heavyUnlocked && heavyFireCooldown <= 0;
    }

    public void resetHeavyFireCooldown() {
        heavyFireCooldown = heavyFireRate;
    }

    public void takeDamage(float amount) {
        float effectiveDamage = amount * (1f - armor);
        health -= effectiveDamage;
        if (health <= 0) {
            health = 0;
            alive = false;
        }
    }

    public void applyDifficulty(fr.supdevinci.games.Difficulty difficulty) {
        if (difficulty == fr.supdevinci.games.Difficulty.HARD) {
            fireRate *= 1.5f; // slower reload
            resetFireCooldown();
        }
    }

    public void enableBurstFire() {
        burstFireUnlocked = true;
        fireRate = 0.08f; // fast fire
        resetFireCooldown();
    }

    public void addExtraHealth(float extra) {
        maxHealth += extra;
        health += extra; // Heal up
    }

    public boolean isBurstFireUnlocked() {
        return burstFireUnlocked;
    }

    public float getBurstActiveTimer() { return burstActiveTimer; }
    public boolean isBurstCooling() { return burstCooling; }

    public void applyLevelUp(int level) {
        customLevel = level;
        maxHealth *= (1f + GameConfig.LEVEL_HEALTH_BONUS);
        health = maxHealth;
        speed *= (1f + GameConfig.LEVEL_SPEED_BONUS);
        damage *= (1f + GameConfig.LEVEL_DAMAGE_BONUS);
        fireRate *= (1f - GameConfig.LEVEL_FIRE_RATE_BONUS);
        armor = Math.min(0.75f, armor + GameConfig.LEVEL_ARMOR_BONUS);
        
        if (level >= 3) {
            heavyUnlocked = true;
        }
        if (level == 5) {
            heavyFireRate *= 0.8f; // 20% faster charge at level 5
        }

        // Adjust physical hitbox size based on level (Level 1 is smaller sprite, Level 2/3 fill more of the frame)
        float newSize = GameConfig.PLAYER_WIDTH;
        if (level >= 2) newSize = 4.0f; // Increase size for Tank2 and Tank3 assets
        
        if (newSize != width && body != null) {
            width = newSize;
            height = newSize; // Keeping it square for simplicity as assets are square-ish
            
            // Recreate fixture to match new size
            if (body.getFixtureList().size > 0) {
                body.destroyFixture(body.getFixtureList().first());
            }
            
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(width / 2f, height / 2f);
            
            com.badlogic.gdx.physics.box2d.FixtureDef fd = new com.badlogic.gdx.physics.box2d.FixtureDef();
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

    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        if (!alive || body == null) return;
        Vector2 pos = body.getPosition();

        // Draw Tank Sprite - use larger visual size for better readability
        int levelIndex = Math.min(customLevel - 1, 2);
        Texture currentTex = textures[levelIndex];

        // Canon in PNG points UP (+Y) so we offset angle by -90° to align with our angle system (0° = right)
        float drawAngle = angle - 90f;

        batch.draw(currentTex,
            pos.x - width / 2f, pos.y - height / 2f,  // position
            width / 2f, height / 2f,                    // origin (center)
            width, height,                               // size matches hitbox
            1f, 1f,                                      // scale
            drawAngle,                                   // corrected rotation
            0, 0, currentTex.getWidth(), currentTex.getHeight(), false, false);
    }

    @Override
    public void render(ShapeRenderer renderer) {
        if (!alive || body == null) return;
        Vector2 pos = body.getPosition();
            
        // Heavy Fire Charge Bar
        if (heavyUnlocked) {
            float barW = width;
            float barH = 0.2f;
            float barY = pos.y - height / 2f - 0.5f;
            
            renderer.setColor(0.3f, 0.1f, 0.1f, 1f);
            renderer.rect(pos.x - barW / 2f, barY, barW, barH);
            
            float pct = 1f;
            if (heavyFireCooldown > 0) {
                pct = 1f - (heavyFireCooldown / heavyFireRate);
            }
            renderer.setColor(1.0f, 0.5f, 0.0f, 1f); // Orange loading
            renderer.rect(pos.x - barW / 2f, barY, barW * pct, barH);
        }

        // Burst Fire Heat Bar
        if (burstFireUnlocked) {
            float barW = width;
            float barH = 0.2f;
            float barY = pos.y - height / 2f - (heavyUnlocked ? 0.8f : 0.5f);
            
            renderer.setColor(0.1f, 0.1f, 0.3f, 1f);
            renderer.rect(pos.x - barW / 2f, barY, barW, barH);
            
            float pct = Math.min(1f, burstActiveTimer / 1.0f);
            if (burstCooling) {
                renderer.setColor(0.0f, 0.5f, 1.0f, 1f); // Blue cooling
            } else {
                renderer.setColor(0.0f, 1.0f, 1.0f, 1f); // Cyan tracking
            }
            renderer.rect(pos.x - barW / 2f, barY, barW * pct, barH);
        }
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
    
    public void dispose() {
        for (Texture tex : textures) {
            if (tex != null) tex.dispose();
        }
    }
}
