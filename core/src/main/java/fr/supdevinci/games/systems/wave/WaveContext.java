package fr.supdevinci.games.systems.wave;

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
}
