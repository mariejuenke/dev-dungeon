package item.effects;

import com.badlogic.gdx.math.Vector2;
import contrib.components.HealthComponent;
import contrib.utils.components.health.Damage;
import contrib.utils.components.health.DamageType;
import core.Entity;
import core.components.PlayerComponent;
import core.components.VelocityComponent;
import core.utils.components.MissingComponentException;
import entities.DevHeroFactory;
import systems.EventScheduler;

import java.util.Vector;

/**
 * Provides a mechanism to apply a temporary speed increase effect to an entity within the game.
 * Utilizing the EffectScheduler, this effect increases the entity's speed for a designated duration
 * before reverting it back to its original state. The implementation relies on scheduling both the
 * application of the speed increase and its subsequent reversal.
 */
public class SpeedEffect {
  private static final EventScheduler EVENT_SCHEDULER = EventScheduler.getInstance();
  private final float speedIncrease;
  private final int duration;

  /**
   * Initializes a new instance of the SpeedEffect with a specified increase in speed and duration.
   *
   * @param speedIncrease The amount to increase the entity's speed by.
   * @param duration The duration, in seconds, for which the speed increase is applied.
   */
  public SpeedEffect(float speedIncrease, int duration) {
    this.speedIncrease = speedIncrease;
    this.duration = duration;
  }

  /**
   * Applies a temporary speed increase to the target entity, then reverts its speed to normal after
   * the specified duration. The increase in speed is applied immediately, and its reversal will be
   * scheduled to occur after the duration expires.
   *
   * <p>TODO: Implement the applySpeedEffect method to schedule the speed increase and its
   * reversion.
   *
   * @param target The entity to which the speed effect will be applied.
   */
  public void applySpeedEffect(Entity target) {
      if (target.fetch(PlayerComponent.class).isEmpty()) {
          throw new UnsupportedOperationException(
              "Movement speed can only be applied to player entities.");
      }

      VelocityComponent orig =  target.fetch(VelocityComponent.class).get();
      target.add(new VelocityComponent( orig.xVelocity() + speedIncrease,orig.yVelocity() + speedIncrease));

      EVENT_SCHEDULER.scheduleAction(
          () -> {
              VelocityComponent current =  target.fetch(VelocityComponent.class).get();
              VelocityComponent velocityComponent = new VelocityComponent(current.xVelocity() - speedIncrease,current.yVelocity() - speedIncrease);
              target.add(velocityComponent);
          }, this.duration * 1000L);

  }
  }

