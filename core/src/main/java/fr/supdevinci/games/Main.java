package fr.supdevinci.games;

import com.badlogic.gdx.Game;
import fr.supdevinci.games.screens.MainMenuScreen;

/** Implémentation du {@link com.badlogic.gdx.ApplicationListener} partagée par toutes les plateformes. */
public class Main extends Game {
    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }
}