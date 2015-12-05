package model;

/**
 * Created by baris on 11/29/2015.
 * Semantic Types
 */
public enum SemanticType {
        FOOD,
        LOCATION,
        OTHER;

        @Override
        public String toString() {
                String str ="";
                switch (this){
                        case FOOD:
                                str="object";
                                break;
                        case LOCATION:
                                str="location";
                                break;
                        case OTHER:
                                str="other";
                                break;
                }
                return str;
        }
}
