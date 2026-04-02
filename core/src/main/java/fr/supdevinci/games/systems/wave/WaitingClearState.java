package fr.supdevinci.games.systems.wave;

import com.badlogic.gdx.math.Vector2;

public class WaitingClearState implements WaveState {
    public static final WaitingClearState INSTANCE = new WaitingClearState();

    private WaitingClearState() {
    }

    @Override
    public WaveState update(WaveContext ctx, float delta, Vector2 playerPos, int aliveEnemyCount) {
        if (aliveEnemyCount > 0) {
            return this;
        }
        if (ctx.isFinalWave()) {
            ctx.markVictory();
            return VictoryState.INSTANCE;
        }

        ctx.prepareNextPause();
        return PauseState.INSTANCE;
    }

    @Override
    public String getName() { return "WAIT_FOR_CLEAR"; }
}
