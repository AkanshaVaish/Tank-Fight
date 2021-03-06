package ca.cmpt213.model;

/**
 * PlaceTank is the class which handles the most trickiest part of the assignment which is placing the tanks
 * When a new Tank object is created, an object of this class is made which places that particular tank on
 * the gameModel.
 * How does it work?
 * It has a list of all the unused cells which are basically free i.e. it doesn't contain any tank as of now
 * It randomly chooses any one of those cells and declare it as its first cell and start finding the rest of the cells
 * It has another list called freeNeighbours which starts adding the left, right, above and below neighbours
 * of the randomly selected cells.
 * Then rest of the cells are chosen from this list and it is updated everytime a cell is added.
 * By this way, we keep out old neighbours intact too.
 * @author vakansha, rmittal
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlaceTank {
    public static final int minimumCellsForTank = 4;
    public static final int completeTank = 4;

    private char nameOfTank;
    private Board field;
    private List<Point> pointsOfTank = new ArrayList<>();

    public PlaceTank(Board field, List<Cell> unusedCells, char nameOfTank) {
        this.nameOfTank = nameOfTank;
        this.field = field;

        if (placeItself(unusedCells)) {
            field.incrementNoOfAliveTank();
        }
    }

    //Working explained in JAVADOC for the class.
    public boolean placeItself(List<Cell> unusedCells) {
        List<Cell> freeNeighbours = new ArrayList<>();
        int cellsInTank = 0;
        int randCellIndex;
        Cell inTank;

        Random rand = new Random();

        do {
            if (unusedCells.size() == 0) {
                return false;
            }
            randCellIndex = rand.nextInt(unusedCells.size());
            inTank = unusedCells.get(randCellIndex);
            unusedCells.remove(randCellIndex);
            cellsInTank++;
            inTank.setOccupied(true);
            pointsOfTank.add(inTank.getLocationOfCell());
            fillFreeNeighbours (inTank, freeNeighbours);

            if (freeNeighbours.size() == 0) {
                //I didn't find any neighbour which is free which means I cannot use this particular cell for my tank
                //because it has no neighbour with whom I can connect.
                //Hence I will remove it from the list which contains all the cells which are unused because I cant use it.
                cellsInTank--;
                pointsOfTank.remove(inTank.getLocationOfCell());
                inTank.setOccupied(false);
            }
        } while((cellsInTank == 0) && !(unusedCells.size() < minimumCellsForTank));

        if ((cellsInTank == 0) && (unusedCells.size() < minimumCellsForTank)) {
            return false;
        }

        while ((cellsInTank != completeTank) && (freeNeighbours.size() != 0)) {
            randCellIndex = rand.nextInt(freeNeighbours.size());
            inTank = freeNeighbours.get(randCellIndex);
            unusedCells.remove(inTank);
            freeNeighbours.remove(inTank);
            inTank.setOccupied(true);
            cellsInTank++;
            pointsOfTank.add(inTank.getLocationOfCell());
            fillFreeNeighbours(inTank, freeNeighbours);
        }

        if (cellsInTank == completeTank) {
            for (Point point : pointsOfTank) {
                //System.out.println(this.nameOfTank + " " + (point.getRowNo()+1) + "," + (point.getColNo()+1));
                field.getCell(point).setName(this.nameOfTank);
            }
        }

        else if ((cellsInTank != completeTank) && (freeNeighbours.size() == 0)) {
            for (Point point : pointsOfTank) {
                field.getCell(point).setOccupied(false);
            }
            pointsOfTank.clear();
            if (unusedCells.size() != 0) {
                return placeItself(unusedCells);
            }
            else {
                return false;
            }
        }
        return true;
    }

    //Working explained in JAVADOC for the class.
    private void fillFreeNeighbours(Cell inTank, List<Cell> freeNeighbours) {
        Point currentPoint = inTank.getLocationOfCell();
        Point leftPoint = new Point(currentPoint.getRowNo(), currentPoint.getColNo()-1);
        Point rightPoint = new Point(currentPoint.getRowNo(), currentPoint.getColNo()+1);
        Point abovePoint = new Point(currentPoint.getRowNo()-1, currentPoint.getColNo());
        Point belowPoint = new Point(currentPoint.getRowNo()+1, currentPoint.getColNo());

        //Cases: I have to deal with all the cases where atRow or atColumn are going out of bound of the gameModel,
        //Case1: where the left cell is going out-of-bound
        //Case2: where the right cell is going out-of-bound
        //Case3: where the above cell is going out-of-bound
        //Case4: where the below cell is going out-of-bound

        if (field.locationExists(leftPoint)) {
            if (field.getCell(leftPoint).isEmpty()) {
                freeNeighbours.add(field.getCell(leftPoint));
            }
        }
        if (field.locationExists(rightPoint)) {
            if (field.getCell(rightPoint).isEmpty()) {
                freeNeighbours.add(field.getCell(rightPoint));
            }
        }
        if (field.locationExists(abovePoint)) {
            if (field.getCell(abovePoint).isEmpty()) {
                freeNeighbours.add(field.getCell(abovePoint));
            }
        }
        if (field.locationExists(belowPoint)) {
            if (field.getCell(belowPoint).isEmpty()) {
                freeNeighbours.add(field.getCell(belowPoint));
            }
        }
    }
}
