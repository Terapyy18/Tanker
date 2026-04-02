package fr.supdevinci.games.ecs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Enemy extends Entity implements Damageable {
    private final EnemyType type;
    private float health;
    private float shootTimer;
    private boolean wantsToShoot;
    private float angle;

    public Enemy(Body body, EnemyType type) {
        super(body, type.getWidth(), type.getHeight());
        this.type = type;
        this.health = type.getHealth();
        this.shootTimer = type.canShoot() ? type.getFireRate() : 0;
        this.wantsToShoot = false;
    }

    public void update(float delta, Vector2 targetPos) {
        if (!alive || body == null) return;

        // Se déplacer vers la cible
        Vector2 pos = body.getPosition();
        Vector2 dir = new Vector2(targetPos).sub(pos);
        if (dir.len2() > 0.1f) {
            angle = dir.angleDeg();
            dir.nor().scl(type.getSpeed());
            body.setLinearVelocity(dir);
        }

        // Logique de tir
        if (type.canShoot()) {
            shootTimer -= delta;
            if (shootTimer <= 0) {
                wantsToShoot = true;
                shootTimer = type.getFireRate();
            }
        }
    }

    @Override
    public void update(float delta) {
        // Utiliser update(delta, targetPos) à la place
    }

    public void takeDamage(float amount) {
        health -= amount;
        if (health <= 0) {
            health = 0;
            alive = false;
        }
    }

    @Override
    public void render(ShapeRenderer renderer) {
        if (!alive || body == null) return;
        Vector2 pos = body.getPosition();

        // Base du tank (pivotée)
        renderer.setColor(type.getColor());
        renderer.rect(pos.x - width / 2f, pos.y - height / 2f,
            width / 2f, height / 2f, width, height, 1f, 1f, angle);

        // Canon
        float rad = angle * MathUtils.degreesToRadians;
        float cannonLength = width * 0.9f;
        Color darker = new Color(type.getColor()).mul(0.6f);
        darker.a = 1f;
        renderer.setColor(darker);
        renderer.rectLine(pos.x, pos.y,
            pos.x + cannonLength * MathUtils.cos(rad),
            pos.y + cannonLength * MathUtils.sin(rad), 0.3f);

        // Le Boss reçoit un indicateur visuel supplémentaire
        if (type == EnemyType.BOSS) {
            renderer.setColor(1f, 0.1f, 0.1f, 1f);
            renderer.rect(pos.x - width / 2f + 0.3f, pos.y - height / 2f + 0.3f,
                width / 2f - 0.3f, height / 2f - 0.3f, width - 0.6f, height - 0.6f, 1f, 1f, angle);
        }
    }

    public EnemyType getType() { return type; }
    public float getHealth() { return health; }
    public boolean wantsToShoot() { return wantsToShoot; }
    public void clearShootFlag() { wantsToShoot = false; }
}
