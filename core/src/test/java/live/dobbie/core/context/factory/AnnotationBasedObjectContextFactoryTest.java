package live.dobbie.core.context.factory;

import live.dobbie.core.context.ObjectContext;
import live.dobbie.core.context.factory.nametranslator.SnakeCaseTranslator;
import live.dobbie.core.context.factory.nametranslator.TrailingRemovingTranslator;
import live.dobbie.core.context.factory.nametranslator.VarNameTranslator;
import live.dobbie.core.loc.Loc;
import live.dobbie.core.loc.LocString;
import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.misc.primitive.StringPrimitive;
import live.dobbie.core.misc.primitive.converter.*;
import live.dobbie.core.path.Path;
import live.dobbie.core.trigger.authored.Author;
import live.dobbie.core.trigger.authored.Authored;
import live.dobbie.core.trigger.authored.PlainAuthor;
import live.dobbie.core.trigger.messaged.Message;
import live.dobbie.core.trigger.messaged.Messaged;
import live.dobbie.core.trigger.messaged.PlainMessage;
import lombok.NonNull;
import lombok.Value;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AnnotationBasedObjectContextFactoryTest {

    @Test
    void varTest() {
        Object o = new Object();
        Instant now = Instant.now();
        Author author = new PlainAuthor("test author");
        Message message = new PlainMessage("test message");
        VarTestTrigger varTest = new VarTestTrigger(
                now,
                "test_source",
                "var_test",
                author,
                message,
                o,
                "bar",
                new ComplexVar("a!", "the b!")
        );
        AnnotationBasedObjectContextFactory objectContextFactory = new AnnotationBasedObjectContextFactory(
                ObjectContextFactory.Simple.INSTANCE,
                new SnakeCaseTranslator(
                        new TrailingRemovingTranslator(
                                VarNameTranslator.NONE,
                                Collections.singletonList("trigger"),
                                Collections.singletonList("get"),
                                Collections.emptyList()
                        )
                ),
                new PrimitiveConverterCache(),
                SequentalConverterProvider.builder()
                        .registerProvider(new AnnotationBasedConverterProvider())
                        .registerProvider(PrimitiveConverterProvider.builder()
                                .registerStandardConverters()
                                .build()
                        )
                        .build()
        );
        ObjectContext objectContext = objectContextFactory.generateContextBuilder(varTest).build();
        assertNotNull(objectContext);
        Map<Path, Primitive> variables = objectContext.getVariables();
        Map<String, Object> objects = objectContext.getObjects();
        assertEquals(9, variables.size());
        assertEquals(Primitive.of(now), variables.get(Path.of("timestamp")));
        assertEquals(Primitive.of(now.toEpochMilli()), variables.get(Path.of("timestamp_utc_millis")));
        assertEquals(Primitive.of("test_source"), variables.get(Path.of("source")));
        assertEquals(Primitive.of("var_test"), variables.get(Path.of("name")));
        assertEquals(Primitive.of("test author"), variables.get(Path.of("author_name")));
        assertEquals(Primitive.of("test message"), variables.get(Path.of("message")));
        assertEquals(Primitive.of("bar"), variables.get(Path.of("foo")));
        assertEquals(Primitive.of("a!"), variables.get(Path.of("complexVar", "a")));
        assertEquals(Primitive.of("the b!"), variables.get(Path.of("complexVar", "b")));
        assertEquals(1, objects.size());
        assertEquals(o, objects.get("object"));
    }

    @Value
    @ContextClass
    public static class VarTestTrigger implements Authored, Messaged {
        @NonNull Instant timestamp;
        @NonNull String source;
        @NonNull String name;

        @NonNull Author author;
        @NonNull Message message;

        @ContextObject
        @NonNull Object object;

        @ContextVar
        @NonNull String foo;

        @ContextComplexVar({
                @ContextVar(path = {"complexVar", "a"}, parser = ComplexVar.A.class),
                @ContextVar(path = {"complexVar", "b"}, parser = ComplexVar.B.class)
        })
        @NonNull ComplexVar complexVar;

        @Override
        public @NonNull LocString toLocString(@NonNull Loc loc) {
            return loc.withKey("Var Test Trigger from {author}: {message}")
                    .copy(Authored.super.toLocString(loc))
                    .copy(Messaged.super.toLocString(loc));
        }
    }

    @Value
    public static class ComplexVar {
        @NonNull String a, b;

        public static class A implements PrimitiveConverter<ComplexVar, StringPrimitive> {
            @NonNull
            @Override
            public StringPrimitive parse(@NonNull ComplexVar value) {
                return new StringPrimitive(value.a);
            }
        }

        public static class B implements PrimitiveConverter<ComplexVar, StringPrimitive> {
            @NonNull
            @Override
            public StringPrimitive parse(@NonNull ComplexVar value) {
                return new StringPrimitive(value.b);
            }
        }
    }

}