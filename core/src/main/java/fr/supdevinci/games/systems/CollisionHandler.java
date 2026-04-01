package fr.supdevinci.games.systems;

import com.badlogic.gdx.physics.box2d.*;
import fr.supdevinci.games.ecs.*;

public class CollisionHandler implements ContactListener {
    private final LevelSystem levelSystem;

    public CollisionHandler(LevelSystem levelSystem) {
        this.levelSystem = levelSystem;
    }

    @Override
    public void beginContact(Contact contact) {
        Object dataA = contact.getFixtureA().getBody().getUserData();
        Object dataB = contact.getFixtureB().getBody().getUserData();

        // Bullet hitting a wall (null userData)
        if (dataA instanceof Bullet && dataB == null) { ((Bullet) dataA).setAlive(false); return; }
        if (dataB instanceof Bullet && dataA == null) { ((Bullet) dataB).setAlive(false); return; }

        if (!(dataA instanceof Entity) || !(dataB instanceof Entity)) return;

        // Player bullet hits enemy
        if (dataA instanceof Bullet && dataB instanceof Enemy) { bulletHitEnemy((Bullet) dataA, (Enemy) dataB); return; }
        if (dataB instanceof Bullet && dataA instanceof Enemy) { bulletHitEnemy((Bullet) dataB, (Enemy) dataA); return; }

        // Enemy bullet hits player
        if (dataA instanceof Bullet && dataB instanceof Tank) { bulletHitTank((Bullet) dataA, (Tank) dataB); return; }
        if (dataB instanceof Bullet && dataA instanceof Tank) { bulletHitTank((Bullet) dataB, (Tank) dataA); return; }

        // Enemy contacts player
        if (dataA instanceof Enemy && dataB instanceof Tank) { enemyHitTank((Enemy) dataA, (Tank) dataB); return; }
        if (dataB instanceof Enemy && dataA instanceof Tank) { enemyHitTank((Enemy) dataB, (Tank) dataA); return; }

        // Player contacts exp orb
        if (dataA instanceof Tank && dataB instanceof ExpOrb) { collectOrb((ExpOrb) dataB); return; }
        if (dataB instanceof Tank && dataA instanceof ExpOrb) { collectOrb((ExpOrb) dataA); return; }
    }

    private void bulletHitEnemy(Bullet bullet, Enemy enemy) {
        if (!bullet.isPlayerBullet()) return;
        bullet.setAlive(false);
        enemy.takeDamage(bullet.getDamage());
    }

    private void bulletHitTank(Bullet bullet, Tank tank) {
        if (bullet.isPlayerBullet()) return;
        bullet.setAlive(false);
        tank.takeDamage(bullet.getDamage());
    }

    private void enemyHitTank(Enemy enemy, Tank tank) {
        tank.takeDamage(enemy.getType().getContactDamage());
    }

    private void collectOrb(ExpOrb orb) {
        orb.setAlive(false);
        levelSystem.addExp(orb.getExpValue());
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
