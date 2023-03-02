import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Tree extends Entity implements Plant, Schedules, ActivityEntity {

    public Tree( String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod, health, healthLimit);
    }
    public void scheduleActions( EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent( this, Functions.createAnimationAction(this, 0), this.getAnimationPeriod());
    }
    public boolean transformPlant( WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getHealth() <= 0) {
            Stump stump = Functions.createStump(Functions.STUMP_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList( Functions.STUMP_KEY));
            world.removeEntity( scheduler, this);
            world.addEntity(stump);
            return true;
        }
        return false;
    }
    public void executeActivity( WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (!this.transformPlant( world, scheduler, imageStore)) {
            scheduler.scheduleEvent( this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
        }
    }
}
