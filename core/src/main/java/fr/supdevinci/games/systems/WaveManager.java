package fr.supdevinci.games.systems;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import fr.supdevinci.games.GameConfig;
import fr.supdevinci.games.ecs.EnemyType;
import java.util.ArrayList;
import java.util.List;

public class WaveManager {
    private int currentWave = 0;
    private boolean waveInProgress = false;
    private boolean gameComplete = false;
    private boolean victory = false;
    private int remainingToSpawn = 0;
    private float spawnTimer = 0;
    private float pauseTimer = 2f; // initial pause before wave 1
    private boolean bossSpawned = false;

    private final List<SpawnRequest> pendingSpawns = new ArrayList<SpawnRequest>();

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
        if (gameComplete) return;

        if (waveInProgress) {
            if (remainingToSpawn > 0) {
                spawnTimer -= delta;
                if (spawnTimer <= 0) {
                    spawnNext(playerPos);
                    spawnTimer = GameConfig.SPAWN_INTERVAL;
                }
            } else if (aliveEnemyCount == 0) {
                // Wave complete
                waveInProgress = false;
                if (currentWave >= GameConfig.TOTAL_WAVES) {
                    gameComplete = true;
                    victory = true;
                } else {
                    pauseTimer = GameConfig.WAVE_PAUSE_DURATION;
                }
            }
        } else {
            pauseTimer -= delta;
            if (pauseTimer <= 0) {
                startNextWave();
            }
        }
    }

    private void startNextWave() {
        currentWave++;
        waveInProgress = true;
        bossSpawned = false;
        remainingToSpawn = calculateEnemyCount(currentWave);
        spawnTimer = 0;
    }

    private void spawnNext(Vector2 playerPos) {
        EnemyType type = chooseType();
        float angle = MathUtils.random(360f);
        float rad = angle * MathUtils.degreesToRadians;
        float x = playerPos.x + GameConfig.SPAWN_DISTANCE * MathUtils.cos(rad);
        float y = playerPos.y + GameConfig.SPAWN_DISTANCE * MathUtils.sin(rad);
        x = MathUtils.clamp(x, 5f, GameConfig.MAP_WIDTH - 5f);
        y = MathUtils.clamp(y, 5f, GameConfig.MAP_HEIGHT - 5f);
        pendingSpawns.add(new SpawnRequest(type, x, y));
        remainingToSpawn--;
    }

    private EnemyType chooseType() {
        if (currentWave == GameConfig.TOTAL_WAVES && !bossSpawned) {
            bossSpawned = true;
            return EnemyType.BOSS;
        }

        List<EnemyType> available = new ArrayList<EnemyType>();
        available.add(EnemyType.BASIC);
        if (currentWave >= 3) available.add(EnemyType.FAST);
        if (currentWave >= 5) available.add(EnemyType.HEAVY);
        if (currentWave >= 7) available.add(EnemyType.SHOOTER);
        return available.get(MathUtils.random(available.size() - 1));
    }

    public int calculateEnemyCount(int wave) {
        if (wave == GameConfig.TOTAL_WAVES) {
            return 1 + 5; // 1 boss + 5 minions
        }
        return GameConfig.WAVE_BASE_ENEMIES + wave * GameConfig.WAVE_ENEMY_GROWTH;
    }

    public List<SpawnRequest> getPendingSpawns() {
        List<SpawnRequest> spawns = new ArrayList<SpawnRequest>(pendingSpawns);
        pendingSpawns.clear();
        return spawns;
    }

    public int getCurrentWave() { return currentWave; }
    public boolean isWaveInProgress() { return waveInProgress; }
    public boolean isGameComplete() { return gameComplete; }
    public boolean isVictory() { return victory; }
    public boolean isBetweenWaves() { return !waveInProgress && !gameComplete && currentWave > 0; }
    public float getPauseTimer() { return pauseTimer; }

    public List<EnemyType> getAvailableTypes(int wave) {
        List<EnemyType> available = new ArrayList<EnemyType>();
        available.add(EnemyType.BASIC);
        if (wave >= 3) available.add(EnemyType.FAST);
        if (wave >= 5) available.add(EnemyType.HEAVY);
        if (wave >= 7) available.add(EnemyType.SHOOTER);
        return available;
    }
}
