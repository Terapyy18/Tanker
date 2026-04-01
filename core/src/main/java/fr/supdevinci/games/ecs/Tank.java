package fr.supdevinci.games.ecs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
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
        if (!alive)
            return;
        fireCooldown = Math.max(0, fireCooldown - delta);
        if (heavyUnlocked) {
            heavyFireCooldown = Math.max(0, heavyFireCooldown - delta);
        }
        if (burstFireUnlocked) {
            if (burstCooling) {
                burstActiveTimer -= delta * 0.5f; // prend 2s pour recharger 1s de rafale
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
        if (body == null)
            return;
        float vx = 0, vy = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.Z))
            vy = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            vy = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.Q))
            vx = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            vx = 1;

        Vector2 velocity = new Vector2(vx, vy);
        if (velocity.len2() > 0)
            velocity.nor();
        velocity.scl(speed);
        body.setLinearVelocity(velocity);
    }

    public boolean canFire() {
        if (burstFireUnlocked && burstCooling)
            return false;
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
            fireRate *= 1.5f; // recharge plus lente
            resetFireCooldown();
        }
    }

    public void enableBurstFire() {
        burstFireUnlocked = true;
        fireRate = 0.08f; // tir rapide
        resetFireCooldown();
    }

    public void addExtraHealth(float extra) {
        maxHealth += extra;
        health += extra; // Soigner
    }

    public boolean isBurstFireUnlocked() {
        return burstFireUnlocked;
    }

    public float getBurstActiveTimer() {
        return burstActiveTimer;
    }

    public boolean isBurstCooling() {
        return burstCooling;
    }

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
            heavyFireRate *= 0.8f; // Charge 20% plus rapide au niveau 5
        }

        // Ajuster la taille de la hitbox physique selon le niveau (Niv 1 sprite plus
        // petit, Niv 2/3 remplissent plus le cadre)
        float newSize = GameConfig.PLAYER_WIDTH;
        if (level >= 2)
            newSize = 4.0f; // Augmenter la taille pour les assets Tank2 et Tank3

        if (newSize != width && body != null) {
            width = newSize;
            height = newSize; // Garder au carré pour la simplicité car les assets le sont

            // Recréer la fixture pour correspondre à la nouvelle taille
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
        if (!alive || body == null)
            return;
        Vector2 pos = body.getPosition();

        // Dessiner le Sprite du Tank - utiliser une taille visuelle plus grande pour
        // une meilleure lisibilité
        int levelIndex = Math.min(customLevel - 1, 2);
        Texture currentTex = textures[levelIndex];

        // Le canon dans le PNG pointe vers le HAUT (+Y) donc on décale l'angle de -90°
        // pour l'aligner avec notre système (0° = droite)
        float drawAngle = angle - 90f;

        batch.draw(currentTex,
                pos.x - width / 2f, pos.y - height / 2f, // position
                width / 2f, height / 2f, // origine (centre)
                width, height, // la taille correspond à la hitbox
                1f, 1f, // échelle
                drawAngle, // rotation corrigée
                0, 0, currentTex.getWidth(), currentTex.getHeight(), false, false);
    }

    @Override
    public void render(ShapeRenderer renderer) {
        if (!alive || body == null)
            return;
        Vector2 pos = body.getPosition();

        // Barre de charge du tir lourd
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
            renderer.setColor(1.0f, 0.5f, 0.0f, 1f); // Chargement orange
            renderer.rect(pos.x - barW / 2f, barY, barW * pct, barH);
        }

        // Barre de chauffe du tir en rafale
        if (burstFireUnlocked) {
            float barW = width;
            float barH = 0.2f;
            float barY = pos.y - height / 2f - (heavyUnlocked ? 0.8f : 0.5f);

            renderer.setColor(0.1f, 0.1f, 0.3f, 1f);
            renderer.rect(pos.x - barW / 2f, barY, barW, barH);

            float pct = Math.min(1f, burstActiveTimer / 1.0f);
            if (burstCooling) {
                renderer.setColor(0.0f, 0.5f, 1.0f, 1f); // Refroidissement bleu
            } else {
                renderer.setColor(0.0f, 1.0f, 1.0f, 1f); // Suivi cyan
            }
            renderer.rect(pos.x - barW / 2f, barY, barW * pct, barH);
        }
    }

    // Getters et setters
    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getSpeed() {
        return speed;
    }

    public float getDamage() {
        return damage;
    }

    public float getFireRate() {
        return fireRate;
    }

    public float getArmor() {
        return armor;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void dispose() {
        for (Texture tex : textures) {
            if (tex != null)
                tex.dispose();
        }
    }
}
