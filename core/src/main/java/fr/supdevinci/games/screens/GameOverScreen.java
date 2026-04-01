package fr.supdevinci.games.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import fr.supdevinci.games.Main;

public class GameOverScreen implements Screen {
    private final Main game;
    private final boolean victory;
    private final int waveReached;
    private final int levelReached;

    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont font;
    private OrthographicCamera camera;
    private GlyphLayout layout;
    private Texture background;
    private com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer;

    public GameOverScreen(Main game, boolean victory, int waveReached, int levelReached) {
        this.game = game;
        this.victory = victory;
        this.waveReached = waveReached;
        this.levelReached = levelReached;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        camera = new OrthographicCamera();
        layout = new GlyphLayout();
        background = new Texture(Gdx.files.internal("BG_menu.png"));
        shapeRenderer = new com.badlogic.gdx.graphics.glutils.ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera.setToOrtho(false, w, h);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background, 0, 0, w, h);
        batch.end();

        // Dark overlay for better text readability
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.4f);
        shapeRenderer.rect(0, 0, w, h);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();

        // Title
        String title = victory ? "VICTORY!" : "GAME OVER";
        titleFont.setColor(victory ? Color.GOLD : Color.RED);
        layout.setText(titleFont, title);
        titleFont.draw(batch, title, (w - layout.width) / 2f, h * 0.65f);

        // Stats
        font.setColor(Color.WHITE);
        String waveLine = "Wave: " + waveReached;
        layout.setText(font, waveLine);
        font.draw(batch, waveLine, (w - layout.width) / 2f, h * 0.45f);

        String levelLine = "Level: " + levelReached;
        layout.setText(font, levelLine);
        font.draw(batch, levelLine, (w - layout.width) / 2f, h * 0.38f);

        // Restart prompt
        font.setColor(Color.GRAY);
        String restart = "Press SPACE for Main Menu";
        layout.setText(font, restart);
        font.draw(batch, restart, (w - layout.width) / 2f, h * 0.2f);

        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            // Need to import and use MainMenuScreen
            game.setScreen(new fr.supdevinci.games.screens.MainMenuScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
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
        if (batch != null)
            batch.dispose();
        if (titleFont != null)
            titleFont.dispose();
        if (font != null)
            font.dispose();
        if (background != null)
            background.dispose();
        if (shapeRenderer != null)
            shapeRenderer.dispose();
    }
}
