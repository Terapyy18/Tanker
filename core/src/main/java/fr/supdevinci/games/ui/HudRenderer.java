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

import java.util.ArrayList;
import java.util.List;

public class HudRenderer {
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera hudCamera;
    private final Tank tank;
    private final LevelSystem levelSystem;
    private final WaveManager waveManager;

    private final BarreUI healthBar;
    private final BarreUI expBar;
    private final List<BoutonUI> upgradeButtons = new ArrayList<>();

    public HudRenderer(Tank tank, LevelSystem levelSystem, WaveManager waveManager) {
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.shapeRenderer = new ShapeRenderer();
        this.hudCamera = new OrthographicCamera();
        this.tank = tank;
        this.levelSystem = levelSystem;
        this.waveManager = waveManager;

        this.healthBar = new BarreUI(new Color(0.3f, 0.1f, 0.1f, 1f), new Color(0.2f, 0.8f, 0.3f, 1f), 200, 20);
        this.expBar = new BarreUI(new Color(0.1f, 0.1f, 0.3f, 1f), new Color(0.2f, 0.6f, 0.9f, 1f), 200, 15);

        initUpgradeMenu();
    }

    private void initUpgradeMenu() {
        upgradeButtons.add(new BoutonUI("Rafale", "Tir continu (Hold Left Click)", 0, 0, 200, 100));
        upgradeButtons.add(new BoutonUI("Blindage Lourd", "+200 HP Max", 0, 0, 200, 100));
    }

    private void layoutUpgradeMenu(float w, float h) {
        upgradeButtons.get(0).setBounds(w / 2f - 250, h / 2f - 50, 200, 100);
        upgradeButtons.get(1).setBounds(w / 2f + 50, h / 2f - 50, 200, 100);
    }

    public void renderUpgradeMenu() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        float mx = Gdx.input.getX();
        float my = h - Gdx.input.getY();
        layoutUpgradeMenu(w, h);
        hudCamera.setToOrtho(false, w, h);
        hudCamera.update();
        
        // Dessiner la superposition
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, 0, w, h);
        shapeRenderer.end();
        Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(3f);
        font.draw(batch, "LEVEL 5 UPGRADE!", w / 2f - 150, h / 2f + 150);
        batch.end();

        for (BoutonUI btn : upgradeButtons) {
            btn.render(shapeRenderer, batch, font, btn.contient(mx, my));
        }
    }

    public void render() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        hudCamera.setToOrtho(false, w, h);
        hudCamera.update();

        // Barres
        float barX = 120; // x + barWidth / 2 = 20 + 200 / 2 = 120 (car BarreUI centre son dessin)
        float healthPct = tank.getMaxHealth() > 0 ? tank.getHealth() / tank.getMaxHealth() : 0;
        healthBar.render(shapeRenderer, hudCamera.combined, barX, h - 40, healthPct);

        float expPct = levelSystem.getExpPercentage();
        expBar.render(shapeRenderer, hudCamera.combined, barX, h - 70, expPct);

        // Texte
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
        font.draw(batch, "HP: " + (int) tank.getHealth() + "/" + (int) tank.getMaxHealth(), 230, h - 24);
        font.draw(batch, "Level " + levelSystem.getLevel(), 230, h - 57);

        String waveStr = waveManager.isInfinite()
            ? "Wave " + waveManager.getCurrentWave()
            : "Wave " + waveManager.getCurrentWave() + "/10";
        font.draw(batch, waveStr, w - 150, h - 24);

        if (waveManager.isBetweenWaves()) {
            String msg = "Wave " + (waveManager.getCurrentWave() + 1) + " incoming...";
            font.setColor(Color.YELLOW);
            font.draw(batch, msg, w / 2f - 60, h / 2f + 50);
        }

        if (waveManager.isWaveInProgress() && waveManager.getCurrentWave() == 10 && !waveManager.isInfinite()) {
            font.setColor(Color.RED);
            font.draw(batch, "!! BOSS WAVE !!", w / 2f - 50, h - 24);
        }

        batch.end();
        
        renderControlsPanel();
    }

    private void renderControlsPanel() {
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

    public List<BoutonUI> getUpgradeButtons() { return upgradeButtons; }

    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
