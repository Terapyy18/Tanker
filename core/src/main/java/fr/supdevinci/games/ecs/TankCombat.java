package fr.supdevinci.games.ecs;

import fr.supdevinci.games.GameConfig;

public class TankCombat {
    private float damage;
    private float fireRate;
    private float fireCooldown;
    private float heavyFireCooldown;
    private float heavyFireRate;
    private boolean heavyUnlocked;
    private boolean burstFireUnlocked;
    private float burstActiveTimer;
    private boolean burstCooling;

    public TankCombat() {
        this.damage = GameConfig.PLAYER_DAMAGE;
        this.fireRate = GameConfig.PLAYER_FIRE_RATE;
        this.fireCooldown = 0f;
        this.heavyFireCooldown = 0f;
        this.heavyFireRate = GameConfig.HEAVY_FIRE_RATE;
        this.heavyUnlocked = false;
        this.burstFireUnlocked = false;
        this.burstActiveTimer = 0f;
        this.burstCooling = false;
    }

    public void update(float delta, boolean alive) {
        if (!alive) return;
        fireCooldown = Math.max(0, fireCooldown - delta);
        if (heavyUnlocked) {
            heavyFireCooldown = Math.max(0, heavyFireCooldown - delta);
        }
        if (burstFireUnlocked) {
            if (burstCooling) {
                burstActiveTimer -= delta * 0.5f;
                if (burstActiveTimer <= 0) {
                    burstActiveTimer = 0;
                    burstCooling = false;
                }
            } else if (fireCooldown <= 0) {
                burstActiveTimer = Math.max(0, burstActiveTimer - delta);
            }
        }
    }

    public boolean canFire(boolean alive) {
        return !(burstFireUnlocked && burstCooling) && alive && fireCooldown <= 0;
    }

    public void resetFireCooldown() {
        fireCooldown = fireRate;
        if (burstFireUnlocked) {
            burstActiveTimer += fireRate;
            if (burstActiveTimer >= 1.0f) {
                burstCooling = true;
            }
        }
    }

    public boolean canFireHeavy(boolean alive) {
        return alive && heavyUnlocked && heavyFireCooldown <= 0;
    }

    public void resetHeavyFireCooldown() {
        heavyFireCooldown = heavyFireRate;
    }

    public void enableBurstFire() {
        burstFireUnlocked = true;
        fireRate = 0.08f;
        resetFireCooldown();
    }

    public void setHeavyUnlocked(boolean unlocked) { this.heavyUnlocked = unlocked; }
    public void setHeavyFireRate(float rate) { this.heavyFireRate = rate; }
    public void setDamage(float damage) { this.damage = damage; }
    public void setFireRate(float fireRate) { this.fireRate = fireRate; }
    
    public float getDamage() { return damage; }
    public float getFireRate() { return fireRate; }
    public float getFireCooldown() { return fireCooldown; }
    public float getHeavyFireCooldown() { return heavyFireCooldown; }
    public float getHeavyFireRate() { return heavyFireRate; }
    public boolean isHeavyUnlocked() { return heavyUnlocked; }
    public boolean isBurstFireUnlocked() { return burstFireUnlocked; }
    public float getBurstActiveTimer() { return burstActiveTimer; }
    public boolean isBurstCooling() { return burstCooling; }
}
