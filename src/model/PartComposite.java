package model;

/**
 * Created by Helena Russello on 05-12-15.
 */
public class PartComposite {

    private SemanticType semanticType;
    private boolean hasOrigin;

    public PartComposite(SemanticType semanticType, boolean hasOrigin) {
        this.semanticType = semanticType;
        this.hasOrigin = hasOrigin;
    }

    public SemanticType getSemanticType() {
        return semanticType;
    }

    public void setSemanticType(SemanticType semanticType) {
        this.semanticType = semanticType;
    }

    public boolean isHasOrigin() {
        return hasOrigin;
    }

    public void setHasOrigin(boolean hasOrigin) {
        this.hasOrigin = hasOrigin;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PartComposite && this.semanticType == ((PartComposite) obj).semanticType && this.hasOrigin == ((PartComposite) obj).hasOrigin;
    }
}
