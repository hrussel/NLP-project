package util;

import java.util.List;

/**
 * Created by baris on 12/10/2015.
 * Matches Strings given a Cluster of words
 */
public class StringMatcher {

    private List<String> cluster;

    public StringMatcher(List<String> cluster) {

        this.cluster = cluster;
    }

    public boolean isMatching(String word) {
        if(word.trim().isEmpty()) {
            return false;
        }
        String[] subWords = word.toLowerCase().split(" ");
        for (String clusterWord : cluster) {
            boolean matching = true;
            for (String subWord : subWords) {
                if (subWord.isEmpty() || subWord.matches(".*\\d+.*")) {
                    continue;
                }
                if (!clusterWord.toLowerCase().contains(subWord)) {
                    matching = false;
                    break;
                }
            }
            if (matching) {
                return true;
            }
        }
        return false;
    }
}
