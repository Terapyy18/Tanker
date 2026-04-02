package fr.supdevinci.games.ecs;

/**
 * Interface implémentée par toute entité pouvant recevoir des dégâts.
 * Permet au CollisionHandler de travailler sans connaître les types concrets.
 */
public interface Damageable {
    void takeDamage(float amount);
    boolean isAlive();
}
