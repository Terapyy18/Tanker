package fr.supdevinci.games.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import fr.supdevinci.games.ecs.Tank;
import fr.supdevinci.games.systems.LevelSystem;
import fr.supdevinci.games.systems.WaveManager;

public class HudRenderer {
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera hudCamera;
    private final Tank tank;
    private final LevelSystem levelSystem;
    private final WaveManager waveManager;

    public HudRenderer(Tank tank, LevelSystem levelSystem, WaveManager waveManager) {
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.shapeRenderer = new ShapeRenderer();
        this.hudCamera = new OrthographicCamera();
        this.tank = tank;
        this.levelSystem = levelSystem;
        this.waveManager = waveManager;
    }

    public void render() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        hudCamera.setToOrtho(false, w, h);
        hudCamera.update();

        // Bars
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        float barX = 20;
        float barWidth = 200;

        // Health bar
        float healthY = h - 40;
        shapeRenderer.setColor(0.3f, 0.1f, 0.1f, 1f);
        shapeRenderer.rect(barX, healthY, barWidth, 20);
        float healthPct = tank.getMaxHealth() > 0 ? tank.getHealth() / tank.getMaxHealth() : 0;
        shapeRenderer.setColor(0.2f, 0.8f, 0.3f, 1f);
        shapeRenderer.rect(barX, healthY, barWidth * healthPct, 20);

        // EXP bar
        float expY = h - 70;
        shapeRenderer.setColor(0.1f, 0.1f, 0.3f, 1f);
        shapeRenderer.rect(barX, expY, barWidth, 15);
        float expPct = levelSystem.getExpPercentage();
        shapeRenderer.setColor(0.2f, 0.6f, 0.9f, 1f);
        shapeRenderer.rect(barX, expY, barWidth * expPct, 15);

        shapeRenderer.end();

        // Text
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "HP: " + (int) tank.getHealth() + "/" + (int) tank.getMaxHealth(), barX + barWidth + 10, healthY + 16);
        font.draw(batch, "Level " + levelSystem.getLevel(), barX + barWidth + 10, expY + 13);

        String waveText = "Wave " + waveManager.getCurrentWave() + "/" + 10;
        font.draw(batch, waveText, w - 150, h - 24);

        // Between waves notification
        if (waveManager.isBetweenWaves()) {
            String msg = "Wave " + (waveManager.getCurrentWave() + 1) + " incoming...";
            font.setColor(Color.YELLOW);
            font.draw(batch, msg, w / 2f - 60, h / 2f + 50);
        }

        // Boss wave warning
        if (waveManager.isWaveInProgress() && waveManager.getCurrentWave() == 10) {
            font.setColor(Color.RED);
            font.draw(batch, "!! BOSS WAVE !!", w / 2f - 50, h - 24);
        }

        batch.end();
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
