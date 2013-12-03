/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser;

import net.developithecus.parser.definitions.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class Builder {

    private final Map<String, Rule> rules = new HashMap<String, Rule>();

    // RULE
    public Rule rule(String name, AbstractDefinition definition) throws RuleExistsException {
        if (rules.containsKey(name)) {
            throw new RuleExistsException(name);
        }
        Rule result = new Rule(name);
        result.definition(definition);
        rules.put(name, result);
        return result;
    }

    public AbstractDefinition ref(String ruleName) {
        return new RuleRefDefinition(ruleName, this);
    }

    public Rule getRule(String name) throws RuleNotFoundException {
        Rule result = rules.get(name);
        if (result == null) {
            throw new RuleNotFoundException(name);
        }
        return result;
    }

    // char
    public CharDefinition chars() {
        return chars(null);
    }

    public CharDefinition chars(String include) {
        CharDefinition d = new CharDefinition();
        if (include == null) {
            d.includeAll();
        } else {
            d.include(include);
        }
        return d;
    }

    // char of
    public CharOfDefinition charof(AbstractDefinition def) {
        return charof(null, def);
    }

    public CharOfDefinition charof(String include, AbstractDefinition def) {
        CharOfDefinition result = new CharOfDefinition();
        if (include == null) {
            result.includeAll();
        } else {
            result.include(include);
        }
        result.definition(def);
        return result;
    }

    // compact
    public CompactDefinition compact() {
        return compact(null);
    }

    public CompactDefinition compact(AbstractDefinition definition) {
        CompactDefinition d = new CompactDefinition();
        d.definition(definition);
        return d;
    }

    // convert to
    public ConvertToDefinition convertto(AbstractDefinition def, String to) {
        ConvertToDefinition result = new ConvertToDefinition();
        result.definition(def);
        result.to(to);
        return result;
    }

    // eof
    public EofDefinition eof() {
        EofDefinition d = new EofDefinition();
        return d;
    }

    // one of
    public OneOfDefinition oneof(AbstractDefinition... defs) {
        OneOfDefinition result = new OneOfDefinition();
        for (AbstractDefinition d : defs) {
            result.add(d);
        }
        return result;
    }

    // optional
    public OptionalDefinition optional() {
        return optional(null);
    }

    public OptionalDefinition optional(AbstractDefinition definition) {
        OptionalDefinition d = new OptionalDefinition();
        d.definition(definition);
        return d;
    }

    // reapply
    public ReapplyDefinition reapply() {
        return reapply(null);
    }

    public ReapplyDefinition reapply(AbstractDefinition definition) {
        ReapplyDefinition b = new ReapplyDefinition();
        b.definition(definition);
        return b;
    }

    // repeat
    public RepeatDefinition repeat(AbstractDefinition def) {
        RepeatDefinition result = new RepeatDefinition();
        result.definition(def);
        return result;
    }

    // sequence
    public SequenceDefinition sequence(AbstractDefinition... defs) {
        SequenceDefinition result = new SequenceDefinition();
        for (AbstractDefinition d : defs) {
            result.add(d);
        }
        return result;
    }

    // sequence
    public SetOfDefinition setof(AbstractDefinition... defs) {
        SetOfDefinition result = new SetOfDefinition();
        for (AbstractDefinition d : defs) {
            result.add(d);
        }
        return result;
    }

    // skip
    public IgnoreDefinition ignore() {
        IgnoreDefinition d = new IgnoreDefinition();
        return d;
    }

    public IgnoreDefinition ignore(AbstractDefinition def) {
        IgnoreDefinition d = new IgnoreDefinition();
        d.definition(def);
        return d;
    }

    // value
    public ValueDefinition value(String value) {
        ValueDefinition d = new ValueDefinition();
        d.value(value);
        return d;
    }

    // value of
    public ValueOfDefinition valueof(String value, AbstractDefinition def) {
        ValueOfDefinition d = new ValueOfDefinition();
        d.string(value);
        d.definition(def);
        return d;
    }

    // exception
    public UnexpectedDefinition unexpected() {
        UnexpectedDefinition d = new UnexpectedDefinition();
        return d;
    }
}
