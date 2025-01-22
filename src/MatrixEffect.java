/*
 * MatrixEffect.java
 * Ario Barin Ostovary
 * Class for creating a matrix effect for visual effects!
 */

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Supplier;

public class MatrixEffect {
    // list of columns in the matrix effect
    private ArrayList<MatrixColumn> columns;

    // suppliers for the x offset, width, and height of the matrix effect
    private final Supplier<Integer> xOffsetSupplier;
    private final Supplier<Integer> widthSupplier;
    private final Supplier<Integer> heightSupplier;

    // number of columns in the matrix effect
    private final int numColumns;

    public MatrixEffect(Supplier<Integer> xOffsetSupplier, Supplier<Integer> widthSupplier, 
                       Supplier<Integer> heightSupplier, int numColumns) {
        this.xOffsetSupplier = xOffsetSupplier;
        this.widthSupplier = widthSupplier;
        this.heightSupplier = heightSupplier;
        this.numColumns = numColumns;
        columns = new ArrayList<>(numColumns);
        init();
    }

    public void init() {
        columns.clear();
        for (int i = 0; i < numColumns; i++) {
            final int columnIndex = i;

            // create a new matrix column with the given suppliers - offsetted by the column index
            columns.add(new MatrixColumn(
                () -> columnIndex * widthSupplier.get() / numColumns, 
                heightSupplier
            ));
        }
    }

    public void update() {
        // update each column
        for (MatrixColumn column : columns) {
            column.update();
        }
    }

    public void draw(Graphics g) {
        // get the x offset of the matrix effect
        int xOffset = xOffsetSupplier.get();

        // draw each column
        for (MatrixColumn column : columns) {
            // draw the column with the supplied x offset
            column.draw(g, xOffset);
        }
    }
}