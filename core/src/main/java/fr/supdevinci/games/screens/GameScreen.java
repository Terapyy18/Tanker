package fr.supdevinci.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import fr.supdevinci.games.ui.BoutonUI;
import java.util.List;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import fr.supdevinci.games.Difficulty;
import fr.supdevinci.games.GameConfig;
import fr.supdevinci.games.GameWorld;
import fr.supdevinci.games.Main;
import fr.supdevinci.games.ecs.Tank;
import fr.supdevinci.games.systems.CameraSystem;
import fr.supdevinci.games.ui.HudRenderer;

public class GameScreen implements Screen {
    private final Main game;
    private final Difficulty difficulty;

    private GameWorld world;
    private GameRenderer renderer;
    private GameInputHandler inputHandler;
    private CameraSystem cameraSystem;
    private HudRenderer hudRenderer;
    private ShapeRenderer pauseRenderer;
    private SpriteBatch pauseBatch;
    private BitmapFont pauseFont;
    private OrthographicCamera pauseCamera;
    private BoutonUI resumeButton;
    private BoutonUI menuButton;

    private OrthographicCamera camera;
    private ExtendViewport viewport;

    public GameScreen(Main game, Difficulty difficulty) {
        this.game = game;
        this.difficulty = difficulty;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(GameConfig.VIEWPORT_WIDTH, GameConfig.VIEWPORT_HEIGHT, camera);
        camera.position.set(GameConfig.MAP_WIDTH / 2f, GameConfig.MAP_HEIGHT / 2f, 0);
        camera.update();

        world = new GameWorld(difficulty);
        renderer = new GameRenderer(world);
        inputHandler = new GameInputHandler(world, camera);
        cameraSystem = new CameraSystem(camera);
        hudRenderer = new HudRenderer(world.getEntityManager().getTank(), world.getLevelSystem(),
                world.getWaveManager());
        pauseRenderer = new ShapeRenderer();
        pauseBatch = new SpriteBatch();
        pauseFont = new BitmapFont();
        pauseCamera = new OrthographicCamera();
        resumeButton = new BoutonUI("Reprendre", "ESC", 0, 0, 260, 50);
        menuButton = new BoutonUI("Menu Principal", "", 0, 0, 260, 50);

        Gdx.input.setInputProcessor(inputHandler);
    }

    @Override
    public void render(float delta) {
        // --- Mise à jour ---
        inputHandler.update(delta);
        world.update(delta);

        Tank tank = world.getEntityManager().getTank();
        if (tank != null) {
            cameraSystem.update(delta, tank.getPosition());
        }

        // Vérifier la fin de partie / victoire
        if (tank != null && !tank.isAlive()) {
            game.setScreen(new GameOverScreen(game, false, world.getWaveManager().getCurrentWave(),
                    world.getLevelSystem().getLevel()));
            return;
        }
        if (world.getVictoryTimer() > 2.0f) {
            game.setScreen(new GameOverScreen(game, true, world.getWaveManager().getCurrentWave(),
                    world.getLevelSystem().getLevel()));
            return;
        }

        // --- Rendu ---
        renderer.render(viewport, camera);

        if (world.isPaused()) {
            renderPauseMenu();
        } else if (world.isUpgrading()) {
            hudRenderer.renderUpgradeMenu();
            handleUpgradeInput();
        } else {
            hudRenderer.render();
        }
    }

    private void handleUpgradeInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mx = Gdx.input.getX();
            float my = Gdx.graphics.getHeight() - Gdx.input.getY();

            Tank tank = world.getEntityManager().getTank();
            List<BoutonUI> buttons = hudRenderer.getUpgradeButtons();

            for (int i = 0; i < buttons.size(); i++) {
                if (buttons.get(i).contient(mx, my)) {
                    if (i == 0) {
                        tank.enableBurstFire();
                    } else if (i == 1) {
                        tank.addExtraHealth(200f);
                    }
                    world.setUpgrading(false);
                    break;
                }
            }
        }
    }

    private void renderPauseMenu() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        float panW = 320f;
        float panH = 200f;
        float panX = w / 2f - panW / 2f;
        float panY = h / 2f - panH / 2f;
        float mx = Gdx.input.getX();
        float my = h - Gdx.input.getY();

        pauseCamera.setToOrtho(false, w, h);
        pauseCamera.update();
        resumeButton.setBounds(panX + 30, panY + 90, 260, 50);
        menuButton.setBounds(panX + 30, panY + 25, 260, 50);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        pauseRenderer.setProjectionMatrix(pauseCamera.combined);
        pauseRenderer.begin(ShapeRenderer.ShapeType.Filled);
        pauseRenderer.setColor(0, 0, 0, 0.65f);
        pauseRenderer.rect(0, 0, w, h);
        pauseRenderer.setColor(0.12f, 0.14f, 0.20f, 1f);
        pauseRenderer.rect(panX, panY, panW, panH);
        pauseRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        pauseBatch.setProjectionMatrix(pauseCamera.combined);
        pauseBatch.begin();
        pauseFont.getData().setScale(2.5f);
        pauseFont.draw(pauseBatch, "PAUSE", panX + 95, panY + 185);
        pauseFont.getData().setScale(1.5f);
        pauseBatch.end();
        pauseFont.getData().setScale(1f);

        resumeButton.render(pauseRenderer, pauseBatch, pauseFont, resumeButton.contient(mx, my));
        menuButton.render(pauseRenderer, pauseBatch, pauseFont, menuButton.contient(mx, my));

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (resumeButton.contient(mx, my)) {
                world.setPaused(false);
            } else if (menuButton.contient(mx, my)) {
                game.setScreen(new MainMenuScreen(game));
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0)
            return;
        viewport.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if (renderer != null)
            renderer.dispose();
        if (world != null)
            world.dispose();
        if (hudRenderer != null)
            hudRenderer.dispose();
        if (pauseRenderer != null)
            pauseRenderer.dispose();
        if (pauseBatch != null)
            pauseBatch.dispose();
        if (pauseFont != null)
            pauseFont.dispose();
    }
}
