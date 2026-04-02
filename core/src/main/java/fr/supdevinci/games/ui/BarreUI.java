package fr.supdevinci.games.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

public class BarreUI {
    private final Color fond;
    private final Color remplissage;
    private final float largeur;
    private final float hauteur;

    public BarreUI(Color fond, Color remplissage, float largeur, float hauteur) {
        this.fond = fond;
        this.remplissage = remplissage;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    public void render(ShapeRenderer sr, Matrix4 projection, float x, float y, float pct) {
        sr.setProjectionMatrix(projection);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        
        // Fond
        sr.setColor(fond);
        sr.rect(x - largeur / 2f, y, largeur, hauteur);
        
        // Remplissage
        sr.setColor(remplissage);
        sr.rect(x - largeur / 2f, y, largeur * pct, hauteur);
        
        sr.end();
    }
}
