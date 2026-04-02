package fr.supdevinci.games.systems;

import com.badlogic.gdx.physics.box2d.*;
import fr.supdevinci.games.ecs.*;

import java.util.function.BiConsumer;

public class CollisionHandler implements ContactListener {
    private final CollisionListener listener;

    public CollisionHandler(CollisionListener listener) {
        this.listener = listener;
    }

    @Override
    public void beginContact(Contact contact) {
        Object dataA = contact.getFixtureA().getBody().getUserData();
        Object dataB = contact.getFixtureB().getBody().getUserData();

        if (handleBulletWallCollision(dataA, dataB)) return;
        if (handleCollision(dataA, dataB, Bullet.class, Damageable.class, listener::onBulletHit)) return;
        if (handleCollision(dataA, dataB, ContactDamageSource.class, ContactDamageTarget.class, listener::onContactDamage)) return;
        handleCollision(dataA, dataB, Collector.class, Collectible.class, listener::onCollected);
    }

    private boolean handleBulletWallCollision(Object dataA, Object dataB) {
        if (dataA instanceof Bullet && dataB == null) {
            listener.onBulletHitWall((Bullet) dataA);
            return true;
        }
        if (dataB instanceof Bullet && dataA == null) {
            listener.onBulletHitWall((Bullet) dataB);
            return true;
        }
        return false;
    }

    private <TFirst, TSecond> boolean handleCollision(
        Object dataA,
        Object dataB,
        Class<TFirst> firstType,
        Class<TSecond> secondType,
        BiConsumer<TFirst, TSecond> collisionConsumer
    ) {
        if (firstType.isInstance(dataA) && secondType.isInstance(dataB)) {
            collisionConsumer.accept(firstType.cast(dataA), secondType.cast(dataB));
            return true;
        }
        if (firstType.isInstance(dataB) && secondType.isInstance(dataA)) {
            collisionConsumer.accept(firstType.cast(dataB), secondType.cast(dataA));
            return true;
        }
        return false;
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
