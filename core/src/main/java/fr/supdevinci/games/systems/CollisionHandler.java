package fr.supdevinci.games.systems;

import com.badlogic.gdx.physics.box2d.*;
import fr.supdevinci.games.ecs.*;

public class CollisionHandler implements ContactListener {
    private final CollisionListener listener;

    public CollisionHandler(CollisionListener listener) {
        this.listener = listener;
    }

    @Override
    public void beginContact(Contact contact) {
        Object dataA = contact.getFixtureA().getBody().getUserData();
        Object dataB = contact.getFixtureB().getBody().getUserData();

        // Une balle touche un mur (userData null)
        if (dataA instanceof Bullet && dataB == null) { listener.onBulletHitWall((Bullet) dataA); return; }
        if (dataB instanceof Bullet && dataA == null) { listener.onBulletHitWall((Bullet) dataB); return; }

        if (!(dataA instanceof Entity) || !(dataB instanceof Entity)) return;

        // Une balle touche un Damageable
        if (dataA instanceof Bullet && dataB instanceof Damageable) { bulletHitDamageable((Bullet) dataA, (Damageable) dataB); return; }
        if (dataB instanceof Bullet && dataA instanceof Damageable) { bulletHitDamageable((Bullet) dataB, (Damageable) dataA); return; }

        // Un ennemi touche le joueur
        if (dataA instanceof Enemy && dataB instanceof Tank) { listener.onEnemyHitTank((Enemy) dataA, (Tank) dataB); return; }
        if (dataB instanceof Enemy && dataA instanceof Tank) { listener.onEnemyHitTank((Enemy) dataB, (Tank) dataA); return; }

        // Le joueur touche une orbe d'expérience
        if (dataA instanceof Tank && dataB instanceof ExpOrb) { listener.onOrbCollected((ExpOrb) dataB); return; }
        if (dataB instanceof Tank && dataA instanceof ExpOrb) { listener.onOrbCollected((ExpOrb) dataA); return; }
    }

    private void bulletHitDamageable(Bullet bullet, Damageable target) {
         listener.onBulletHit(bullet, target);
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
