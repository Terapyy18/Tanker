package fr.supdevinci.games.physics;

import com.badlogic.gdx.physics.box2d.*;
import fr.supdevinci.games.GameConfig;
import fr.supdevinci.games.ecs.EnemyType;

public class BodyFactory {

    public Body createTankBody(World world, float x, float y) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.set(x, y);
        bd.fixedRotation = true;
        bd.linearDamping = 5f;
        Body body = world.createBody(bd);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(GameConfig.PLAYER_WIDTH / 2f, GameConfig.PLAYER_HEIGHT / 2f);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 1f;
        fd.friction = 0.3f;
        fd.restitution = 0.1f;
        fd.filter.categoryBits = PhysicsConstants.CATEGORY_PLAYER;
        fd.filter.maskBits = PhysicsConstants.MASK_PLAYER;
        body.createFixture(fd);
        shape.dispose();

        return body;
    }

    public Body createEnemyBody(World world, float x, float y, EnemyType type) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.set(x, y);
        bd.fixedRotation = true;
        Body body = world.createBody(bd);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(type.getWidth() / 2f, type.getHeight() / 2f);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 1f;
        fd.friction = 0.2f;
        fd.restitution = 0.3f;
        fd.filter.categoryBits = PhysicsConstants.CATEGORY_ENEMY;
        fd.filter.maskBits = PhysicsConstants.MASK_ENEMY;
        body.createFixture(fd);
        shape.dispose();

        return body;
    }

    public Body createPlayerBulletBody(World world, float x, float y, float vx, float vy) {
        return createBulletBody(world, x, y, vx, vy, PhysicsConstants.CATEGORY_BULLET_PLAYER, PhysicsConstants.MASK_BULLET_PLAYER);
    }

    public Body createEnemyBulletBody(World world, float x, float y, float vx, float vy) {
        return createBulletBody(world, x, y, vx, vy, PhysicsConstants.CATEGORY_BULLET_ENEMY, PhysicsConstants.MASK_BULLET_ENEMY);
    }

    private Body createBulletBody(World world, float x, float y, float vx, float vy, short category, short mask) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.set(x, y);
        bd.bullet = true;
        bd.fixedRotation = true;
        Body body = world.createBody(bd);

        CircleShape shape = new CircleShape();
        shape.setRadius(GameConfig.BULLET_RADIUS);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 0.1f;
        fd.friction = 0f;
        fd.restitution = 0f;
        fd.filter.categoryBits = category;
        fd.filter.maskBits = mask;
        body.createFixture(fd);
        shape.dispose();

        body.setLinearVelocity(vx, vy);
        return body;
    }

    public Body createExpOrbBody(World world, float x, float y) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.set(x, y);
        bd.fixedRotation = true;
        bd.linearDamping = 3f;
        Body body = world.createBody(bd);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.3f);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 0.1f;
        fd.isSensor = true;
        fd.filter.categoryBits = PhysicsConstants.CATEGORY_EXP_ORB;
        fd.filter.maskBits = PhysicsConstants.MASK_EXP_ORB;
        body.createFixture(fd);
        shape.dispose();

        return body;
    }

    public void createWalls(World world) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        bd.position.set(0, 0);
        Body body = world.createBody(bd);

        ChainShape chain = new ChainShape();
        chain.createLoop(new float[]{
            0, 0,
            GameConfig.MAP_WIDTH, 0,
            GameConfig.MAP_WIDTH, GameConfig.MAP_HEIGHT,
            0, GameConfig.MAP_HEIGHT
        });

        FixtureDef fd = new FixtureDef();
        fd.shape = chain;
        fd.friction = 0.5f;
        fd.restitution = 0.2f;
        fd.filter.categoryBits = PhysicsConstants.CATEGORY_WALL;
        fd.filter.maskBits = PhysicsConstants.MASK_WALL;
        body.createFixture(fd);
        chain.dispose();
    }
}
