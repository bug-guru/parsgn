package net.developithecus.parser2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public abstract class Group<T extends Group<T>> extends Rule {
    private List<Rule> ruleList = new ArrayList<>();

    public T addAll(Collection<Rule> ruleList) {
        this.ruleList.addAll(ruleList);
        return (T) this;
    }

    public T addAll(Rule... ruleList) {
        this.ruleList.addAll(Arrays.asList(ruleList));
        return (T) this;
    }

    public void add(Rule rule) {
        ruleList.add(rule);
    }
}
