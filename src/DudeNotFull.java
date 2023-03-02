import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class DudeNotFull extends Entity implements ActivityEntity, Schedules {

    public DudeNotFull( String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        super(id, position, images, resourceLimit, resourceCount, actionPeriod, animationPeriod, health, healthLimit);
    }

    public void scheduleActions( EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent( this, Functions.createAnimationAction(this, 0), this.getAnimationPeriod());
    }

    private boolean transformNotFull( WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getResourceCount() >= this.getResourceLimit()) {
            DudeFull dude = Functions.createDudeFull(this.getId(), this.getPosition(), this.getActionPeriod(), this.getAnimationPeriod(), this.getResourceLimit(), this.getImages());

            world.removeEntity( scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            dude.scheduleActions( scheduler, world, imageStore);

            return true;
        }

        return false;
    }


    private boolean moveToNotFull(WorldModel world, Entity target, EventScheduler scheduler) {
        if (Functions.adjacent(this.getPosition(), target.getPosition())) {
            this.increaseResourceCount(1);
            target.increaseHealth(-1);
            //target.health--;
            //target.resourceCount += 1;
            return true;
        } else {
            Point nextPos = this.nextPositionDude( world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity( scheduler, this, nextPos);
            }
            return false;
        }
    }
    private Point nextPositionDude( WorldModel world, Point destPos) {
        int horiz = Integer.signum(destPos.x - this.getPosition().x);
        Point newPos = new Point(this.getPosition().x + horiz, this.getPosition().y);
        if (horiz == 0 || world.isOccupied( newPos) && world.getOccupancyCell( newPos).getClass() != Stump.class) {
            int vert = Integer.signum(destPos.y - this.getPosition().y);
            newPos = new Point(this.getPosition().x, this.getPosition().y + vert);

            if (vert == 0 || world.isOccupied( newPos) && world.getOccupancyCell( newPos).getClass() != Stump.class) {
                newPos = this.getPosition();
            }
        }

        return newPos;
    }

    //help
    public void executeActivity( WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = Functions.findNearest(world, this.getPosition(), new ArrayList<>(Arrays.asList(Tree.class, Sapling.class)));

        if (target.isEmpty() || !this.moveToNotFull( world, target.get(), scheduler) || !this.transformNotFull( world, scheduler, imageStore)) {
            scheduler.scheduleEvent( this, Functions.createActivityAction(this, world, imageStore), this.getActionPeriod());
        }
    }
}
