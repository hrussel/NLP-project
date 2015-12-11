package model;

/**
 * Created by baris on 11/29/2015.
 * Syntactic Types
 */
public enum SyntacticType {
    DOBJ,
    PP;

    @Override
    public String toString() {
        switch (this){
            case DOBJ:
                return "DOBJ";
            case PP:
                return "PP";
        }
        return "";
    }
}
