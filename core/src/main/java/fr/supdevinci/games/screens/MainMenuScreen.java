package fr.supdevinci.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
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
    private final Texture background;

    private final Rectangle btnNormal;
    private final Rectangle btnHard;
    private final Rectangle btnInfinite;
    private final Rectangle btnQuit;

    public MainMenuScreen(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.renderer = new ShapeRenderer();
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        this.background = new Texture(Gdx.files.internal("BG_menu.png"));

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        btnNormal = new Rectangle(w / 2f - 250, h / 2f + 50, 500, 70);
        btnHard = new Rectangle(w / 2f - 250, h / 2f - 40, 500, 70);
        btnInfinite = new Rectangle(w / 2f - 250, h / 2f - 130, 500, 70);
        btnQuit = new Rectangle(w / 2f - 250, h / 2f - 220, 500, 70);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        // Dessiner l'arrière-plan
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Position de la souris pour les effets de survol
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Activer la transparence pour les boutons
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        renderer.setProjectionMatrix(camera.combined);

        renderer.begin(ShapeRenderer.ShapeType.Filled);

        // Rendre les boutons avec vérification de survol
        drawMenuButton(renderer, btnNormal, 0.2f, 0.4f, 0.2f, btnNormal.contains(mx, my));
        drawMenuButton(renderer, btnHard, 0.6f, 0.2f, 0.2f, btnHard.contains(mx, my));
        drawMenuButton(renderer, btnInfinite, 0.4f, 0.2f, 0.8f, btnInfinite.contains(mx, my));
        drawMenuButton(renderer, btnQuit, 0.3f, 0.3f, 0.3f, btnQuit.contains(mx, my));

        renderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(4f);
        font.draw(batch, "TANK SURVIVAL", Gdx.graphics.getWidth() / 2f - 180, Gdx.graphics.getHeight() - 100);

        font.getData().setScale(2f);
        font.draw(batch, "NORMAL", btnNormal.x + 10, btnNormal.y + 45);
        font.draw(batch, "HARD", btnHard.x + 10, btnHard.y + 45);
        font.draw(batch, "INFINITE", btnInfinite.x + 10, btnInfinite.y + 45);

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "QUITTER LE JEU", btnQuit.x + 160, btnQuit.y + 45);
        batch.end();

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            // Utiliser mx/my calculés ci-dessus
            if (btnNormal.contains(mx, my)) {
                game.setScreen(new GameScreen(game, Difficulty.NORMAL));
            } else if (btnHard.contains(mx, my)) {
                game.setScreen(new GameScreen(game, Difficulty.HARD));
            } else if (btnInfinite.contains(mx, my)) {
                game.setScreen(new GameScreen(game, Difficulty.INFINITE));
            } else if (btnQuit.contains(mx, my)) {
                Gdx.app.exit();
            }
        }
    }

    private void drawMenuButton(ShapeRenderer renderer, Rectangle rect, float r, float g, float b, boolean hovered) {
        float alpha = hovered ? 0.95f : 0.75f;
        float factor = hovered ? 1.2f : 1.0f; // Plus lumineux au survol
        renderer.setColor(Math.min(1, r * factor), Math.min(1, g * factor), Math.min(1, b * factor), alpha);
        renderer.rect(rect.x, rect.y, rect.width, rect.height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        renderer.dispose();
        background.dispose();
    }
}
