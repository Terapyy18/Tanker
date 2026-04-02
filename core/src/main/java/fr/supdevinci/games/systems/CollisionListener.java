package fr.supdevinci.games.systems;

import fr.supdevinci.games.ecs.Bullet;
import fr.supdevinci.games.ecs.Collectible;
import fr.supdevinci.games.ecs.Collector;
import fr.supdevinci.games.ecs.ContactDamageSource;
import fr.supdevinci.games.ecs.ContactDamageTarget;
import fr.supdevinci.games.ecs.Damageable;

public interface CollisionListener {
    void onBulletHit(Bullet bullet, Damageable target);
    void onBulletHitWall(Bullet bullet);
    void onContactDamage(ContactDamageSource source, ContactDamageTarget target);
    void onCollected(Collector collector, Collectible collectible);
}
