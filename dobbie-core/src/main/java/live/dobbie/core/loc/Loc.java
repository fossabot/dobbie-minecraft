package live.dobbie.core.loc;

import live.dobbie.core.exception.ParserRuntimeException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Loc {
    private static final InternalLocSource INTERNAL = new InternalLocSource();

    private final @NonNull LocSource source;

    public Loc() {
        this(INTERNAL);
    }

    @NonNull
    public LocString args() {
        return new Lightweight();
    }

    @NonNull
    public LocString withKey(@NonNull String key) {
        String translatedKey = source.getTranslation(key);
        LocNumericVarSelector numericVarSelector;
        if (translatedKey == null) {
            translatedKey = INTERNAL.getTranslation(key);
            numericVarSelector = INTERNAL.getNumericVarSelect();
        } else {
            numericVarSelector = source.getNumericVarSelect();
        }
        return new Complete(translatedKey, numericVarSelector);
    }

    private class Lightweight extends LocString {
        private final Map<String, Object> values = new HashMap<>(2, 1.f);

        @Override
        Map<String, Object> values() {
            return values;
        }

        @Override
        public @NonNull LocString set(@NonNull String arg, String value) {
            put(arg, value);
            return this;
        }

        @Override
        public @NonNull LocString set(@NonNull String arg, Number number) {
            put(arg, number);
            return this;
        }

        @Override
        public @NonNull LocString set(@NonNull String arg, LocString nestedLocString) {
            put(arg, nestedLocString);
            return this;
        }

        @Override
        public @NonNull LocString set(@NonNull String arg, ToLocString nestedToLocString) {
            return set(arg, nestedToLocString == null ? null : nestedToLocString.toLocString(Loc.this));
        }

        @Override
        public @NonNull LocString copy(LocString storage) {
            values.putAll(storage.values());
            return this;
        }

        @Override
        public LocString key(@NonNull String key) {
            return Loc.this.withKey(key).copy(this);
        }

        private void put(String arg, Object value) {
            values.put(arg, value);
        }

        protected Object get(String arg) {
            return values.get(arg);
        }

        @Override
        public @NonNull String build() {
            throw new IllegalStateException("should not be called on lightweight LocString");
        }
    }

    @RequiredArgsConstructor
    private class Complete extends Lightweight {
        private final @NonNull String key;
        private final @NonNull LocNumericVarSelector numericVarSelector;

        @Override
        public LocString key(@NonNull String key) {
            throw new IllegalStateException("key already set on Complete LocString");
        }

        @Override
        public @NonNull String build() {
            String lastNumericArgName = null;
            StringBuilder b = new StringBuilder();
            char[] buf = key.toCharArray();
            int index = 0;
            do {
                int openingBrackets = readUntil(buf, '{', index, b);
                if (openingBrackets == -1) {
                    break;
                }
                StringBuilder inb = new StringBuilder();
                int closingBrackets = readUntil(buf, '}', openingBrackets + 1, inb);
                if (closingBrackets == -1) {
                    break;
                }
                index = closingBrackets;
                String bracketsContent = inb.toString();
                if (bracketsContent.indexOf('|') != -1) {
                    String[] bracketsNumericArray = StringUtils.split(bracketsContent, "|", 2);
                    String numericArgumentName = bracketsNumericArray[0];
                    String[] numericVariants = StringUtils.split(bracketsNumericArray[1], ",");
                    if (numericVariants.length != numericVarSelector.variantCount()) {
                        throw new ParserRuntimeException("expected " + numericVarSelector.variantCount() + " variants in \"" + bracketsContent + "\" of \"" + key + "\"");
                    }
                    if (numericArgumentName.equals("%")) {
                        if (lastNumericArgName == null) {
                            throw new ParserRuntimeException("expected at least one numeric argument before \"" + bracketsContent + "\" in \"" + key + "\"");
                        }
                        numericArgumentName = lastNumericArgName;
                    }
                    Object argument = get(numericArgumentName);
                    if (!(argument instanceof Number)) {
                        throw new ParserRuntimeException("argument " + numericArgumentName + " is not a number (given: " + argument + ") in \"" + bracketsContent + "\" of \"" + key + "\"");
                    }
                    int selectedVariantIndex = numericVarSelector.selectVariant((Number) argument);
                    b.append(numericVariants[selectedVariantIndex]);
                    continue;
                }
                String argName = bracketsContent;
                Object substitutingValue = get(argName);
                if (substitutingValue == null) {
                    b.append("(" + argName + ")");
                    continue;
                }
                if (substitutingValue instanceof String) {
                    b.append(substitutingValue);
                    continue;
                }
                if (substitutingValue instanceof Number) {
                    b.append(substitutingValue.toString());
                    lastNumericArgName = argName;
                    continue;
                }
                if (substitutingValue instanceof LocString) {
                    LocString c = (LocString) substitutingValue;
                    b.append(c.build());
                    continue;
                }
            } while (++index < buf.length);

            return b.toString();
        }
    }

    private static int readUntil(char[] buf, char stopChar, int startIndex, StringBuilder b) {
        for (int i = startIndex; i < buf.length; i++) {
            char ch = buf[i];
            if (ch == stopChar) {
                return i;
            }
            b.append(ch);
        }
        return -1;
    }

    private static class InternalLocSource implements LocSource {
        private final LocNumericVarSelector numericVarSelector = new LocNumericVarSelector() {
            @Override
            public int variantCount() {
                return 2;
            }

            @Override
            public int selectVariant(Number number) {
                return number.intValue() == 1 ? 0 : 1;
            }
        };

        @Override
        @NonNull
        public String getTranslation(@NonNull String key) {
            return key;
        }

        @Override
        public @NonNull LocNumericVarSelector getNumericVarSelect() {
            return numericVarSelector;
        }
    }
}
