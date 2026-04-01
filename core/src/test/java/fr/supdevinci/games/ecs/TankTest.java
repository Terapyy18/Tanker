package fr.supdevinci.games.ecs;

import fr.supdevinci.games.GameConfig;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TankTest {
    private Tank tank;

    @Before
    public void setUp() {
        // Create tank with null body for pure unit testing of stats
        tank = new Tank(null);
    }

    @Test
    public void testInitialStats() {
        assertEquals(GameConfig.PLAYER_MAX_HEALTH, tank.getHealth(), 0.01f);
        assertEquals(GameConfig.PLAYER_MAX_HEALTH, tank.getMaxHealth(), 0.01f);
        assertEquals(GameConfig.PLAYER_SPEED, tank.getSpeed(), 0.01f);
        assertEquals(GameConfig.PLAYER_DAMAGE, tank.getDamage(), 0.01f);
        assertEquals(GameConfig.PLAYER_FIRE_RATE, tank.getFireRate(), 0.01f);
        assertEquals(0f, tank.getArmor(), 0.01f);
        assertTrue(tank.isAlive());
    }

    @Test
    public void testTakeDamage() {
        float initialHealth = tank.getHealth();
        tank.takeDamage(20f);
        assertEquals(initialHealth - 20f, tank.getHealth(), 0.01f);
        assertTrue(tank.isAlive());
    }

    @Test
    public void testTakeDamageWithArmor() {
        // Apply one level-up to gain armor
        tank.applyLevelUp(2);
        float armorBefore = tank.getArmor();
        assertTrue(armorBefore > 0);

        float healthBefore = tank.getHealth();
        tank.takeDamage(50f);
        float expectedDamage = 50f * (1f - armorBefore);
        assertEquals(healthBefore - expectedDamage, tank.getHealth(), 0.5f);
    }

    @Test
    public void testTakeFatalDamage() {
        tank.takeDamage(1000f);
        assertEquals(0f, tank.getHealth(), 0.01f);
        assertFalse(tank.isAlive());
    }

    @Test
    public void testTakeExactLethalDamage() {
        tank.takeDamage(GameConfig.PLAYER_MAX_HEALTH);
        assertEquals(0f, tank.getHealth(), 0.01f);
        assertFalse(tank.isAlive());
    }

    @Test
    public void testCanFireInitially() {
        assertTrue(tank.canFire());
    }

    @Test
    public void testFireCooldown() {
        tank.resetFireCooldown();
        // Tank should not be able to fire immediately after resetting cooldown
        // canFire checks fireCooldown <= 0; after reset, fireCooldown = fireRate > 0
        assertFalse(tank.canFire());
    }

    @Test
    public void testApplyLevelUpIncreasesStats() {
        float prevMaxHealth = tank.getMaxHealth();
        float prevSpeed = tank.getSpeed();
        float prevDamage = tank.getDamage();
        float prevFireRate = tank.getFireRate();
        float prevArmor = tank.getArmor();

        tank.applyLevelUp(2);

        assertTrue(tank.getMaxHealth() > prevMaxHealth);
        assertTrue(tank.getSpeed() > prevSpeed);
        assertTrue(tank.getDamage() > prevDamage);
        assertTrue(tank.getFireRate() < prevFireRate); // fire rate decreases (faster)
        assertTrue(tank.getArmor() > prevArmor);
    }

    @Test
    public void testLevelUpFullHeals() {
        tank.takeDamage(50f);
        assertTrue(tank.getHealth() < tank.getMaxHealth());

        tank.applyLevelUp(2);
        assertEquals(tank.getMaxHealth(), tank.getHealth(), 0.01f);
    }

    @Test
    public void testMultipleLevelUps() {
        float initialMaxHealth = tank.getMaxHealth();
        tank.applyLevelUp(2);
        tank.applyLevelUp(3);
        tank.applyLevelUp(4);

        // After 3 level-ups, maxHealth should have increased 3 times
        float expected = initialMaxHealth
            * (1f + GameConfig.LEVEL_HEALTH_BONUS)
            * (1f + GameConfig.LEVEL_HEALTH_BONUS)
            * (1f + GameConfig.LEVEL_HEALTH_BONUS);
        assertEquals(expected, tank.getMaxHealth(), 0.5f);
    }

    @Test
    public void testArmorCap() {
        // Apply many level-ups to test armor never exceeds 0.75
        for (int i = 0; i < 50; i++) {
            tank.applyLevelUp(i + 2);
        }
        assertTrue(tank.getArmor() <= 0.75f);
    }

    @Test
    public void testDeadTankCannotFire() {
        tank.takeDamage(1000f);
        assertFalse(tank.isAlive());
        assertFalse(tank.canFire());
    }

    @Test
    public void testSetAngle() {
        tank.setAngle(45f);
        assertEquals(45f, tank.getAngle(), 0.01f);
        tank.setAngle(-90f);
        assertEquals(-90f, tank.getAngle(), 0.01f);
    }
}
