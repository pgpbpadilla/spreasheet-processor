package org.pgpb.spreadsheet;

import com.google.common.collect.ImmutableList;
import org.pgpb.evaluation.ExpressionError;
import org.pgpb.evaluation.ExpressionEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.*;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class Spreadsheet {
    private static final Logger LOGGER = LoggerFactory.getLogger(Spreadsheet.class);
    private Dimensions dimensions;
    private Cell[][] cells;

    public Spreadsheet(int rows, int columns) {
        if (rows < 1 || columns < 1) {
            cells = new Cell[0][0];
        } else {
            cells = new Cell[rows][];
            for (int r = 0; r < rows; r++) {
                cells[r] = new Cell[columns];
            }
        }
    }

    private static Dimensions parseHeader(String[] header) {
        int rowCount = Integer.parseInt(header[0]);
        int columnCount = Integer.parseInt(header[1]);

        if (rowCount < 0 || columnCount < 0) {
            throw new RuntimeException("Invalid headers");
        }

        return new Dimensions(rowCount, columnCount);
    }

    public static Spreadsheet fromTsvLines(List<String> lines) {
        String[] header = lines.get(0).split("\\t");
        Dimensions dimensions = parseHeader(header);

        Cell [][] cells = lines.stream()
            .skip(1)
            .map(l -> l.split("\\t"))
            .map(Arrays::asList)
            .map(entries -> entries.stream()
                .map(Cell::new)
                .toArray(Cell[]::new))
            .toArray(Cell[][]::new);

        Spreadsheet spreadsheet = new Spreadsheet(dimensions.rows, dimensions.columns);
        spreadsheet.setCells(cells);
        return spreadsheet;
    }

    public int getRowCount() {
        return cells.length;
    }

    public int getColumnCount() {
        if (hasRows() && hasColumns()) {
            return cells[0].length;
        }
        return 0;
    }

    private boolean hasColumns() {
        return null != cells[0];
    }

    private boolean hasRows() {
        return cells.length > 0;
    }

    public Cell getCell(String address) {
        Coordinate coordinate = null;
        try {
            coordinate = Coordinate.fromAddress(address);
        } catch (Exception e) {
            return new Cell(
                ExpressionEvaluator.formatError(ExpressionError.INVALID_ADDRESS_FORMAT)
            );
        }
        if (!cellExists(coordinate)) {
            return new Cell(
                ExpressionEvaluator.formatError(ExpressionError.CELL_NOT_FOUND)
            );
        }
        return cells[coordinate.row][coordinate.column];
    }

    private boolean cellExists(Coordinate c) {
        if (!hasRows()) {
            return false;
        }
        if (!hasColumns()) {
            return false;
        }
        if (c.row > getRowCount()) {
            return false;
        }
        if (c.column > getColumnCount()) {
            return false;
        }
        return true;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public ImmutableList<String> toTSVLines() {
        ImmutableList lines = Arrays.asList(cells).stream()
            .map(Arrays::asList)
            .map(cellRow -> cellRow.stream()
                .map(Cell::getContent)
                .collect(joining("\t"))
            ).collect(ImmutableList.toImmutableList());
        return lines;
    }

    public ImmutableList<Cell[]> getRows() {
        return ImmutableList.copyOf(Arrays.asList(cells));
    }

    public static class Dimensions {
        public final int rows;
        public final int columns;

        public Dimensions(int rowCount, int columnCount) {
            rows = rowCount;
            columns = columnCount;
        }
    }
}
