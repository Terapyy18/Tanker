package fr.supdevinci.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
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
        hudRenderer = new HudRenderer(world.getEntityManager().getTank(), world.getLevelSystem(), world.getWaveManager());

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
            game.setScreen(new GameOverScreen(game, false, world.getWaveManager().getCurrentWave(), world.getLevelSystem().getLevel()));
            return;
        }
        if (world.getVictoryTimer() > 2.0f) {
            game.setScreen(new GameOverScreen(game, true, world.getWaveManager().getCurrentWave(), world.getLevelSystem().getLevel()));
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

            float w = Gdx.graphics.getWidth();
            float h = Gdx.graphics.getHeight();

            Rectangle btnBurst = new Rectangle(w / 2f - 250, h / 2f - 50, 200, 100);
            Rectangle btnHp = new Rectangle(w / 2f + 50, h / 2f - 50, 200, 100);

            Tank tank = world.getEntityManager().getTank();
            if (btnBurst.contains(mx, my)) {
                tank.enableBurstFire();
                world.setUpgrading(false);
            } else if (btnHp.contains(mx, my)) {
                tank.addExtraHealth(200f);
                world.setUpgrading(false);
            }
        }
    }

    private void renderPauseMenu() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Utiliser un batch/caméra temporaire pour l'UI si nécessaire
        // ou simplement utiliser une superposition ShapeRenderer comme avant.
        // Pour plus de concision et pour garder GameScreen propre, je garde le rendu du menu pause ici pour le moment mais simplifié.
        
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        ShapeRenderer uiRenderer = new ShapeRenderer();
        OrthographicCamera uiCam = new OrthographicCamera();
        uiCam.setToOrtho(false, w, h);
        uiCam.update();
        
        uiRenderer.setProjectionMatrix(uiCam.combined);
        uiRenderer.begin(ShapeRenderer.ShapeType.Filled);
        uiRenderer.setColor(0, 0, 0, 0.65f);
        uiRenderer.rect(0, 0, w, h);
        
        float panW = 320, panH = 200;
        float panX = w / 2f - panW / 2f, panY = h / 2f - panH / 2f;
        uiRenderer.setColor(0.12f, 0.14f, 0.20f, 1f);
        uiRenderer.rect(panX, panY, panW, panH);
        
        Rectangle btnResume = new Rectangle(panX + 30, panY + 90, 260, 50);
        Rectangle btnMenu = new Rectangle(panX + 30, panY + 25, 260, 50);
        
        uiRenderer.setColor(0.2f, 0.55f, 0.2f, 1f);
        uiRenderer.rect(btnResume.x, btnResume.y, btnResume.width, btnResume.height);
        uiRenderer.setColor(0.55f, 0.15f, 0.15f, 1f);
        uiRenderer.rect(btnMenu.x, btnMenu.y, btnMenu.width, btnMenu.height);
        uiRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        SpriteBatch uiBatch = new SpriteBatch();
        BitmapFont pauseFont = new BitmapFont();
        uiBatch.setProjectionMatrix(uiCam.combined);
        uiBatch.begin();
        pauseFont.getData().setScale(2.5f);
        pauseFont.draw(uiBatch, "PAUSE", panX + 95, panY + 185);
        pauseFont.getData().setScale(1.5f);
        pauseFont.draw(uiBatch, "Reprendre   (ESC)", panX + 45, panY + 123);
        pauseFont.draw(uiBatch, "Menu Principal", panX + 65, panY + 58);
        uiBatch.end();

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mx = Gdx.input.getX();
            float my = h - Gdx.input.getY();
            if (btnResume.contains(mx, my)) {
                world.setPaused(false);
            } else if (btnMenu.contains(mx, my)) {
                game.setScreen(new MainMenuScreen(game));
            }
        }
        
        uiRenderer.dispose();
        uiBatch.dispose();
        pauseFont.dispose();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        viewport.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if (renderer != null) renderer.dispose();
        if (world != null) world.dispose();
        if (hudRenderer != null) hudRenderer.dispose();
    }
}
