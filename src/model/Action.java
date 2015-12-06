package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baris on 11/29/2015.
 * Action class
 */
public class Action {
    private StringSpan predicate;
    private List<Argument> arguments;
    private VerbSignature signature;
    private SemanticType semanticType;

    public Action() {
        this.arguments = new ArrayList<>();
        this.semanticType = SemanticType.OTHER;
    }

    public StringSpan getPredicate() {
        return predicate;
    }

    public void setPredicate(StringSpan predicate) {
        this.predicate = predicate;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

    public void setArguments(List<Argument> arguments) {
        this.arguments = arguments;
    }

    public VerbSignature getSignature() {
        return signature;
    }

    public void setSignature(VerbSignature signature) {
        this.signature = signature;
    }

    public SemanticType getSemanticType() {
        return semanticType;
    }

    public void setSemanticType(SemanticType semanticType) {
        this.semanticType = semanticType;
    }
}
