import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Fairy extends Entity implements Schedules, ActivityEntity {
    public Fairy( String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod, health, healthLimit);
    }
    public void scheduleActions( EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
                scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
                scheduler.scheduleEvent( this, Functions.createAnimationAction(this, 0), this.getAnimationPeriod());
    }

    private boolean moveToFairy(WorldModel world, Entity target, EventScheduler scheduler) {
        if (Functions.adjacent(this.getPosition(), target.getPosition())) {
            world.removeEntity( scheduler, target);
            return true;
        } else {
            Point nextPos = this.nextPositionFairy( world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity( scheduler, this, nextPos);
            }
            return false;
        }
    }

    private Point nextPositionFairy( WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - this.getPosition().x);
        Point newPos = new Point(this.getPosition().x + horiz, this.getPosition().y);

        if (horiz == 0 || world.isOccupied( newPos)) {
            int vert = Integer.signum(destPos.y - this.getPosition().y);
            newPos = new Point(this.getPosition().x, this.getPosition().y + vert);

            if (vert == 0 || world.isOccupied( newPos)) {
                newPos = this.getPosition();
            }
        }
        return newPos;
    }
    //scheduleActions and getAnimationPeriod

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = Functions.findNearest(world, this.getPosition(), new ArrayList<>(List.of(Stump.class)));
//        Optional<Fairy> fairyTarget = Functions.findNearest(world, this.getPosition(), new ArrayList<>(List.of(EntityKind.STUMP)));
// used to be that
        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (this.moveToFairy( world, fairyTarget.get(), scheduler)) {

                Sapling sapling = Functions.createSapling(Functions.SAPLING_KEY + "_" + fairyTarget.get().getId(), tgtPos, imageStore.getImageList( Functions.SAPLING_KEY), 0);

                world.addEntity(sapling);
                sapling.scheduleActions( scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
    }
}
