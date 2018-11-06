package org.pgpb.evaluation;

public enum ExpressionError {
    INVALID_FORMAT ("Invalid format."),
    NEGATIVE_NUMBER ("Only positive numbers are allowed."),
    INVALID_ADDRESS_FORMAT("Invalid cell address format."),
    CELL_NOT_FOUND ("Could not resolve cell address.");

    private final String message;

    ExpressionError(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return message;
    }
}
