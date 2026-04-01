package fr.supdevinci.games;

import com.badlogic.gdx.Game;
import fr.supdevinci.games.screens.MainMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }
}