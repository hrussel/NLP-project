package model;

/**
 * Created by baris on 11/29/2015.
 * String span with a start and end point
 */
public class StringSpan {

    private String tId;
    private int start;
    private int end;
    private String word;


    //for implicit arguments
    public StringSpan() {
        this.word = "";
        this.start = -1;
        this.end = -1;
    }

    public StringSpan(String word, int start, int end) {
        this.word = word;
        this.start = start;
        this.end = end;
    }

    public String getTid() {
        return tId;
    }

    public void setTid(String tId) {
        this.tId = tId;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

}
