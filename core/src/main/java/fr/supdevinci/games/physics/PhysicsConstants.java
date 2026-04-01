package fr.supdevinci.games.physics;

public final class PhysicsConstants {
    public static final short CATEGORY_PLAYER       = 0x0001;
    public static final short CATEGORY_ENEMY         = 0x0002;
    public static final short CATEGORY_BULLET_PLAYER = 0x0004;
    public static final short CATEGORY_BULLET_ENEMY  = 0x0008;
    public static final short CATEGORY_EXP_ORB       = 0x0010;
    public static final short CATEGORY_WALL          = 0x0020;

    public static final short MASK_PLAYER       = CATEGORY_ENEMY | CATEGORY_BULLET_ENEMY | CATEGORY_EXP_ORB | CATEGORY_WALL;
    public static final short MASK_ENEMY         = CATEGORY_PLAYER | CATEGORY_BULLET_PLAYER | CATEGORY_WALL | CATEGORY_ENEMY;
    public static final short MASK_BULLET_PLAYER = CATEGORY_ENEMY | CATEGORY_WALL;
    public static final short MASK_BULLET_ENEMY  = CATEGORY_PLAYER | CATEGORY_WALL;
    public static final short MASK_EXP_ORB       = CATEGORY_PLAYER;
    public static final short MASK_WALL          = CATEGORY_PLAYER | CATEGORY_ENEMY | CATEGORY_BULLET_PLAYER | CATEGORY_BULLET_ENEMY;

    public static final float TIME_STEP = 1f / 60f;
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 2;

    private PhysicsConstants() {}
}
