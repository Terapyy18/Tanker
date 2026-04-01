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

    public void renderUpgradeMenu() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        
        // Draw overlay
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, 0, w, h);
        
        // Buttons
        shapeRenderer.setColor(0.2f, 0.2f, 0.4f, 1f);
        shapeRenderer.rect(w / 2f - 250, h / 2f - 50, 200, 100); // Burst button
        shapeRenderer.rect(w / 2f + 50, h / 2f - 50, 200, 100);  // HP button
        shapeRenderer.end();
        Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(3f);
        font.draw(batch, "LEVEL 5 UPGRADE!", w / 2f - 150, h / 2f + 150);
        
        font.getData().setScale(1.5f);
        font.draw(batch, "Rafale (Tir continu)", w / 2f - 240, h / 2f + 20);
        font.draw(batch, "Hold Left Click", w / 2f - 220, h / 2f - 20);
        
        font.draw(batch, "Blindage Lourd", w / 2f + 70, h / 2f + 20);
        font.draw(batch, "+200 HP Max", w / 2f + 90, h / 2f - 20);
        font.getData().setScale(1.5f); // Used by HUD renderer
        batch.end();
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

        String waveText = waveManager.isInfinite()
            ? "Wave " + waveManager.getCurrentWave()
            : "Wave " + waveManager.getCurrentWave() + "/" + 10;
        font.draw(batch, waveText, w - 150, h - 24);

        // Between waves notification
        if (waveManager.isBetweenWaves()) {
            String msg = "Wave " + (waveManager.getCurrentWave() + 1) + " incoming...";
            font.setColor(Color.YELLOW);
            font.draw(batch, msg, w / 2f - 60, h / 2f + 50);
        }

        // Boss wave warning (only in non-infinite with wave 10)
        if (waveManager.isWaveInProgress() && waveManager.getCurrentWave() == 10 && !waveManager.isInfinite()) {
            font.setColor(Color.RED);
            font.draw(batch, "!! BOSS WAVE !!", w / 2f - 50, h - 24);
        }

        batch.end();
        
        renderControlsPanel();
    }

    private void renderControlsPanel() {
        // Semi-transparent dark background panel
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
        shapeRenderer.rect(10, 10, 220, 165);
        shapeRenderer.end();
        Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        font.getData().setScale(1.1f);
        font.setColor(Color.GOLD);
        font.draw(batch, "CONTROLES", 20, 168);

        font.getData().setScale(0.95f);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Z / W  -  Avancer",      20, 148);
        font.draw(batch, "S       -  Reculer",      20, 130);
        font.draw(batch, "Q / A  -  Gauche",        20, 112);
        font.draw(batch, "D       -  Droite",        20, 94);
        font.draw(batch, "Clic G  -  Tirer",         20, 76);
        font.draw(batch, "Clic D  -  Missile lourd", 20, 58);
        font.setColor(0.5f, 0.8f, 1.0f, 1.0f);
        font.draw(batch, "  (debloque niv. 3)",      20, 40);

        font.getData().setScale(1f);
        batch.end();
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
