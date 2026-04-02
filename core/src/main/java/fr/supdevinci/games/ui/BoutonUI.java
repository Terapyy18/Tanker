package fr.supdevinci.games.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BoutonUI {
    private final String titre;
    private final String description;
    private final float x, y, largeur, hauteur;
    private final Color fondCouleur = new Color(0.2f, 0.2f, 0.4f, 1f);

    public BoutonUI(String titre, String description, float x, float y, float largeur, float hauteur) {
        this.titre = titre;
        this.description = description;
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    public void render(ShapeRenderer sr, SpriteBatch batch, BitmapFont font) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(fondCouleur);
        sr.rect(x, y, largeur, hauteur);
        sr.end();

        batch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);
        font.draw(batch, titre, x + 10, y + hauteur - 20);
        font.getData().setScale(0.9f);
        font.draw(batch, description, x + 10, y + 40);
        batch.end();
    }

    public boolean contient(float px, float py) {
        return px >= x && px <= x + largeur && py >= y && py <= y + hauteur;
    }
}
