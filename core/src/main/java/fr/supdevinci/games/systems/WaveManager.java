package fr.supdevinci.games.systems;

import com.badlogic.gdx.math.Vector2;
import fr.supdevinci.games.ecs.EnemyType;
import fr.supdevinci.games.systems.wave.*;
import java.util.ArrayList;
import java.util.List;

public class WaveManager {
    private final WaveContext ctx;
    private WaveState currentState;

    public WaveManager(fr.supdevinci.games.Difficulty difficulty) {
        this.ctx = new WaveContext(difficulty);
        this.currentState = PauseState.INSTANCE;
    }

    public static class SpawnRequest {
        public final EnemyType type;
        public final float x;
        public final float y;

        public SpawnRequest(EnemyType type, float x, float y) {
            this.type = type;
            this.x = x;
            this.y = y;
        }
    }

    public void update(float delta, Vector2 playerPos, int aliveEnemyCount) {
        if (ctx.gameComplete) return;

        currentState = currentState.update(ctx, delta, playerPos, aliveEnemyCount);
    }

    public int calculateEnemyCount(int wave) {
        return ctx.calculateEnemyCount(wave);
    }

    public List<SpawnRequest> getPendingSpawns() {
        List<SpawnRequest> spawns = new ArrayList<>(ctx.pendingSpawns);
        ctx.pendingSpawns.clear();
        return spawns;
    }

    public int getCurrentWave() { return ctx.currentWave; }
    public boolean isWaveInProgress() { return currentState instanceof SpawningState || currentState instanceof WaitingClearState; }
    public boolean isGameComplete() { return ctx.gameComplete; }
    public boolean isVictory() { return ctx.victory; }
    public boolean isBetweenWaves() { return currentState instanceof PauseState && ctx.currentWave > 0; }
    public float getPauseTimer() { return ctx.pauseTimer; }
    public boolean isInfinite() { return ctx.difficulty == fr.supdevinci.games.Difficulty.INFINITE; }
    public String getCurrentStateName() { return currentState.getName(); }
}
