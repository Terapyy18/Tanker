package fr.supdevinci.games.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import fr.supdevinci.games.GameConfig;

public class CameraSystem {
    private final OrthographicCamera camera;
    private static final float LERP_SPEED = 5f;

    public CameraSystem(OrthographicCamera camera) {
        this.camera = camera;
    }

    public void update(float delta, Vector2 targetPos) {
        float targetX = MathUtils.clamp(targetPos.x,
            camera.viewportWidth / 2f, GameConfig.MAP_WIDTH - camera.viewportWidth / 2f);
        float targetY = MathUtils.clamp(targetPos.y,
            camera.viewportHeight / 2f, GameConfig.MAP_HEIGHT - camera.viewportHeight / 2f);

        camera.position.x += (targetX - camera.position.x) * LERP_SPEED * delta;
        camera.position.y += (targetY - camera.position.y) * LERP_SPEED * delta;
        camera.update();
    }
}
