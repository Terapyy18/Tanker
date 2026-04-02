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
        this.currentState = new PauseState();
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

        currentState.update(ctx, delta, playerPos, aliveEnemyCount);
        handleTransitions(aliveEnemyCount);
    }

    private void handleTransitions(int aliveEnemyCount) {
        if (currentState instanceof PauseState) {
            if (ctx.pauseTimer <= 0) {
                startNextWave();
                currentState = new SpawningState();
            }
        } else if (currentState instanceof SpawningState) {
            if (ctx.remainingToSpawn == 0) {
                currentState = new WaitingClearState();
            }
        } else if (currentState instanceof WaitingClearState) {
            if (aliveEnemyCount == 0) {
                if (ctx.difficulty != fr.supdevinci.games.Difficulty.INFINITE && ctx.currentWave >= fr.supdevinci.games.GameConfig.TOTAL_WAVES) {
                    currentState = new VictoryState();
                } else {
                    ctx.pauseTimer = fr.supdevinci.games.GameConfig.WAVE_PAUSE_DURATION;
                    currentState = new PauseState();
                }
            }
        }
    }

    private void startNextWave() {
        ctx.currentWave++;
        ctx.bossesToSpawnQueue.clear();
        if (ctx.currentWave % 10 == 0) {
            int numBosses = ctx.currentWave / 10;
            for (int i = 1; i <= numBosses; i++) {
                if (i == 1) ctx.bossesToSpawnQueue.add(EnemyType.BOSS);
                else if (i == 2) ctx.bossesToSpawnQueue.add(EnemyType.BOSS_2);
                else ctx.bossesToSpawnQueue.add(EnemyType.BOSS_3);
            }
        }

        ctx.remainingToSpawn = calculateEnemyCount(ctx.currentWave) + ctx.bossesToSpawnQueue.size();
        ctx.spawnTimer = 0;
    }

    public int calculateEnemyCount(int wave) {
        int enemies = fr.supdevinci.games.GameConfig.WAVE_BASE_ENEMIES + wave * fr.supdevinci.games.GameConfig.WAVE_ENEMY_GROWTH;
        if (ctx.difficulty == fr.supdevinci.games.Difficulty.HARD) {
            enemies = (int)(enemies * 1.5f);
        }
        return enemies;
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
}
