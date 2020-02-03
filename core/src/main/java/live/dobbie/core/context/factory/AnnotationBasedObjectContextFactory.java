package live.dobbie.core.context.factory;

import live.dobbie.core.context.ObjectContextBuilder;
import live.dobbie.core.context.factory.nametranslator.VarNameTranslator;
import live.dobbie.core.misc.primitive.Primitive;
import live.dobbie.core.misc.primitive.converter.PrimitiveConverter;
import live.dobbie.core.misc.primitive.converter.PrimitiveConverterCache;
import live.dobbie.core.misc.primitive.converter.PrimitiveConverterProvider;
import live.dobbie.core.misc.primitive.converter.StandardConverters;
import live.dobbie.core.path.Path;
import live.dobbie.core.trigger.Trigger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

// TODO more caching for reflection operations?
@RequiredArgsConstructor
public class AnnotationBasedObjectContextFactory implements ObjectContextFactory {
    private final @NonNull ObjectContextFactory delegateContext;
    private final @NonNull VarNameTranslator nameTranslator;
    private final @NonNull PrimitiveConverterCache converterCache;
    private final @NonNull PrimitiveConverterProvider converterProvider;

    @Override
    public @NonNull ObjectContextBuilder generateContextBuilder(@NonNull Trigger trigger) {
        ObjectContextBuilder builder = delegateContext.generateContextBuilder(trigger);
        setObjectAndVars(trigger, builder);
        return builder;
    }

    private void setObjectAndVars(Trigger trigger, ObjectContextBuilder builder) {
        Class<?> cl = trigger.getClass();
        Map<String[], Primitive> vars = new HashMap<>();
        Map<String, Object> objects = new HashMap<>();
        processClass(trigger, vars, objects);
        //setVars(cl, vars, builder);
        setVars((String) null, vars, builder);
        setObjects(objects, builder);
        /*for (Class<?> sc : ClassUtils.getAllSuperclasses(cl)) {
            if(isTrigger(sc)) {
                setVars(sc, vars, builder);
            }
        }
        for (Class<?> i : ClassUtils.getAllInterfaces(cl)) {
            if(isTrigger(i)) {
                setVars(i, vars, builder);
            }
        }*/
    }

    private void setVars(Class<?> cl, Map<String[], Primitive> vars, ObjectContextBuilder builder) {
        setVars(getVarName(cl), vars, builder);
    }

    private void setObjects(Map<String, Object> objects, ObjectContextBuilder builder) {
        objects.forEach(builder::set);
    }


    private String getVarName(Class<?> cl) {
        ContextClass objectClass = cl.getAnnotation(ContextClass.class);
        if (objectClass == null) {
            return null;
        }
        if (objectClass.name().isEmpty()) {
            return translateClassName(cl);
        } else {
            return objectClass.name();
        }
    }

    private void setVars(String varName, Map<String[], Primitive> vars, ObjectContextBuilder builder) {
        /*if(varName == null) {
            return;
        }*/
        Path p = varName == null ? Path.EMPTY : Path.of(varName);
        for (Map.Entry<String[], Primitive> primitiveEntry : vars.entrySet()) {
            builder.set(p.merge(primitiveEntry.getKey()), primitiveEntry.getValue());
        }
    }

    private void processClass(Trigger trigger, Map<String[], Primitive> vars, Map<String, Object> objects) {
        Class<?> cl = trigger.getClass();
        processClass(trigger, cl, vars, objects);
        for (Class<?> sc : ClassUtils.getAllSuperclasses(cl)) {
            if (isTrigger(sc)) {
                processClass(trigger, sc, vars, objects);
            }
        }
        for (Class<?> i : ClassUtils.getAllInterfaces(cl)) {
            if (isTrigger(i)) {
                processClass(trigger, i, vars, objects);
            }
        }
    }

    private void processClass(Trigger trigger, Class<?> cl, Map<String[], Primitive> vars, Map<String, Object> objects) {
        for (Method method : cl.getDeclaredMethods()) {
            processMember(trigger, method, vars, objects);
        }
        for (Field field : cl.getDeclaredFields()) {
            processMember(trigger, field, vars, objects);
        }
    }

    private void processMember(Trigger trigger, AccessibleObject member, Map<String[], Primitive> vars, Map<String, Object> objects) {
        processVarMember(trigger, member, vars);
        processObjectMember(trigger, member, objects);
    }

