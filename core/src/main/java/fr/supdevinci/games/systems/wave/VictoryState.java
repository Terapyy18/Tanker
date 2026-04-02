package fr.supdevinci.games.systems.wave;

import com.badlogic.gdx.math.Vector2;

public class VictoryState implements WaveState {
    @Override
    public void update(WaveContext ctx, float delta, Vector2 playerPos, int aliveEnemyCount) {
        // État terminal
        ctx.gameComplete = true;
        ctx.victory = true;
    }

    @Override
    public String getName() { return "VICTORY"; }
}
