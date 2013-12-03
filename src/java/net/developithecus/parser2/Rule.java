package net.developithecus.parser2;


/**
 *
 */
public abstract class Rule {
    private int minRepeats = 1;
    private int maxRepeats = 1;

    public int minRepeats() {
        return minRepeats;
    }

    public Rule requiredAtLeast(int min) {
        this.minRepeats = min;
        return this;
    }

    public Rule optional() {
        this.minRepeats = 0;
        return this;
    }

    public Rule required() {
        this.minRepeats = 1;
        return this;
    }

    public int maxRepeats() {
        return maxRepeats;
    }

    public Rule repeatNoMore(int max) {
        this.maxRepeats = max;
        return this;
    }

    public Rule repeat() {
        this.maxRepeats = Integer.MAX_VALUE;
        return this;
    }

    public static SequentialGroup sequence(Rule... rules) {
        return new SequentialGroup().addAll(rules);
    }

    public static ParallelGroup oneOf(Rule... rules) {
        return new ParallelGroup().addAll(rules);
    }

    public static StringRule string(String str) {
        return new StringRule().value(str);
    }

    public static ReferenceRule ref(Rule reference) {
        return new ReferenceRule().reference(reference);
    }

}
