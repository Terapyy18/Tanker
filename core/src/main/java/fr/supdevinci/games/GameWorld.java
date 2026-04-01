package fr.supdevinci.games;

import com.badlogic.gdx.math.Vector2;
import fr.supdevinci.games.ecs.*;
import fr.supdevinci.games.physics.BodyFactory;
import fr.supdevinci.games.physics.PhysicsWorld;
import fr.supdevinci.games.systems.CollisionHandler;
import fr.supdevinci.games.systems.LevelSystem;
import fr.supdevinci.games.systems.WaveManager;

import java.util.List;

public class GameWorld {
    private final PhysicsWorld physicsWorld;
    private final EntityManager entityManager;
    private final BodyFactory bodyFactory;
    private final WaveManager waveManager;
    private final LevelSystem levelSystem;
    private final CollisionHandler collisionHandler;

    private boolean paused = false;
    private boolean upgrading = false;
    private float victoryTimer = 0f;

    public GameWorld(Difficulty difficulty) {
        this.physicsWorld = new PhysicsWorld();
        this.bodyFactory = new BodyFactory();
        this.entityManager = new EntityManager(physicsWorld.getWorld());
        this.waveManager = new WaveManager(difficulty);
        this.levelSystem = new LevelSystem(new LevelSystem.LevelUpListener() {
            @Override
            public void onLevelUp(int newLevel) {
                if (entityManager.getTank() != null) {
                    entityManager.getTank().applyLevelUp(newLevel);
                }
                if (newLevel == 5) {
                    upgrading = true;
                }
            }
        });
        this.collisionHandler = new CollisionHandler(levelSystem);
        this.physicsWorld.getWorld().setContactListener(collisionHandler);

        // Écouteur de mort d'ennemi pour faire apparaître des orbes d'exp
        entityManager.setOnEnemyKilledListener(new EntityManager.OnEnemyKilledListener() {
            @Override
            public void onEnemyKilled(Enemy enemy) {
                Vector2 pos = enemy.getBody().getPosition();
                spawnExpOrb(pos.x, pos.y, enemy.getType().getExpValue());
            }
        });

        init();
    }

    private void init() {
        bodyFactory.createWalls(physicsWorld.getWorld());
        
        // Faire apparaître le Tank
        fr.supdevinci.games.ecs.Tank tank = new fr.supdevinci.games.ecs.Tank(
            bodyFactory.createTankBody(physicsWorld.getWorld(), 
            GameConfig.MAP_WIDTH / 2f, GameConfig.MAP_HEIGHT / 2f)
        );
        entityManager.setTank(tank);
    }

    public void update(float delta) {
        if (paused || upgrading) return;

        delta = Math.min(delta, 0.05f);

        // Mettre à jour les entités
        entityManager.update(delta);

        // Étape de physique
        physicsWorld.step(delta);

        // Vagues
        Tank tank = entityManager.getTank();
        waveManager.update(delta, tank.getPosition(), entityManager.getEnemies().size());
        List<WaveManager.SpawnRequest> spawns = waveManager.getPendingSpawns();
        for (WaveManager.SpawnRequest req : spawns) {
            spawnEnemy(req.type, req.x, req.y);
        }

        // Tir des ennemis
        for (Enemy enemy : entityManager.getEnemies()) {
            if (enemy.wantsToShoot() && enemy.isAlive()) {
                enemy.clearShootFlag();
                fireEnemyBullet(enemy);
            }
        }

        // Compteur de victoire
        if (waveManager.isGameComplete() && waveManager.isVictory()) {
            victoryTimer += delta;
        }
    }

    public void spawnEnemy(EnemyType type, float x, float y) {
        com.badlogic.gdx.physics.box2d.Body body = bodyFactory.createEnemyBody(physicsWorld.getWorld(), x, y, type);
        Enemy enemy = new Enemy(body, type);
        entityManager.addEnemy(enemy);
    }

    public void fireEnemyBullet(Enemy enemy) {
        Vector2 ePos = enemy.getPosition();
        Tank tank = entityManager.getTank();
        Vector2 dir = new Vector2(tank.getPosition()).sub(ePos).nor();

        boolean isBoss = enemy.getType().name().startsWith("BOSS");
        float bulletSpeed = isBoss ? GameConfig.ENEMY_BULLET_SPEED * 1.5f : GameConfig.ENEMY_BULLET_SPEED;

        float bx = ePos.x + dir.x * enemy.getWidth();
        float by = ePos.y + dir.y * enemy.getHeight();
        float vx = dir.x * bulletSpeed;
        float vy = dir.y * bulletSpeed;

        com.badlogic.gdx.physics.box2d.Body bulletBody;
        if (isBoss) {
            bulletBody = bodyFactory.createEnemyHeavyBulletBody(physicsWorld.getWorld(), bx, by, vx, vy);
        } else {
            bulletBody = bodyFactory.createEnemyBulletBody(physicsWorld.getWorld(), bx, by, vx, vy);
        }

        Bullet bullet = new Bullet(bulletBody, enemy.getType().getBulletDamage(), false, isBoss);
        entityManager.addBullet(bullet);
    }

    public void spawnExpOrb(float x, float y, int expValue) {
        com.badlogic.gdx.physics.box2d.Body orbBody = bodyFactory.createExpOrbBody(physicsWorld.getWorld(), x, y);
        ExpOrb orb = new ExpOrb(orbBody, expValue);
        entityManager.addExpOrb(orb);
    }

    public EntityManager getEntityManager() { return entityManager; }
    public WaveManager getWaveManager() { return waveManager; }
    public LevelSystem getLevelSystem() { return levelSystem; }
    public PhysicsWorld getPhysicsWorld() { return physicsWorld; }
    public BodyFactory getBodyFactory() { return bodyFactory; }

    public boolean isPaused() { return paused; }
    public void setPaused(boolean paused) { this.paused = paused; }
    public boolean isUpgrading() { return upgrading; }
    public void setUpgrading(boolean upgrading) { this.upgrading = upgrading; }
    public float getVictoryTimer() { return victoryTimer; }

    public void dispose() {
        entityManager.dispose();
        physicsWorld.dispose();
    }
}
