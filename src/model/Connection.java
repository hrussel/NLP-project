package model;

/**
 * Created by baris on 11/29/2015.
 * Connects a verb with a string span of an argument
 */
public class Connection {

    private Action fromAction;
    private Argument toArgument;
    private Action toAction;
    private StringSpan toStringSpan;

    public Connection(Action fromAction, Action toAction, Argument toArgument, StringSpan toStringSpan) {
        this.fromAction = fromAction;
        this.toArgument = toArgument;
        this.toAction = toAction;
        this.toStringSpan = toStringSpan;
    }

    public Action getFromAction() {
        return fromAction;
    }

    public void setFromAction(Action fromAction) {
        this.fromAction = fromAction;
    }

    public Argument getToArgument() {
        return toArgument;
    }

    public void setToArgument(Argument toArgument) {
        this.toArgument = toArgument;
    }

    public Action getToAction() {
        return toAction;
    }

    public void setToAction(Action toAction) {
        this.toAction = toAction;
    }

    public StringSpan getToStringSpan() {
        return toStringSpan;
    }

    public void setToStringSpan(StringSpan toStringSpan) {
        this.toStringSpan = toStringSpan;
    }
}
