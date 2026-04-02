package fr.supdevinci.games.systems.wave;

import fr.supdevinci.games.GameConfig;
import fr.supdevinci.games.Difficulty;
import fr.supdevinci.games.ecs.EnemyType;
import fr.supdevinci.games.systems.WaveManager.SpawnRequest;

import java.util.ArrayList;
import java.util.List;

public class WaveContext {
    public int currentWave = 0;
    public int remainingToSpawn = 0;
    public float spawnTimer = 0;
    public float pauseTimer = 2f;
    public boolean gameComplete = false;
    public boolean victory = false;
    public final Difficulty difficulty;
    public final List<SpawnRequest> pendingSpawns = new ArrayList<>();
    public final List<EnemyType> bossesToSpawnQueue = new ArrayList<>();

    public WaveContext(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void beginNextWave() {
        currentWave++;
        bossesToSpawnQueue.clear();
        queueBossesForCurrentWave();
        remainingToSpawn = calculateEnemyCount(currentWave) + bossesToSpawnQueue.size();
        spawnTimer = 0f;
    }

    public void prepareNextPause() {
        pauseTimer = GameConfig.WAVE_PAUSE_DURATION;
    }

    public void markVictory() {
        gameComplete = true;
        victory = true;
    }

    public boolean isFinalWave() {
        return difficulty != Difficulty.INFINITE && currentWave >= GameConfig.TOTAL_WAVES;
    }

    public int calculateEnemyCount(int wave) {
        int enemies = GameConfig.WAVE_BASE_ENEMIES + wave * GameConfig.WAVE_ENEMY_GROWTH;
        if (difficulty == Difficulty.HARD) {
            enemies = (int) (enemies * 1.5f);
        }
        return enemies;
    }

    private void queueBossesForCurrentWave() {
        if (currentWave % 10 != 0) return;

        int bossCount = currentWave / 10;
        for (int index = 1; index <= bossCount; index++) {
            if (index == 1) {
                bossesToSpawnQueue.add(EnemyType.BOSS);
            } else if (index == 2) {
                bossesToSpawnQueue.add(EnemyType.BOSS_2);
            } else {
                bossesToSpawnQueue.add(EnemyType.BOSS_3);
            }
        }
    }
}
