package fr.supdevinci.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import fr.supdevinci.games.GameConfig;
import fr.supdevinci.games.GameWorld;
import fr.supdevinci.games.ecs.EntityManager;

public class GameRenderer {
    private final GameWorld world;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final Texture background;

    public GameRenderer(GameWorld world) {
        this.world = world;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.background = new Texture(Gdx.files.internal("BG_menu.png"));
    }

    public void render(Viewport viewport, OrthographicCamera camera) {
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.12f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        EntityManager entityManager = world.getEntityManager();

        // 1. Arrière-plan
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background, 0, 0, GameConfig.MAP_WIDTH, GameConfig.MAP_HEIGHT);
        batch.end();

        // 2. Entités de formes
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        entityManager.renderShapes(shapeRenderer);
        shapeRenderer.end();

        // 3. Sprites du Tank et autres sprites
        batch.begin();
        entityManager.renderSprites(batch, shapeRenderer);
        batch.end();

        // 4. Bordure du monde
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1f);
        shapeRenderer.rect(0, 0, GameConfig.MAP_WIDTH, GameConfig.MAP_HEIGHT);
        shapeRenderer.end();
    }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        background.dispose();
    }
}
