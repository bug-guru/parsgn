package net.developithecus.parser2;

/**
 *
 */
public class StringRule extends Rule {
    private String value;

    public String value() {
        return value;
    }

    public StringRule value(String value) {
        this.value = value;
        return this;
    }
}
