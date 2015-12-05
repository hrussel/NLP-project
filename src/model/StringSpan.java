package model;

/**
 * Created by baris on 11/29/2015.
 * String span with a start and end point
 */
public class StringSpan {

    private String tId;
    private int start;
    private int end;
    private String baseWord;
    private String extras;


    //for implicit arguments
    public StringSpan() {
        this.baseWord = "";
        this.extras = "";
        this.start = -1;
        this.end = -1;
    }

    public StringSpan(String word, int start, int end) {
        this.baseWord = word;
        this.extras = "";
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
        if (baseWord.equals("")) {
            return extras;
        } else if (extras.equals("")) {
            return baseWord;
        } else {
            return baseWord + " " + extras;
        }
    }

    public void setWord(String word) {
        this.baseWord = word;
    }

    public String getBaseWord() {
        return baseWord;
    }

    public void setBaseWord(String baseWord) {
        this.baseWord = baseWord;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public void addExtra(String extra) {
        if (this.extras.equals("")) {
            this.extras = extra;
        } else {
            this.extras += " " + extra;
        }
    }
}
