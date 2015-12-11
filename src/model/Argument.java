package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baris on 11/29/2015.
 * Argument Class
 */
public class Argument {


    private List<StringSpan> words;
    private SyntacticType syntacticType;
    private SemanticType semanticType;
    private boolean semanticTypeFixed;

    public Argument() {
        this.words = new ArrayList<>();
        this.semanticType = SemanticType.OTHER;
        this.semanticTypeFixed = false;
    }

    public List<StringSpan> getWords() {
        return words;
    }

    public void setWords(List<StringSpan> words) {
        this.words = words;
    }

    public SyntacticType getSyntacticType() {
        return syntacticType;
    }

    public void setSyntacticType(SyntacticType syntacticType) {
        this.syntacticType = syntacticType;
    }

    public SemanticType getSemanticType() {
        return semanticType;
    }

    public void setSemanticType(SemanticType semanticType) {
        if(!semanticTypeFixed) {
            this.semanticType = semanticType;
        }
    }

    public boolean isSemanticTypeFixed() {
        return semanticTypeFixed;
    }

    public void setSemanticTypeFixed(boolean semanticTypeFixed) {
        this.semanticTypeFixed = semanticTypeFixed;
    }
}
