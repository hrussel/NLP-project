package model;

import java.util.Collections;
import java.util.Set;

/**
 * Created by baris on 12/5/2015.
 * Verb Signature Model
 */
public class VerbSignature {
    private Set<SyntacticType> syntacticTypeSet;
    private boolean leaf;

    public VerbSignature(Set<SyntacticType> syntacticTypeSet, boolean leaf) {
        this.syntacticTypeSet = syntacticTypeSet;
        this.leaf = leaf;
    }

    public Set<SyntacticType> getSyntacticTypeSet() {
        return syntacticTypeSet;
    }

    public void setSyntacticTypeSet(Set<SyntacticType> syntacticTypeSet) {
        this.syntacticTypeSet = syntacticTypeSet;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof VerbSignature && ((VerbSignature) o).getSyntacticTypeSet().equals(this.syntacticTypeSet) && ((VerbSignature) o).isLeaf() == this.leaf;
    }

    @Override
    public int hashCode() {
        int leafInt = 0;
        if(leaf){
            leafInt = 1;
        }
        return 37* leafInt + 71 * syntacticTypeSet.hashCode();
    }
}
