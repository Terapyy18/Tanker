package fr.supdevinci.games.ecs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import fr.supdevinci.games.ui.BarreUI;

public class TankRenderer {
    private final Texture[] textures;
    private final BarreUI heavyChargeBar;
    private final BarreUI burstHeatBar;

    public TankRenderer(Texture[] textures) {
        this.textures = textures;
        this.heavyChargeBar = new BarreUI(new Color(0.3f, 0.1f, 0.1f, 1f), new Color(1.0f, 0.5f, 0.0f, 1f), 2.0f, 0.2f);
        this.burstHeatBar = new BarreUI(new Color(0.1f, 0.1f, 0.3f, 1f), new Color(0.0f, 1.0f, 1.0f, 1f), 2.0f, 0.2f);
    }

    public void render(SpriteBatch batch, Vector2 pos, float width, float height, float angle, int level) {
        int levelIndex = Math.min(level - 1, 2);
        Texture currentTex = textures[levelIndex];
        float drawAngle = angle - 90f;

        batch.draw(currentTex,
                pos.x - width / 2f, pos.y - height / 2f,
                width / 2f, height / 2f,
                width, height,
                1f, 1f,
                drawAngle,
                0, 0, currentTex.getWidth(), currentTex.getHeight(), false, false);
    }

    public void renderBars(ShapeRenderer renderer, Vector2 pos, float width, float height, TankCombat combat) {
        if (combat.isHeavyUnlocked()) {
            float pct = combat.getHeavyFireCooldown() > 0 ? 1f - (combat.getHeavyFireCooldown() / combat.getHeavyFireRate()) : 1f;
            heavyChargeBar.render(renderer, renderer.getProjectionMatrix(), pos.x, pos.y - height / 2f - 0.5f, pct);
        }

        if (combat.isBurstFireUnlocked()) {
            float pct = Math.min(1f, combat.getBurstActiveTimer() / 1.0f);
            // On peut créer une nouvelle BarreUI ou changer la couleur dynamiquement si nécessaire, 
            // mais ici on va simplifier en utilisant la barre existante.
            burstHeatBar.render(renderer, renderer.getProjectionMatrix(), pos.x, pos.y - height / 2f - (combat.isHeavyUnlocked() ? 0.8f : 0.5f), pct);
        }
    }
}
