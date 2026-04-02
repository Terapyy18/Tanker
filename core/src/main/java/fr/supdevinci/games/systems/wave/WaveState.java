package fr.supdevinci.games.systems.wave;

import com.badlogic.gdx.math.Vector2;

public interface WaveState {
    WaveState update(WaveContext ctx, float delta, Vector2 playerPos, int aliveEnemyCount);
    String getName();
}
