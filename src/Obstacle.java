import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Obstacle extends Entity implements Schedules {
    public Obstacle( String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod, health, healthLimit);

    }
    public void scheduleActions( EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent( this, Functions.createAnimationAction(this, 0), this.getAnimationPeriod());
    }
}
