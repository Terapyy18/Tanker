package fr.supdevinci.games.systems;

import com.badlogic.gdx.math.Vector2;
import fr.supdevinci.games.Difficulty;
import fr.supdevinci.games.GameConfig;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WaveManagerTest {
    private static final Vector2 PLAYER_POSITION = new Vector2(100f, 75f);

    @Test
    public void startsFirstWaveWithoutSkippingWaveOne() {
        WaveManager manager = new WaveManager(Difficulty.NORMAL);

        manager.update(2.1f, PLAYER_POSITION, 0);

        assertEquals(1, manager.getCurrentWave());
        assertEquals("SPAWNING", manager.getCurrentStateName());
        assertTrue(manager.isWaveInProgress());
        assertTrue(manager.getPendingSpawns().isEmpty());
    }

    @Test
    public void returnsToPauseAfterWaveIsCleared() {
        WaveManager manager = new WaveManager(Difficulty.NORMAL);

        manager.update(2.1f, PLAYER_POSITION, 0);
        drainWaveSpawns(manager, 1);

        manager.update(0.1f, PLAYER_POSITION, 0);

        assertEquals("PAUSE", manager.getCurrentStateName());
        assertTrue(manager.isBetweenWaves());
        assertFalse(manager.isGameComplete());

        manager.update(GameConfig.WAVE_PAUSE_DURATION, PLAYER_POSITION, 0);

        assertEquals(2, manager.getCurrentWave());
        assertEquals("SPAWNING", manager.getCurrentStateName());
    }

    @Test
    public void reachesVictoryAfterLastWave() {
        WaveManager manager = new WaveManager(Difficulty.NORMAL);

        manager.update(2.1f, PLAYER_POSITION, 0);

        for (int wave = 1; wave <= GameConfig.TOTAL_WAVES; wave++) {
            drainWaveSpawns(manager, wave);
            manager.update(0.1f, PLAYER_POSITION, 0);

            if (wave < GameConfig.TOTAL_WAVES) {
                assertEquals("PAUSE", manager.getCurrentStateName());
                manager.update(GameConfig.WAVE_PAUSE_DURATION, PLAYER_POSITION, 0);
            }
        }

        assertEquals(GameConfig.TOTAL_WAVES, manager.getCurrentWave());
        assertEquals("VICTORY", manager.getCurrentStateName());
        assertTrue(manager.isGameComplete());
        assertTrue(manager.isVictory());
    }

    private void drainWaveSpawns(WaveManager manager, int waveNumber) {
        int expectedSpawnCount = manager.calculateEnemyCount(waveNumber) + bossCountForWave(waveNumber);
        int spawnedCount = 0;

        while (spawnedCount < expectedSpawnCount) {
            manager.update(GameConfig.SPAWN_INTERVAL, PLAYER_POSITION, 0);
            spawnedCount += manager.getPendingSpawns().size();
        }

        assertEquals(expectedSpawnCount, spawnedCount);
        assertEquals("WAIT_FOR_CLEAR", manager.getCurrentStateName());
    }

    private int bossCountForWave(int waveNumber) {
        return waveNumber % 10 == 0 ? waveNumber / 10 : 0;
    }
}
