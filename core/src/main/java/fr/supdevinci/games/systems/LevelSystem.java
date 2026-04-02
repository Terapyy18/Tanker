package fr.supdevinci.games.systems;

import fr.supdevinci.games.GameConfig;

public class LevelSystem {
    private int currentLevel = 1;
    private float currentExp = 0;
    private float expToNextLevel;
    private final LevelUpListener listener;

    public interface LevelUpListener {
        void onLevelUp(int newLevel);
    }

    public LevelSystem(LevelUpListener listener) {
        this.listener = listener;
        this.expToNextLevel = GameConfig.BASE_EXP_TO_LEVEL;
    }

    public void addExp(float amount) {
        currentExp += amount;
        while (currentExp >= expToNextLevel) {
            currentExp -= expToNextLevel;
            currentLevel++;
            expToNextLevel = calculateExpForLevel(currentLevel);
            if (listener != null) {
                listener.onLevelUp(currentLevel);
            }
        }
    }

    public float calculateExpForLevel(int level) {
        return GameConfig.BASE_EXP_TO_LEVEL * (float) Math.pow(GameConfig.EXP_GROWTH_FACTOR, level - 1);
    }

    public int getLevel() {
        return currentLevel;
    }

    public float getCurrentExp() {
        return currentExp;
    }

    public float getExpToNextLevel() {
        return expToNextLevel;
    }

    public float getExpPercentage() {
        return currentExp / expToNextLevel;
    }
}
