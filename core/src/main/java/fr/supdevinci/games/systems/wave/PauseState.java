package fr.supdevinci.games.systems.wave;

import com.badlogic.gdx.math.Vector2;

public class PauseState implements WaveState {
    @Override
    public void update(WaveContext ctx, float delta, Vector2 playerPos, int aliveEnemyCount) {
        ctx.pauseTimer -= delta;
        if (ctx.pauseTimer <= 0) {
            // Passer à l'état de spawn
            ctx.currentWave++;
            ctx.remainingToSpawn = calculateEnemyCount(ctx) + ctx.bossesToSpawnQueue.size();
            // Note: On changerait l'état dans le WaveManager
        }
    }

    private int calculateEnemyCount(WaveContext ctx) {
        int base = 10 + ctx.currentWave * 2; // Exemple, on ajustera avec les vraies constantes
        if (ctx.difficulty == fr.supdevinci.games.Difficulty.HARD) base *= 1.5f;
        return base;
    }

    @Override
    public String getName() { return "PAUSE"; }
}
