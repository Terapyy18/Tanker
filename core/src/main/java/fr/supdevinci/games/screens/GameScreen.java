package fr.supdevinci.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import fr.supdevinci.games.Difficulty;
import fr.supdevinci.games.GameConfig;
import fr.supdevinci.games.Main;
import fr.supdevinci.games.ecs.*;
import fr.supdevinci.games.physics.BodyFactory;
import fr.supdevinci.games.physics.PhysicsWorld;
import fr.supdevinci.games.systems.*;
import fr.supdevinci.games.ui.HudRenderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameScreen implements Screen {
    private final Main game;
    private final Difficulty difficulty;

    private PhysicsWorld physicsWorld;
    private BodyFactory bodyFactory;
    private Tank tank;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<ExpOrb> expOrbs = new ArrayList<>();

    private WaveManager waveManager;
    private LevelSystem levelSystem;
    private CollisionHandler collisionHandler;
    private CameraSystem cameraSystem;
    private HudRenderer hudRenderer;

    private boolean upgrading = false;
    private float victoryTimer = 0f;

    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private ShapeRenderer shapeRenderer;
    private com.badlogic.gdx.graphics.g2d.SpriteBatch batch;

    public GameScreen(Main game, Difficulty difficulty) {
        this.game = game;
        this.difficulty = difficulty;
    }

    @Override
    public void show() {
        // Physics
        physicsWorld = new PhysicsWorld();
        bodyFactory = new BodyFactory();
        bodyFactory.createWalls(physicsWorld.getWorld());

        // Camera
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(GameConfig.VIEWPORT_WIDTH, GameConfig.VIEWPORT_HEIGHT, camera);
        camera.position.set(GameConfig.MAP_WIDTH / 2f, GameConfig.MAP_HEIGHT / 2f, 0);
        camera.update();
        cameraSystem = new CameraSystem(camera);

        // Player
        Body tankBody = bodyFactory.createTankBody(physicsWorld.getWorld(),
            GameConfig.MAP_WIDTH / 2f, GameConfig.MAP_HEIGHT / 2f);
        tank = new Tank(tankBody);
        tank.applyDifficulty(difficulty);

        // Systems
        levelSystem = new LevelSystem(new LevelSystem.LevelUpListener() {
            @Override
            public void onLevelUp(int newLevel) {
                tank.applyLevelUp(newLevel);
                if (newLevel == 5) {
                    upgrading = true;
                }
            }
        });
        waveManager = new WaveManager(difficulty);
        collisionHandler = new CollisionHandler(levelSystem);
        physicsWorld.getWorld().setContactListener(collisionHandler);

        // Rendering
        shapeRenderer = new ShapeRenderer();
        batch = new com.badlogic.gdx.graphics.g2d.SpriteBatch();
        hudRenderer = new HudRenderer(tank, levelSystem, waveManager);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (upgrading) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            renderMap();
            for (ExpOrb orb : expOrbs) orb.render(shapeRenderer);
            for (Enemy enemy : enemies) enemy.render(shapeRenderer);
            for (Bullet bullet : bullets) bullet.render(shapeRenderer);
            shapeRenderer.end();
            
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            tank.render(shapeRenderer, batch); // Draw tank sprite
            batch.end();
            
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            tank.render(shapeRenderer); // Draw bars
            shapeRenderer.end();

            hudRenderer.renderUpgradeMenu();

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                float mx = Gdx.input.getX();
                float my = Gdx.graphics.getHeight() - Gdx.input.getY();
                
                float w = Gdx.graphics.getWidth();
                float h = Gdx.graphics.getHeight();
                
                Rectangle btnBurst = new Rectangle(w / 2f - 250, h / 2f - 50, 200, 100);
                Rectangle btnHp = new Rectangle(w / 2f + 50, h / 2f - 50, 200, 100);
                
                if (btnBurst.contains(mx, my)) {
                    tank.enableBurstFire();
                    upgrading = false;
                } else if (btnHp.contains(mx, my)) {
                    tank.addExtraHealth(200f);
                    upgrading = false;
                }
            }
            return;
        }

        delta = Math.min(delta, 0.05f);

        // --- Update ---
        // Tank angle toward mouse
        Vector3 mouseWorld = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        Vector2 tankPos = tank.getPosition();
        float angle = MathUtils.atan2(mouseWorld.y - tankPos.y, mouseWorld.x - tankPos.x) * MathUtils.radiansToDegrees;
        tank.setAngle(angle);

        // Fire on click
        boolean firing = tank.isBurstFireUnlocked() ? 
                         Gdx.input.isButtonPressed(Input.Buttons.LEFT) : 
                         Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);

        if (firing && tank.canFire()) {
            fireBullet();
        }
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT) && tank.canFireHeavy()) {
            fireHeavyBullet();
        }

        tank.update(delta);
        for (Enemy enemy : enemies) {
            enemy.update(delta, tank.getPosition());
        }
        for (Bullet bullet : bullets) {
            bullet.update(delta);
        }
        for (ExpOrb orb : expOrbs) {
            orb.update(delta, tank.getPosition());
        }

        // Enemy shooting
        for (Enemy enemy : enemies) {
            if (enemy.wantsToShoot() && enemy.isAlive()) {
                enemy.clearShootFlag();
                fireEnemyBullet(enemy);
            }
        }

        // Physics step
        physicsWorld.step(delta);

        // Remove dead entities
        removeDeadEntities();

        // Waves
        waveManager.update(delta, tank.getPosition(), enemies.size());
        List<WaveManager.SpawnRequest> spawns = waveManager.getPendingSpawns();
        for (WaveManager.SpawnRequest req : spawns) {
            spawnEnemy(req.type, req.x, req.y);
        }

        // Camera
        cameraSystem.update(delta, tank.getPosition());

        // Check game over / victory
        if (!tank.isAlive()) {
            game.setScreen(new GameOverScreen(game, false, waveManager.getCurrentWave(), levelSystem.getLevel()));
            return;
        }
        if (waveManager.isGameComplete() && waveManager.isVictory()) {
            victoryTimer += delta;
            if (victoryTimer > 2.0f) { // 2 seconds delay
                game.setScreen(new GameOverScreen(game, true, waveManager.getCurrentWave(), levelSystem.getLevel()));
                return;
            }
        }

        // --- Render ---
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(camera.combined);

        // Map ground + grid
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderMap();

        // Entities
        for (ExpOrb orb : expOrbs) orb.render(shapeRenderer);
        for (Enemy enemy : enemies) enemy.render(shapeRenderer);
        for (Bullet bullet : bullets) bullet.render(shapeRenderer);
        shapeRenderer.end();

        // Draw Tank Sprite
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        tank.render(shapeRenderer, batch);
        batch.end();

        // Draw Tank Bars
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        tank.render(shapeRenderer);
        shapeRenderer.end();

        // Map border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1f);
        shapeRenderer.rect(0, 0, GameConfig.MAP_WIDTH, GameConfig.MAP_HEIGHT);
        shapeRenderer.end();

        // HUD
        hudRenderer.render();
    }

    private void renderMap() {
        // Ground
        shapeRenderer.setColor(0.12f, 0.14f, 0.12f, 1f);
        shapeRenderer.rect(0, 0, GameConfig.MAP_WIDTH, GameConfig.MAP_HEIGHT);

        // Grid
        shapeRenderer.setColor(0.16f, 0.18f, 0.16f, 1f);
        for (float x = 0; x <= GameConfig.MAP_WIDTH; x += 10) {
            shapeRenderer.rectLine(x, 0, x, GameConfig.MAP_HEIGHT, 0.05f);
        }
        for (float y = 0; y <= GameConfig.MAP_HEIGHT; y += 10) {
            shapeRenderer.rectLine(0, y, GameConfig.MAP_WIDTH, y, 0.05f);
        }
    }

    private void fireBullet() {
        tank.resetFireCooldown();
        Vector2 pos = tank.getPosition();
        float rad = tank.getAngle() * MathUtils.degreesToRadians;
        float spawnDist = tank.getWidth() * 0.9f;
        float bx = pos.x + spawnDist * MathUtils.cos(rad);
        float by = pos.y + spawnDist * MathUtils.sin(rad);
        float vx = GameConfig.BULLET_SPEED * MathUtils.cos(rad);
        float vy = GameConfig.BULLET_SPEED * MathUtils.sin(rad);

        Body bulletBody = bodyFactory.createPlayerBulletBody(physicsWorld.getWorld(), bx, by, vx, vy);
        Bullet bullet = new Bullet(bulletBody, tank.getDamage(), true, false);
        bullets.add(bullet);
    }

    private void fireHeavyBullet() {
        tank.resetHeavyFireCooldown();
        Vector2 pos = tank.getPosition();
        float rad = tank.getAngle() * MathUtils.degreesToRadians;
        float spawnDist = tank.getWidth() * 0.9f;
        float bx = pos.x + spawnDist * MathUtils.cos(rad);
        float by = pos.y + spawnDist * MathUtils.sin(rad);
        float vx = GameConfig.HEAVY_BULLET_SPEED * MathUtils.cos(rad);
        float vy = GameConfig.HEAVY_BULLET_SPEED * MathUtils.sin(rad);

        Body bulletBody = bodyFactory.createPlayerBulletBody(physicsWorld.getWorld(), bx, by, vx, vy);
        Bullet bullet = new Bullet(bulletBody, tank.getDamage() * GameConfig.HEAVY_DAMAGE_MULT, true, true);
        bullets.add(bullet);
    }

    private void fireEnemyBullet(Enemy enemy) {
        Vector2 ePos = enemy.getPosition();
        Vector2 dir = new Vector2(tank.getPosition()).sub(ePos).nor();
        
        boolean isBoss = enemy.getType().name().startsWith("BOSS");
        float bulletSpeed = isBoss ? GameConfig.ENEMY_BULLET_SPEED * 1.5f : GameConfig.ENEMY_BULLET_SPEED;
        
        float bx = ePos.x + dir.x * enemy.getWidth();
        float by = ePos.y + dir.y * enemy.getHeight();
        float vx = dir.x * bulletSpeed;
        float vy = dir.y * bulletSpeed;

        Body bulletBody;
        if (isBoss) {
            bulletBody = bodyFactory.createEnemyHeavyBulletBody(physicsWorld.getWorld(), bx, by, vx, vy);
        } else {
            bulletBody = bodyFactory.createEnemyBulletBody(physicsWorld.getWorld(), bx, by, vx, vy);
        }
        
        // Pass isHeavy=isBoss to render it orange and bigger
        Bullet bullet = new Bullet(bulletBody, enemy.getType().getBulletDamage(), false, isBoss);
        bullets.add(bullet);
    }

    private void spawnEnemy(EnemyType type, float x, float y) {
        Body enemyBody = bodyFactory.createEnemyBody(physicsWorld.getWorld(), x, y, type);
        Enemy enemy = new Enemy(enemyBody, type);
        enemies.add(enemy);
    }

    private void removeDeadEntities() {
        // Enemies
        Iterator<Enemy> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            Enemy e = enemyIt.next();
            if (!e.isAlive()) {
                Vector2 pos = e.getBody().getPosition();
                spawnExpOrb(pos.x, pos.y, e.getType().getExpValue());
                physicsWorld.getWorld().destroyBody(e.getBody());
                enemyIt.remove();
            }
        }

        // Bullets
        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet b = bulletIt.next();
            if (!b.isAlive()) {
                physicsWorld.getWorld().destroyBody(b.getBody());
                bulletIt.remove();
            }
        }

        // Exp orbs
        Iterator<ExpOrb> orbIt = expOrbs.iterator();
        while (orbIt.hasNext()) {
            ExpOrb o = orbIt.next();
            if (!o.isAlive()) {
                physicsWorld.getWorld().destroyBody(o.getBody());
                orbIt.remove();
            }
        }
    }

    private void spawnExpOrb(float x, float y, int expValue) {
        Body orbBody = bodyFactory.createExpOrbBody(physicsWorld.getWorld(), x, y);
        ExpOrb orb = new ExpOrb(orbBody, expValue);
        expOrbs.add(orb);
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        viewport.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (batch != null) batch.dispose();
        if (hudRenderer != null) hudRenderer.dispose();
        if (physicsWorld != null) physicsWorld.dispose();
        if (tank != null) tank.dispose();
    }
}