    private void processVarMember(Trigger trigger, AccessibleObject member, Map<String[], Primitive> vars) {
        ContextComplexVar contextComplexVar = member.getAnnotation(ContextComplexVar.class);
        if (contextComplexVar == null) {
            ContextVar contextVar = member.getAnnotation(ContextVar.class);
            if (contextVar != null) {
                processVarMember(trigger, member, contextVar, vars);
            }
        }
        for (ContextVar contextVar : contextComplexVar.value()) {
            processVarMember(trigger, member, contextVar, vars);
        }
    }

    private void validateMember(AccessibleObject member) {
        if (member instanceof Method) {
            validateMethod((Method) member);
        }
        member.setAccessible(true);
    }

    private void validateMethod(Method method) {
        if (method.getParameterCount() != 0) {
            throw new Error("method is annotated with " + ContextObject.class + ", but has parameters: " + method);
        }
    }

    private Object getValue(AccessibleObject member, Trigger trigger) {
        if (member instanceof Method) {
            return getMethodValue((Method) member, trigger);
        }
        if (member instanceof Field) {
            return getFieldValue((Field) member, trigger);
        }
        return reportUnknownMember(member);
    }

    private static Object getMethodValue(Method method, Trigger trigger) {
        try {
            return method.invoke(trigger);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("could not call " + method + " on " + trigger, e);
        }
    }

    private static Object getFieldValue(Field field, Trigger trigger) {
        try {
            return field.get(trigger);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("could not get field value " + field + " on " + trigger, e);
        }
    }

    private static Class<?> getReturnType(AccessibleObject member) {
        if (member instanceof Method) {
            return ((Method) member).getReturnType();
        }
        if (member instanceof Field) {
            return ((Field) member).getType();
        }
        return reportUnknownMember(member);
    }

    private String translateMember(AccessibleObject member) {
        if (member instanceof Method) {
            return translateMethod((Method) member);
        }
        if (member instanceof Field) {
            return translateField((Field) member);
        }
        return reportUnknownMember(member);
    }

    private static <T> T reportUnknownMember(AccessibleObject member) {
        throw new Error("unknown member passed: " + member);
    }

    private void processObjectMember(Trigger trigger, AccessibleObject member, Map<String, Object> objects) {
        ContextObject contextObject = member.getAnnotation(ContextObject.class);
        if (contextObject == null) {
            return;
        }
        validateMember(member);
        Object object = getValue(member, trigger);
        String name;
        if (contextObject.name().isEmpty()) {
            name = translateMember(member);
        } else {
            name = contextObject.name();
        }
        objects.put(name, object);
    }

    private boolean processVarMember(Trigger trigger, AccessibleObject member, ContextVar contextVar, Map<String[], Primitive> vars) {
        validateMember(member);
        PrimitiveConverter converter;
        try {
            converter = findConverter(contextVar, getReturnType(member));
        } catch (RuntimeException rE) {
            throw new RuntimeException("could not find converter for " + member);
        }
        Object value = getValue(member, trigger);
        String[] pathEnd;
        if (contextVar.path().length == 0) {
            pathEnd = new String[]{translateMember(member)};
        } else {
            pathEnd = contextVar.path();
        }
        Primitive convertedValue;
        try {
            convertedValue = converter.parse(value);
        } catch (RuntimeException rE) {
            throw new RuntimeException("could not convert " + value + " taken from " + member + " on " + trigger, rE);
        }
        vars.put(pathEnd, convertedValue);
        return true;
    }

    private PrimitiveConverter findConverter(ContextVar contextVar, Class<?> cl) {
        PrimitiveConverter converter;
        if (contextVar.parser() == PrimitiveConverter.class) {
            converter = converterProvider.requireConverter(cl);
        } else {
            converter = converterCache.get(contextVar.parser());
        }
        if (contextVar.nullable()) {
            converter = new StandardConverters.NullAwareConverter(converter);
        }
        return converter;
    }

    private String translateClassName(Class<?> clazz) {
        return nameTranslator.translateClass(clazz.getSimpleName());
    }

    private String translateMethod(Method method) {
        return nameTranslator.translateMethod(method.getName());
    }

    private String translateField(Field field) {
        return nameTranslator.translateField(field.getName());
    }

    private static boolean isTrigger(Class cl) {
        return Trigger.class.isAssignableFrom(cl);
    }
}
