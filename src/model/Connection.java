package model;

/**
 * Created by baris on 11/29/2015.
 * Connects a verb with a string span of an argument
 */
public class Connection {

    private StringSpan from;
    private Argument argument;
    private StringSpan to;

    public StringSpan getFrom() {
        return from;
    }

    public void setFrom(StringSpan from) {
        this.from = from;
    }

    public Argument getArgument() {
        return argument;
    }

    public void setArgument(Argument argument) {
        this.argument = argument;
    }

    public StringSpan getTo() {
        return to;
    }

    public void setTo(StringSpan to) {
        this.to = to;
    }
}
