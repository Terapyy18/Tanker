package fr.supdevinci.games.ecs;

import com.badlogic.gdx.graphics.Color;

public enum EnemyType {
    BASIC   (30f,  5f,  1.0f, 1.0f, 10, 10f, 3.0f, 5f,   new Color(0.9f, 0.2f, 0.2f, 1f)),
    FAST    (15f,  8f,  0.7f, 0.7f, 15, 5f,  4.0f, 3f,   new Color(1.0f, 0.6f, 0.1f, 1f)),
    HEAVY   (80f,  3f,  1.8f, 1.8f, 25, 20f, 4.0f, 15f,  new Color(0.6f, 0.2f, 0.8f, 1f)),
    SHOOTER (40f,  4f,  1.2f, 1.2f, 20, 8f,  1.5f, 10f,  new Color(1.0f, 0.9f, 0.2f, 1f)),
    BOSS    (500f, 2f,  4.0f, 4.0f, 100, 30f, 1.0f, 20f, new Color(0.5f, 0.0f, 0.0f, 1f)),
    BOSS_2  (600f, 3f,  4.5f, 4.5f, 150, 40f, 0.5f, 15f, new Color(0.7f, 0.1f, 0.1f, 1f)),
    BOSS_3  (800f, 1.5f, 5.0f, 5.0f, 200, 50f, 1.5f, 30f, new Color(0.4f, 0.0f, 0.2f, 1f));

    private final float health;
    private final float speed;
    private final float width;
    private final float height;
    private final int expValue;
    private final float contactDamage;
    private final float fireRate;
    private final float bulletDamage;
    private final Color color;

    EnemyType(float health, float speed, float width, float height, int expValue,
              float contactDamage, float fireRate, float bulletDamage, Color color) {
        this.health = health;
        this.speed = speed;
        this.width = width;
        this.height = height;
        this.expValue = expValue;
        this.contactDamage = contactDamage;
        this.fireRate = fireRate;
        this.bulletDamage = bulletDamage;
        this.color = color;
    }

    public float getHealth() { return health; }
    public float getSpeed() { return speed; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public int getExpValue() { return expValue; }
    public float getContactDamage() { return contactDamage; }
    public float getFireRate() { return fireRate; }
    public float getBulletDamage() { return bulletDamage; }
    public Color getColor() { return color; }
    public boolean canShoot() { return fireRate > 0; }
}
