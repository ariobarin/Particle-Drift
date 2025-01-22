/*
 * name: Ario Barin Ostovary
 */

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Supplier;

public class MatrixEffect {
    private ArrayList<MatrixColumn> columns;
    private final Supplier<Integer> xOffsetSupplier;
    private final Supplier<Integer> widthSupplier;
    private final Supplier<Integer> heightSupplier;
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
            columns.add(new MatrixColumn(
                () -> columnIndex * widthSupplier.get() / numColumns, 
                heightSupplier
            ));
        }
    }

    public void update() {
        for (MatrixColumn column : columns) {
            column.update();
        }
    }

    public void draw(Graphics g) {
        int xOffset = xOffsetSupplier.get();
        for (MatrixColumn column : columns) {
            column.draw(g, xOffset);
        }
    }
}