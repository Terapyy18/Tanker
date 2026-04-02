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
import fr.supdevinci.games.Difficulty;
import fr.supdevinci.games.Main;
import fr.supdevinci.games.ui.BoutonUI;

public class MainMenuScreen extends ScreenAdapter {
    private final Main game;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ShapeRenderer renderer;
    private final OrthographicCamera camera;
    private final Texture background;

    private final BoutonUI btnNormal;
    private final BoutonUI btnHard;
    private final BoutonUI btnInfinite;
    private final BoutonUI btnQuit;

    public MainMenuScreen(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.renderer = new ShapeRenderer();
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        this.background = new Texture(Gdx.files.internal("BG_menu.png"));

        btnNormal = new BoutonUI("NORMAL", "", 0, 0, 500, 70);
        btnHard = new BoutonUI("HARD", "", 0, 0, 500, 70);
        btnInfinite = new BoutonUI("INFINITE", "", 0, 0, 500, 70);
        btnQuit = new BoutonUI("QUITTER LE JEU", "", 0, 0, 500, 70);
        layoutButtons();
    }

    private void layoutButtons() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        btnNormal.setBounds(w / 2f - 250, h / 2f + 50, 500, 70);
        btnHard.setBounds(w / 2f - 250, h / 2f - 40, 500, 70);
        btnInfinite.setBounds(w / 2f - 250, h / 2f - 130, 500, 70);
        btnQuit.setBounds(w / 2f - 250, h / 2f - 220, 500, 70);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        layoutButtons();

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

        batch.setProjectionMatrix(camera.combined);
        font.setColor(Color.WHITE);
        font.getData().setScale(4f);
        batch.begin();
        font.draw(batch, "TANKER", Gdx.graphics.getWidth() / 2f - 180, Gdx.graphics.getHeight() - 100);
        batch.end();
        font.getData().setScale(1f);

        btnNormal.render(renderer, batch, font, btnNormal.contient(mx, my));
        btnHard.render(renderer, batch, font, btnHard.contient(mx, my));
        btnInfinite.render(renderer, batch, font, btnInfinite.contient(mx, my));
        btnQuit.render(renderer, batch, font, btnQuit.contient(mx, my));

        Gdx.gl.glDisable(GL20.GL_BLEND);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (btnNormal.contient(mx, my)) {
                game.setScreen(new GameScreen(game, Difficulty.NORMAL));
            } else if (btnHard.contient(mx, my)) {
                game.setScreen(new GameScreen(game, Difficulty.HARD));
            } else if (btnInfinite.contient(mx, my)) {
                game.setScreen(new GameScreen(game, Difficulty.INFINITE));
            } else if (btnQuit.contient(mx, my)) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        renderer.dispose();
        background.dispose();
    }
}
