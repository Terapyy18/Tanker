package fr.supdevinci.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Body;
import fr.supdevinci.games.GameConfig;
import fr.supdevinci.games.GameWorld;
import fr.supdevinci.games.ecs.Bullet;
import fr.supdevinci.games.ecs.Tank;

public class GameInputHandler extends InputAdapter {
    private final GameWorld world;
    private final OrthographicCamera camera;

    public GameInputHandler(GameWorld world, OrthographicCamera camera) {
        this.world = world;
        this.camera = camera;
    }

    public void update(float delta) {
        if (world.isPaused() || world.isUpgrading()) return;

        Tank tank = world.getEntityManager().getTank();
        if (tank == null || !tank.isAlive()) return;

        // --- Rotation ---
        Vector3 mouseWorld = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Vector2 tankPos = tank.getPosition();
        float angle = MathUtils.atan2(mouseWorld.y - tankPos.y, mouseWorld.x - tankPos.x) * MathUtils.radiansToDegrees;
        tank.setAngle(angle);

        // --- Tir ---
        // Clic gauche (Normal ou Rafale)
        boolean firing = tank.isBurstFireUnlocked() ? Gdx.input.isButtonPressed(Input.Buttons.LEFT)
                : Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);

        if (firing && tank.canFire()) {
            fireBullet(tank);
        }

        // Clic droit (Lourd)
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT) && tank.canFireHeavy()) {
            fireHeavyBullet(tank);
        }

        // --- Alterner la pause ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            world.setPaused(!world.isPaused());
        }
    }

    private void fireBullet(Tank tank) {
        tank.resetFireCooldown();
        Vector2 pos = tank.getPosition();
        float rad = tank.getAngle() * MathUtils.degreesToRadians;
        float spawnDist = tank.getWidth() * 0.9f;
        float bx = pos.x + spawnDist * MathUtils.cos(rad);
        float by = pos.y + spawnDist * MathUtils.sin(rad);
        float vx = GameConfig.BULLET_SPEED * MathUtils.cos(rad);
        float vy = GameConfig.BULLET_SPEED * MathUtils.sin(rad);

        Body bulletBody = world.getBodyFactory().createPlayerBulletBody(world.getPhysicsWorld().getWorld(), bx, by, vx, vy);
        Bullet bullet = new Bullet(bulletBody, tank.getDamage(), true, false);
        world.getEntityManager().addBullet(bullet);
    }

    private void fireHeavyBullet(Tank tank) {
        tank.resetHeavyFireCooldown();
        Vector2 pos = tank.getPosition();
        float rad = tank.getAngle() * MathUtils.degreesToRadians;
        float spawnDist = tank.getWidth() * 0.9f;
        float bx = pos.x + spawnDist * MathUtils.cos(rad);
        float by = pos.y + spawnDist * MathUtils.sin(rad);
        float vx = GameConfig.HEAVY_BULLET_SPEED * MathUtils.cos(rad);
        float vy = GameConfig.HEAVY_BULLET_SPEED * MathUtils.sin(rad);

        Body bulletBody = world.getBodyFactory().createPlayerBulletBody(world.getPhysicsWorld().getWorld(), bx, by, vx, vy);
        Bullet bullet = new Bullet(bulletBody, tank.getDamage() * GameConfig.HEAVY_DAMAGE_MULT, true, true);
        world.getEntityManager().addBullet(bullet);
    }
}
