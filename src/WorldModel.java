import processing.core.PImage;

import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel {
    private int numRows;
    private int numCols;
    private Background[][] background;
    private Entity[][] occupancy;

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    private Set<Entity> entities;

    public WorldModel() {

    }

    public void tryAddEntity( Entity entity) {
        if (this.isOccupied( entity.getPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        this.addEntity( entity);
    }

    private boolean withinBounds( Point pos) {
        return pos.y >= 0 && pos.y < this.numRows && pos.x >= 0 && pos.x < this.numCols;
    }

    public boolean isOccupied( Point pos) {
        return this.withinBounds(pos) && this.getOccupancyCell(pos) != null;
    }

    /*
           Assumes that there is no entity currently occupying the
           intended destination cell.
        */
    public void addEntity( Entity entity) {
        if (this.withinBounds( entity.getPosition())) {
            this.setOccupancyCell( entity.getPosition(), entity);
            this.entities.add(entity);
        }
    }

    public void moveEntity( EventScheduler scheduler, Entity entity, Point pos) {
        Point oldPos = entity.getPosition();
        if (this.withinBounds( pos) && !pos.equals(oldPos)) {
            this.setOccupancyCell( oldPos, null);
            Optional<Entity> occupant = this.getOccupant(pos);
            occupant.ifPresent(target -> this.removeEntity(scheduler, target));
            this.setOccupancyCell( pos, entity);
            entity.setPosition(pos);
        }
    }

    public void removeEntity( EventScheduler scheduler, Entity entity) {
        scheduler.unscheduleAllEvents(entity);
        this.removeEntityAt( entity.getPosition());
    }

    private  void removeEntityAt( Point pos) {
        if (this.withinBounds( pos) && this.getOccupancyCell( pos) != null) {
            Entity entity = this.getOccupancyCell( pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            this.entities.remove(entity);
            this.setOccupancyCell( pos, null);
        }
    }

    public Optional<Entity> getOccupant( Point pos) {
        if (this.isOccupied( pos)) {
            return Optional.of(this.getOccupancyCell( pos));
        } else {
            return Optional.empty();
        }
    }

    public Entity getOccupancyCell( Point pos) {
        return this.occupancy[pos.y][pos.x];
    }

    private void setOccupancyCell( Point pos, Entity entity) {
        this.occupancy[pos.y][pos.x] = entity;
    }

    //keep createEntity stuff
    public void load( Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        this.parseSaveFile( saveFile, imageStore, defaultBackground);
        if(this.background == null){
            this.background = new Background[this.numRows][this.numCols];
            for (Background[] row : this.background)
                Arrays.fill(row, defaultBackground);
        }
        if(this.occupancy == null){
            this.occupancy = new Entity[this.numRows][this.numCols];
            this.entities = new HashSet<>();
        }
    }

    private void parseSaveFile( Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        String lastHeader = "";
        int headerLine = 0;
        int lineCounter = 0;
        while(saveFile.hasNextLine()){
            lineCounter++;
            String line = saveFile.nextLine().strip();
            if(line.endsWith(":")){
                headerLine = lineCounter;
                lastHeader = line;
                switch (line){
                    case "Backgrounds:" -> this.background = new Background[this.numRows][this.numCols];
                    case "Entities:" -> {
                        this.occupancy = new Entity[this.numRows][this.numCols];
                        this.entities = new HashSet<>();
                    }
                }
            }else{
                switch (lastHeader){
                    case "Rows:" -> this.numRows = Integer.parseInt(line);
                    case "Cols:" -> this.numCols = Integer.parseInt(line);
                    case "Backgrounds:" -> this.parseBackgroundRow( line, lineCounter-headerLine-1, imageStore);
                    case "Entities:" -> Functions.parseEntity(this, line, imageStore);
                }
            }
        }
    }

    private void parseBackgroundRow( String line, int row, ImageStore imageStore) {
        String[] cells = line.split(" ");
        if(row < this.numRows){
            int rows = Math.min(cells.length, this.numCols);
            for (int col = 0; col < rows; col++){
                this.background[row][col] = new Background(cells[col], imageStore.getImageList( cells[col]));
            }
        }
    }

    private Background getBackgroundCell( Point pos) {
        return this.background[pos.y][pos.x];
    }

    private void setBackgroundCell( Point pos, Background background) {
        this.background[pos.y][pos.x] = background;
    }

    public  Optional<PImage> getBackgroundImage( Point pos) {
        if (this.withinBounds(pos)) {
            return Optional.of(this.getBackgroundCell( pos).getCurrentImage());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Helper method for testing. Don't move or modify this method.
     */
    public List<String> log(){
        List<String> list = new ArrayList<>();
        for (Entity entity : entities) {
            String log = entity.log();
            if(log != null) list.add(log);
        }
        return list;
    }
}
