package fr.supdevinci.games.systems;

import fr.supdevinci.games.ecs.Bullet;
import fr.supdevinci.games.ecs.Damageable;
import fr.supdevinci.games.ecs.ExpOrb;

public interface CollisionListener {
    void onBulletHit(Bullet bullet, Damageable target);
    void onBulletHitWall(Bullet bullet);
    void onEnemyHitTank(Damageable enemy, Damageable tank);
    void onOrbCollected(ExpOrb orb);
}
