package fr.supdevinci.games.systems.wave;

import com.badlogic.gdx.math.Vector2;

public class VictoryState implements WaveState {
    public static final VictoryState INSTANCE = new VictoryState();

    private VictoryState() {
    }

    @Override
    public WaveState update(WaveContext ctx, float delta, Vector2 playerPos, int aliveEnemyCount) {
        ctx.markVictory();
        return this;
    }

    @Override
    public String getName() { return "VICTORY"; }
}
