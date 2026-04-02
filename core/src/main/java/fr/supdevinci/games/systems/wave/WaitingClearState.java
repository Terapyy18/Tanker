package fr.supdevinci.games.systems.wave;

import com.badlogic.gdx.math.Vector2;

public class WaitingClearState implements WaveState {
    @Override
    public void update(WaveContext ctx, float delta, Vector2 playerPos, int aliveEnemyCount) {
        // Transition à gérer dans WaveManager : si aliveEnemyCount == 0, passer à PauseState ou VictoryState
    }

    @Override
    public String getName() { return "WAIT_FOR_CLEAR"; }
}
