package fr.supdevinci.games.systems;

import fr.supdevinci.games.GameConfig;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LevelSystemTest {
    private LevelSystem levelSystem;
    private List<Integer> levelUpEvents;

    @Before
    public void setUp() {
        levelUpEvents = new ArrayList<Integer>();
        levelSystem = new LevelSystem(new LevelSystem.LevelUpListener() {
            @Override
            public void onLevelUp(int newLevel) {
                levelUpEvents.add(newLevel);
            }
        });
    }

    @Test
    public void testInitialState() {
        assertEquals(1, levelSystem.getLevel());
        assertEquals(0f, levelSystem.getCurrentExp(), 0.01f);
        assertEquals(GameConfig.BASE_EXP_TO_LEVEL, levelSystem.getExpToNextLevel(), 0.01f);
    }

    @Test
    public void testAddExpWithoutLevelUp() {
        levelSystem.addExp(50f);
        assertEquals(1, levelSystem.getLevel());
        assertEquals(50f, levelSystem.getCurrentExp(), 0.01f);
        assertTrue(levelUpEvents.isEmpty());
    }

    @Test
    public void testLevelUp() {
        levelSystem.addExp(GameConfig.BASE_EXP_TO_LEVEL);
        assertEquals(2, levelSystem.getLevel());
        assertEquals(0f, levelSystem.getCurrentExp(), 0.01f);
        assertEquals(1, levelUpEvents.size());
        assertEquals(Integer.valueOf(2), levelUpEvents.get(0));
    }

    @Test
    public void testLevelUpWithRemainder() {
        levelSystem.addExp(GameConfig.BASE_EXP_TO_LEVEL + 30f);
        assertEquals(2, levelSystem.getLevel());
        assertEquals(30f, levelSystem.getCurrentExp(), 0.01f);
    }

    @Test
    public void testMultipleLevelUps() {
        float expFor2Levels = GameConfig.BASE_EXP_TO_LEVEL +
                GameConfig.BASE_EXP_TO_LEVEL * GameConfig.EXP_GROWTH_FACTOR;
        levelSystem.addExp(expFor2Levels);
        assertEquals(3, levelSystem.getLevel());
        assertEquals(0f, levelSystem.getCurrentExp(), 0.5f);
        assertEquals(2, levelUpEvents.size());
        assertEquals(Integer.valueOf(2), levelUpEvents.get(0));
        assertEquals(Integer.valueOf(3), levelUpEvents.get(1));
    }

    @Test
    public void testExpFormulaGrowsExponentially() {
        float expLevel1 = levelSystem.calculateExpForLevel(1);
        float expLevel2 = levelSystem.calculateExpForLevel(2);
        float expLevel3 = levelSystem.calculateExpForLevel(3);

        assertEquals(GameConfig.BASE_EXP_TO_LEVEL, expLevel1, 0.01f);
        assertEquals(GameConfig.BASE_EXP_TO_LEVEL * GameConfig.EXP_GROWTH_FACTOR, expLevel2, 0.01f);
        assertTrue(expLevel3 > expLevel2);
        assertTrue(expLevel2 > expLevel1);

        float ratio1 = expLevel2 / expLevel1;
        float ratio2 = expLevel3 / expLevel2;
        assertEquals(ratio1, ratio2, 0.01f);
    }

    @Test
    public void testExpPercentage() {
        assertEquals(0f, levelSystem.getExpPercentage(), 0.01f);

        levelSystem.addExp(50f);
        assertEquals(0.5f, levelSystem.getExpPercentage(), 0.01f);
    }

    @Test
    public void testNullListener() {
        LevelSystem noListener = new LevelSystem(null);
        noListener.addExp(GameConfig.BASE_EXP_TO_LEVEL * 5);
        assertTrue(noListener.getLevel() > 1);
    }

    @Test
    public void testIncrementalExpGain() {
        for (int i = 0; i < 10; i++) {
            levelSystem.addExp(10f);
        }
        assertEquals(2, levelSystem.getLevel());
        assertEquals(1, levelUpEvents.size());
    }

    @Test
    public void testExpToNextLevelUpdatesAfterLevelUp() {
        float initialExpRequired = levelSystem.getExpToNextLevel();
        levelSystem.addExp(GameConfig.BASE_EXP_TO_LEVEL);

        float newExpRequired = levelSystem.getExpToNextLevel();
        assertTrue(newExpRequired > initialExpRequired);
        assertEquals(
                GameConfig.BASE_EXP_TO_LEVEL * GameConfig.EXP_GROWTH_FACTOR,
                newExpRequired, 0.01f);
    }
}
