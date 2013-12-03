package net.developithecus.parser2;

/**
 *
 */
public class CharacterRule extends Rule {
    private CharType charType;

    public CharType type() {
        return charType;
    }

    public CharacterRule type(CharType charType) {
        this.charType = charType;
        return this;
    }
}
