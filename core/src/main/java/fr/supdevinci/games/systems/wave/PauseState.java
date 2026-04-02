package fr.supdevinci.games.systems.wave;

import com.badlogic.gdx.math.Vector2;

public class PauseState implements WaveState {
    public static final PauseState INSTANCE = new PauseState();

    private PauseState() {
    }

    @Override
    public WaveState update(WaveContext ctx, float delta, Vector2 playerPos, int aliveEnemyCount) {
        ctx.pauseTimer -= delta;
        if (ctx.pauseTimer > 0f) {
            return this;
        }

        ctx.beginNextWave();
        return SpawningState.INSTANCE;
    }

    @Override
    public String getName() { return "PAUSE"; }
}
