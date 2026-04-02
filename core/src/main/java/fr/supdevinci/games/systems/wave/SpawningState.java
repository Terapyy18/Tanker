package fr.supdevinci.games.systems.wave;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import fr.supdevinci.games.GameConfig;
import fr.supdevinci.games.ecs.EnemyType;
import fr.supdevinci.games.systems.WaveManager.SpawnRequest;

import java.util.ArrayList;
import java.util.List;

public class SpawningState implements WaveState {
    @Override
    public void update(WaveContext ctx, float delta, Vector2 playerPos, int aliveEnemyCount) {
        if (ctx.remainingToSpawn > 0) {
            ctx.spawnTimer -= delta;
            if (ctx.spawnTimer <= 0) {
                spawnNext(ctx, playerPos);
                ctx.spawnTimer = GameConfig.SPAWN_INTERVAL;
            }
        }
        // Transition à gérer dans WaveManager : si remainingToSpawn == 0, passer à WaitingClearState
    }

    private void spawnNext(WaveContext ctx, Vector2 playerPos) {
        EnemyType type = chooseType(ctx);
        float angle = MathUtils.random(360f);
        float rad = angle * MathUtils.degreesToRadians;
        float x = playerPos.x + GameConfig.SPAWN_DISTANCE * MathUtils.cos(rad);
        float y = playerPos.y + GameConfig.SPAWN_DISTANCE * MathUtils.sin(rad);
        x = MathUtils.clamp(x, 5f, GameConfig.MAP_WIDTH - 5f);
        y = MathUtils.clamp(y, 5f, GameConfig.MAP_HEIGHT - 5f);
        ctx.pendingSpawns.add(new SpawnRequest(type, x, y));
        ctx.remainingToSpawn--;
    }

    private EnemyType chooseType(WaveContext ctx) {
        if (!ctx.bossesToSpawnQueue.isEmpty()) {
            return ctx.bossesToSpawnQueue.remove(0);
        }

        List<EnemyType> available = new ArrayList<>();
        available.add(EnemyType.BASIC);
        if (ctx.currentWave >= 3) available.add(EnemyType.FAST);
        if (ctx.currentWave >= 5) available.add(EnemyType.HEAVY);
        if (ctx.currentWave >= 7) available.add(EnemyType.SHOOTER);
        return available.get(MathUtils.random(available.size() - 1));
    }

    @Override
    public String getName() { return "SPAWNING"; }
}
