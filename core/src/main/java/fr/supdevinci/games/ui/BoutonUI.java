package fr.supdevinci.games.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BoutonUI {
    private final String titre;
    private final String description;
    private float x;
    private float y;
    private float largeur;
    private float hauteur;
    private final Color fondCouleur;
    private final Color survolCouleur;
    private final Color texteCouleur;

    public BoutonUI(String titre, String description, float x, float y, float largeur, float hauteur) {
        this(
            titre,
            description,
            x,
            y,
            largeur,
            hauteur,
            new Color(0.2f, 0.2f, 0.4f, 1f),
            new Color(0.3f, 0.3f, 0.55f, 1f),
            Color.WHITE
        );
    }

    public BoutonUI(
        String titre,
        String description,
        float x,
        float y,
        float largeur,
        float hauteur,
        Color fondCouleur,
        Color survolCouleur,
        Color texteCouleur
    ) {
        this.titre = titre;
        this.description = description;
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.fondCouleur = new Color(fondCouleur);
        this.survolCouleur = new Color(survolCouleur);
        this.texteCouleur = new Color(texteCouleur);
    }

    public void setBounds(float x, float y, float largeur, float hauteur) {
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    public void render(ShapeRenderer sr, SpriteBatch batch, BitmapFont font, boolean hovered) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(hovered ? survolCouleur : fondCouleur);
        sr.rect(x, y, largeur, hauteur);
        sr.end();

        batch.begin();
        font.setColor(texteCouleur);
        font.getData().setScale(1.2f);
        float titleY = description.isEmpty() ? y + hauteur / 2f + 10f : y + hauteur - 20f;
        font.draw(batch, titre, x + 10, titleY);
        if (!description.isEmpty()) {
            font.getData().setScale(0.9f);
            font.draw(batch, description, x + 10, y + 40);
        }
        font.getData().setScale(1f);
        batch.end();
    }

    public boolean contient(float px, float py) {
        return px >= x && px <= x + largeur && py >= y && py <= y + hauteur;
    }
}
