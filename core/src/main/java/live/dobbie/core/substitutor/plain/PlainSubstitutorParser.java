package live.dobbie.core.substitutor.plain;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.SubstitutableParser;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PlainSubstitutorParser implements SubstitutableParser {
    private final char[] allowedVariableNameChars = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',

            '.', '_',

            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z',

            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
    };

    {
        Arrays.sort(allowedVariableNameChars);
    }

    private final char openingChar = '{', closingChar = '}', escapingChar = '\\';

    @Override
    public @NonNull PlainSubstitutable parse(@NonNull String str) throws ParserException {
        List<Substitutable> elements = new ArrayList<>();
        StringBuilder b = new StringBuilder();
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (c == '$' &&
                    is(ch, i + 1, openingChar) &&
                    i + 2 < ch.length - 1
            ) {
                // check if escaped like that "\$"
                if (is(ch, i - 1, escapingChar)) {
                    // but not like that "\\$"
                    if (is(ch, i - 2, escapingChar)) {
                        // then delete last '\' and leave another '\'
                        b.deleteCharAt(b.length() - 1);
                    } else {
                        b.append(c);
                        continue;
                    }
                }
                drainBuffer(b, elements);
                String varName = readVariableName(ch, i + 2);
                elements.add(new VarSubstitutable(varName));
                i += varName.length() + 2; // skip openingChar, varName and closingChar
            } else {
                b.append(c);
            }
        }
        drainBuffer(b, elements);
        return new PlainSubstitutable(Collections.unmodifiableList(elements));
    }

    private String readVariableName(char[] ch, int index) throws ParserException {
        StringBuilder b = new StringBuilder();
        boolean gotClosingCharacter = false;
        for (int i = index; i < ch.length; i++) {
            char c = ch[i];
            if (c == closingChar) {
                gotClosingCharacter = true;
                break;
            }
            if (Arrays.binarySearch(allowedVariableNameChars, c) < 0) {
                throw new ParserException("bad char \"" + c + "\" at index " + index);
            }
            b.append(c);
        }
        if (!gotClosingCharacter) {
            throw new ParserException("expected \"" + closingChar + "\" on a variable name, but got end of string");
        }
        return b.toString();
    }

    private static boolean is(char[] chars, int at, char value) {
        if (at < 0 || at >= chars.length) {
            return false;
        }
        return chars[at] == value;
    }

    private static void drainBuffer(StringBuilder buffer, List<Substitutable> list) {
        if (buffer.length() != 0) {
            list.add(new StringSubstitutable(buffer.toString()));
            buffer.setLength(0);
        }
    }
}
