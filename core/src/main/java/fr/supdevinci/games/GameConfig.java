package fr.supdevinci.games;

public final class GameConfig {
    // Map
    public static final float MAP_WIDTH = 200f;
    public static final float MAP_HEIGHT = 150f;

    // Viewport
    public static final float VIEWPORT_WIDTH = 40f;
    public static final float VIEWPORT_HEIGHT = 30f;

    // Player
    public static final float PLAYER_WIDTH = 2f;
    public static final float PLAYER_HEIGHT = 1.5f;
    public static final float PLAYER_SPEED = 10f;
    public static final float PLAYER_MAX_HEALTH = 100f;
    public static final float PLAYER_DAMAGE = 25f;
    public static final float PLAYER_FIRE_RATE = 0.3f;

    // Bullets
    public static final float BULLET_SPEED = 30f;
    public static final float BULLET_RADIUS = 0.2f;
    public static final float BULLET_LIFETIME = 3f;
    public static final float ENEMY_BULLET_SPEED = 15f;
    
    // Heavy Bullets
    public static final float HEAVY_BULLET_SPEED = 20f;
    public static final float HEAVY_BULLET_RADIUS = 0.4f;
    public static final float HEAVY_FIRE_RATE = 2.0f;
    public static final float HEAVY_DAMAGE_MULT = 3.0f;

    // EXP
    public static final float BASE_EXP_TO_LEVEL = 100f;
    public static final float EXP_GROWTH_FACTOR = 1.5f;
    public static final float EXP_ORB_ATTRACT_RANGE = 5f;
    public static final float EXP_ORB_SPEED = 15f;

    // Waves
    public static final int TOTAL_WAVES = 10;
    public static final int WAVE_BASE_ENEMIES = 3;
    public static final int WAVE_ENEMY_GROWTH = 2;
    public static final float WAVE_PAUSE_DURATION = 3f;
    public static final float SPAWN_DISTANCE = 25f;
    public static final float SPAWN_INTERVAL = 0.5f;

    // Level-up bonuses
    public static final float LEVEL_HEALTH_BONUS = 0.10f;
    public static final float LEVEL_SPEED_BONUS = 0.05f;
    public static final float LEVEL_DAMAGE_BONUS = 0.08f;
    public static final float LEVEL_FIRE_RATE_BONUS = 0.05f;
    public static final float LEVEL_ARMOR_BONUS = 0.03f;

    private GameConfig() {}
}
