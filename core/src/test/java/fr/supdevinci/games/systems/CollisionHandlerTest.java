package fr.supdevinci.games.systems;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import fr.supdevinci.games.ecs.Bullet;
import fr.supdevinci.games.ecs.Enemy;
import fr.supdevinci.games.ecs.EnemyType;
import fr.supdevinci.games.ecs.ExpOrb;
import fr.supdevinci.games.ecs.Tank;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class CollisionHandlerTest {
    private CollisionListener listener;
    private CollisionHandler handler;

    @Before
    public void setUp() {
        listener = mock(CollisionListener.class);
        handler = new CollisionHandler(listener);
    }

    @Test
    public void bulletHitsDamageableTarget() {
        Bullet bullet = new Bullet(null, 25f, true, false);
        Enemy enemy = new Enemy(null, EnemyType.BASIC);

        handler.beginContact(createContact(bullet, enemy));

        verify(listener).onBulletHit(bullet, enemy);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void bulletHitsWall() {
        Bullet bullet = new Bullet(null, 10f, false, false);

        handler.beginContact(createContact(bullet, null));

        verify(listener).onBulletHitWall(bullet);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void contactDamageUsesCollisionRoles() {
        Enemy enemy = new Enemy(null, EnemyType.HEAVY);
        Tank tank = new Tank(null);

        handler.beginContact(createContact(enemy, tank));

        verify(listener).onContactDamage(enemy, tank);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void collectibleCollisionUsesGenericCollectorContract() {
        Tank tank = new Tank(null);
        ExpOrb orb = new ExpOrb(null, 25);

        handler.beginContact(createContact(tank, orb));

        verify(listener).onCollected(tank, orb);
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void unrelatedEntitiesDoNotTriggerCallbacks() {
        Enemy firstEnemy = new Enemy(null, EnemyType.BASIC);
        Enemy secondEnemy = new Enemy(null, EnemyType.FAST);

        handler.beginContact(createContact(firstEnemy, secondEnemy));

        verifyNoInteractions(listener);
    }

    private Contact createContact(Object userDataA, Object userDataB) {
        Contact contact = mock(Contact.class);
        Fixture fixtureA = mock(Fixture.class);
        Fixture fixtureB = mock(Fixture.class);
        Body bodyA = mock(Body.class);
        Body bodyB = mock(Body.class);

        when(contact.getFixtureA()).thenReturn(fixtureA);
        when(contact.getFixtureB()).thenReturn(fixtureB);
        when(fixtureA.getBody()).thenReturn(bodyA);
        when(fixtureB.getBody()).thenReturn(bodyB);
        when(bodyA.getUserData()).thenReturn(userDataA);
        when(bodyB.getUserData()).thenReturn(userDataB);

        return contact;
    }
}
