package live.dobbie.core.substitutor.plain;

import live.dobbie.core.exception.ParserException;
import live.dobbie.core.substitutor.Substitutable;
import live.dobbie.core.substitutor.SubstitutableParser;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@RequiredArgsConstructor
@Builder
public class PlainSubstitutorParser implements SubstitutableParser {
    private final char[] allowedVariableNameChars = new char[]{
            '*', // modifier

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

    private final char varChar = '$', varModChar = '*', openingChar = '{', closingChar = '}', escapingChar = '\\';

    private final @NonNull
    @Singular
    Map<String, VarConverter> varMods;
    private final @NonNull VarConverter defaultVarMod;

    public PlainSubstitutorParser() {
        this(Collections.emptyMap(), VarConverter.Identity.INSTANCE);
    }

    @Override
    public @NonNull ListSubstitutable parse(@NonNull String str) throws ParserException {
        List<Substitutable> elements = new ArrayList<>();
        StringBuilder b = new StringBuilder();
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];

            if (c == varChar &&
                    is(ch, i + 1, openingChar) && // ${
                    i + 2 < ch.length - 1 // ${...
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
                String varName = readCharsUntil(ch, i + 2, closingChar);
                int varLength = varName.length();
                VarConverter varConverter = defaultVarMod;
                if (varName.charAt(0) == varModChar) {
                    String modName = StringUtils.substringBefore(varName.substring(1), String.valueOf(varModChar));
                    if (varName.length() == modName.length() + 1) {
                        throw new ParserException("varName missing after varMod");
                    }
                    varConverter = varMods.get(modName);
                    if (varConverter == null) {
                        throw new ParserException("modName not found: \"" + modName + "\"");
                    }
                    varName = varName.substring(modName.length() + 2); // skip both «*»
                }
                drainBuffer(b, elements);
                elements.add(new VarSubstitutable(varName, varConverter));
                i += varLength + 2; // skip openingChar, varName and closingChar
            } else {
                b.append(c);
            }
        }
        drainBuffer(b, elements);
        return new ListSubstitutable(Collections.unmodifiableList(elements));
    }

    private String readCharsUntil(char[] ch, int index, char expectedChar) throws ParserException {
        StringBuilder b = new StringBuilder();
        boolean gotExpectedCharacter = false;
        for (int i = index; i < ch.length; i++) {
            char c = ch[i];
            if (c == expectedChar) {
                gotExpectedCharacter = true;
                break;
            }
            if (Arrays.binarySearch(allowedVariableNameChars, c) < 0) {
                throw new ParserException("bad char \"" + c + "\" at index " + index);
            }
            b.append(c);
        }
        if (!gotExpectedCharacter) {
            throw new ParserException("expected \"" + expectedChar + "\" but got end of string");
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
