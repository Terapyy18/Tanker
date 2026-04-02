package fr.supdevinci.games.ecs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import fr.supdevinci.games.GameConfig;

public class Tank extends Entity implements Damageable {
    private float health;
    private float maxHealth;
    private float armor;
    private float angle;
    private int customLevel = 1;

    private final TankPhysics physics;
    private final TankCombat combat;
    private final TankRenderer renderer;
    private final Texture[] textures;

    public Tank(Body body) {
        super(body, GameConfig.PLAYER_WIDTH, GameConfig.PLAYER_HEIGHT);
        this.maxHealth = GameConfig.PLAYER_MAX_HEALTH;
        this.health = maxHealth;
        this.armor = 0f;
        this.angle = 0f;

        this.physics = new TankPhysics(body, GameConfig.PLAYER_SPEED, width, height);
        this.combat = new TankCombat();
        
        this.textures = new Texture[3];
        if (Gdx.app != null) {
            this.textures[0] = new Texture(Gdx.files.internal("tank_1.png"));
            this.textures[1] = new Texture(Gdx.files.internal("Tank2.png"));
            this.textures[2] = new Texture(Gdx.files.internal("Tank3.png"));
        }
        this.renderer = new TankRenderer(this.textures);
    }

    @Override
    public void update(float delta) {
        if (!alive) return;
        combat.update(delta, alive);
        physics.update(delta);
    }

    public boolean canFire() { return combat.canFire(alive); }
    public void resetFireCooldown() { combat.resetFireCooldown(); }
    public boolean canFireHeavy() { return combat.canFireHeavy(alive); }
    public void resetHeavyFireCooldown() { combat.resetHeavyFireCooldown(); }

    @Override
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
            combat.setFireRate(combat.getFireRate() * 1.5f);
            combat.resetFireCooldown();
        }
    }

    public void enableBurstFire() { combat.enableBurstFire(); }
    public void addExtraHealth(float extra) {
        maxHealth += extra;
        health += extra;
    }

    public void applyLevelUp(int level) {
        customLevel = level;
        maxHealth *= (1f + GameConfig.LEVEL_HEALTH_BONUS);
        health = maxHealth;
        
        physics.setSpeed(physics.getSpeed() * (1f + GameConfig.LEVEL_SPEED_BONUS));
        
        combat.setDamage(combat.getDamage() * (1f + GameConfig.LEVEL_DAMAGE_BONUS));
        combat.setFireRate(combat.getFireRate() * (1f - GameConfig.LEVEL_FIRE_RATE_BONUS));
        armor = Math.min(0.75f, armor + GameConfig.LEVEL_ARMOR_BONUS);

        if (level >= 3) combat.setHeavyUnlocked(true);
        if (level == 5) combat.setHeavyFireRate(combat.getHeavyFireRate() * 0.8f);

        float newSize = GameConfig.PLAYER_WIDTH;
        if (level >= 2) newSize = 4.0f;
        physics.updateHitbox(newSize);
        this.width = physics.getWidth();
        this.height = physics.getHeight();
    }

    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        if (!alive || body == null) return;
        this.renderer.render(batch, body.getPosition(), width, height, angle, customLevel);
    }

    @Override
    public void render(ShapeRenderer renderer) {
        if (!alive || body == null) return;
        this.renderer.renderBars(renderer, body.getPosition(), width, height, combat);
    }

    // Adaptations pour la compatibilité avec le code existant
    public float getHealth() { return health; }
    public float getMaxHealth() { return maxHealth; }
    public float getDamage() { return combat.getDamage(); }
    public float getAngle() { return angle; }
    public void setAngle(float angle) { this.angle = angle; }
    public boolean isBurstFireUnlocked() { return combat.isBurstFireUnlocked(); }
    public float getBurstActiveTimer() { return combat.getBurstActiveTimer(); }
    public boolean isBurstCooling() { return combat.isBurstCooling(); }
    public float getSpeed() { return physics.getSpeed(); }
    public float getFireRate() { return combat.getFireRate(); }
    public float getArmor() { return armor; }

    public void dispose() {
        for (Texture tex : textures) { if (tex != null) tex.dispose(); }
    }
}
