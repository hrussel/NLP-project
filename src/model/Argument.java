package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baris on 11/29/2015.
 * Argument Class
 */
public class Argument {

    private SyntacticType syntacticType;
    private SemanticType semanticType;
    private List<StringSpan> words;

    public Argument() {
        this.words = new ArrayList<>();
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
        this.semanticType = semanticType;
    }

    public List<StringSpan> getWords() {
        return words;
    }

    public void setWords(List<StringSpan> words) {
        this.words = words;
    }
}
