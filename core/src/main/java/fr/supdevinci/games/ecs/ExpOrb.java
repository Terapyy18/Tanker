package fr.supdevinci.games.ecs;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import fr.supdevinci.games.GameConfig;

public class ExpOrb extends Entity {
    private final int expValue;

    public ExpOrb(Body body, int expValue) {
        super(body, 0.6f, 0.6f);
        this.expValue = expValue;
    }

    public void update(float delta, Vector2 playerPos) {
        if (!alive || body == null) return;
        Vector2 pos = body.getPosition();
        float dist = pos.dst(playerPos);
        if (dist < GameConfig.EXP_ORB_ATTRACT_RANGE) {
            Vector2 dir = new Vector2(playerPos).sub(pos).nor();
            body.setLinearVelocity(dir.scl(GameConfig.EXP_ORB_SPEED));
        } else {
            body.setLinearVelocity(0, 0);
        }
    }

    @Override
    public void update(float delta) {
        // Utiliser update(delta, playerPos) à la place
    }

    @Override
    public void render(ShapeRenderer renderer) {
        if (!alive || body == null) return;
        Vector2 pos = body.getPosition();
        renderer.setColor(0.0f, 0.9f, 0.9f, 1f);
        // Forme de diamant utilisant un rectangle pivoté
        renderer.rect(pos.x - 0.2f, pos.y - 0.2f, 0.2f, 0.2f, 0.4f, 0.4f, 1f, 1f, 45f);
    }

    public int getExpValue() { return expValue; }
}
