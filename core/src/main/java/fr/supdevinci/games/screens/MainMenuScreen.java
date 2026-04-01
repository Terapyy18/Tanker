package fr.supdevinci.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import fr.supdevinci.games.Difficulty;
import fr.supdevinci.games.Main;

public class MainMenuScreen extends ScreenAdapter {
    private final Main game;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ShapeRenderer renderer;
    private final OrthographicCamera camera;

    private final Rectangle btnNormal;
    private final Rectangle btnHard;
    private final Rectangle btnInfinite;

    public MainMenuScreen(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.renderer = new ShapeRenderer();
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        
        btnNormal = new Rectangle(w / 2f - 250, h / 2f + 50, 500, 80);
        btnHard = new Rectangle(w / 2f - 250, h / 2f - 50, 500, 80);
        btnInfinite = new Rectangle(w / 2f - 250, h / 2f - 150, 500, 80);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        renderer.setProjectionMatrix(camera.combined);
        
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.2f, 0.4f, 0.2f, 1f);
        renderer.rect(btnNormal.x, btnNormal.y, btnNormal.width, btnNormal.height);
        
        renderer.setColor(0.6f, 0.2f, 0.2f, 1f);
        renderer.rect(btnHard.x, btnHard.y, btnHard.width, btnHard.height);
        
        renderer.setColor(0.4f, 0.2f, 0.8f, 1f);
        renderer.rect(btnInfinite.x, btnInfinite.y, btnInfinite.width, btnInfinite.height);
        renderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(4f);
        font.draw(batch, "TANK SURVIVAL", Gdx.graphics.getWidth() / 2f - 180, Gdx.graphics.getHeight() - 100);
        
        font.getData().setScale(2f);
        font.draw(batch, "NORMAL (10 Waves)", btnNormal.x + 130, btnNormal.y + 50);
        font.draw(batch, "HARD (Plus d'ennemis, cadence lente)", btnHard.x + 30, btnHard.y + 50);
        font.draw(batch, "INFINITE (Mode de survie sans fin)", btnInfinite.x + 40, btnInfinite.y + 50);
        batch.end();

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mx = Gdx.input.getX();
            float my = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (btnNormal.contains(mx, my)) {
                game.setScreen(new GameScreen(game, Difficulty.NORMAL));
            } else if (btnHard.contains(mx, my)) {
                game.setScreen(new GameScreen(game, Difficulty.HARD));
            } else if (btnInfinite.contains(mx, my)) {
                game.setScreen(new GameScreen(game, Difficulty.INFINITE));
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        renderer.dispose();
    }
}
