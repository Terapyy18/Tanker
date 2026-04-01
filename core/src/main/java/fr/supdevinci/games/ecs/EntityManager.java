package fr.supdevinci.games.ecs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EntityManager {
    private final World world;
    private Tank tank;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<ExpOrb> expOrbs = new ArrayList<>();

    public EntityManager(World world) {
        this.world = world;
    }

    public void setTank(Tank tank) {
        this.tank = tank;
    }

    public Tank getTank() {
        return tank;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public List<ExpOrb> getExpOrbs() {
        return expOrbs;
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public void addExpOrb(ExpOrb orb) {
        expOrbs.add(orb);
    }

    public void update(float delta) {
        if (tank != null) tank.update(delta);

        Vector2 tankPos = tank != null ? tank.getPosition() : Vector2.Zero;

        for (Enemy enemy : enemies) {
            enemy.update(delta, tankPos);
        }
        for (Bullet bullet : bullets) {
            bullet.update(delta);
        }
        for (ExpOrb orb : expOrbs) {
            orb.update(delta, tankPos);
        }

        removeDeadEntities();
    }

    private void removeDeadEntities() {
        // Ennemis
        Iterator<Enemy> enemyIt = enemies.iterator();
        while (enemyIt.hasNext()) {
            Enemy e = enemyIt.next();
            if (!e.isAlive()) {
                if (onEnemyKilledListener != null) {
                    onEnemyKilledListener.onEnemyKilled(e);
                }
                world.destroyBody(e.getBody());
                enemyIt.remove();
            }
        }

        // Balles
        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet b = bulletIt.next();
            if (!b.isAlive()) {
                world.destroyBody(b.getBody());
                bulletIt.remove();
            }
        }

        // Orbes d'XP
        Iterator<ExpOrb> orbIt = expOrbs.iterator();
        while (orbIt.hasNext()) {
            ExpOrb o = orbIt.next();
            if (!o.isAlive()) {
                world.destroyBody(o.getBody());
                orbIt.remove();
            }
        }
    }

    public void renderShapes(ShapeRenderer renderer) {
        for (ExpOrb orb : expOrbs) orb.render(renderer);
        for (Enemy enemy : enemies) enemy.render(renderer);
        for (Bullet bullet : bullets) bullet.render(renderer);
        if (tank != null) tank.render(renderer);
    }

    public void renderSprites(SpriteBatch batch, ShapeRenderer renderer) {
        if (tank != null) tank.render(renderer, batch);
    }

    public void dispose() {
        if (tank != null) tank.dispose();
        // Les corps Box2D sont détruits par le monde, mais nous pourrions vouloir vider les listes
        enemies.clear();
        bullets.clear();
        expOrbs.clear();
    }

    // Écouteur pour la mort d'un ennemi (pour faire apparaître des orbes d'exp ou autre)
    public interface OnEnemyKilledListener {
        void onEnemyKilled(Enemy enemy);
    }

    private OnEnemyKilledListener onEnemyKilledListener;

    public void setOnEnemyKilledListener(OnEnemyKilledListener listener) {
        this.onEnemyKilledListener = listener;
    }
}
