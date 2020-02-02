package live.dobbie.core.path;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.iterators.ArrayIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.*;
import java.util.stream.Collectors;

public final class Path implements Iterable<String> {
    public static final Path EMPTY = new Path();
    public static final String SEPARATOR = ".";

    private final String[] breadcrumbs;
    private final int start, length;

    private Path() {
        this(new String[0], 0, 0);
    }

    private Path(@NonNull String[] breadcrumbs, int start, int length) {
        this.breadcrumbs = breadcrumbs;
        this.start = start;
        this.length = length;
    }

    public String at(int index) {
        try {
            return breadcrumbs[index];
        } catch (ArrayIndexOutOfBoundsException indexException) {
            throw new RuntimeException("index " + index + " is not found in " + Path.toString(this));
        }
    }

    public boolean isEmpty() {
        return length == 0;
    }

    public int length() {
        return length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return stringArrayEquals(
                breadcrumbs, start, length,
                path.breadcrumbs, path.start, path.length
        );
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(start, length);
        result = 31 * result + Arrays.hashCode(breadcrumbs);
        return result;
    }

    public Path parent() {
        if (length == 0) {
            return null;
        }
        if (length == 1) {
            return Path.EMPTY;
        }
        return new Path(breadcrumbs, start, length - 1);
    }

    public @NonNull Path subset(int offset, int length) throws ArrayIndexOutOfBoundsException {
        if (offset < 0) {
            throw new IllegalArgumentException("offset < 0");
        }
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
        if (length == 0) {
            return Path.EMPTY;
        }
        if (offset == 0 && length == this.length) {
            return this;
        }
        int newStart = this.start + offset;
        if (newStart >= breadcrumbs.length) {
            throw new ArrayIndexOutOfBoundsException("offset");
        }
        int remaining = breadcrumbs.length - newStart;
        if (length > remaining) {
            throw new ArrayIndexOutOfBoundsException("length");
        }
        return new Path(breadcrumbs, start + offset, length);
    }

    public @NonNull Path merge(@NonNull Path path) {
        if (path.isEmpty()) {
            return this;
        }
        if (isEmpty()) {
            return path;
        }
        String[] breadcrumbsCopy = copy(breadcrumbs, start, length + path.length);
        System.arraycopy(path.breadcrumbs, 0, breadcrumbsCopy, breadcrumbs.length, path.breadcrumbs.length);
        return new Path(breadcrumbsCopy, 0, breadcrumbsCopy.length);
    }

    public @NonNull Path merge(@NonNull String... path) {
        return merge(Path.of(path));
    }

    private UnmodifiableArrayList<String> breadcrumbsList;

    public List<String> getBreadcrumbs() {
        if (breadcrumbsList == null) {
            breadcrumbsList = new UnmodifiableArrayList<>(breadcrumbs, start, length);
        }
        return breadcrumbsList;
    }

    public void ensureSize(int expectedSize) throws IndexOutOfBoundsException {
        if (length != expectedSize) {
            throw new IndexOutOfBoundsException("illegal size: " + length + ", expected: " + expectedSize);
        }
    }

    public void ensureSizeAtLeast(int expectedMinSize) throws IndexOutOfBoundsException {
        if (length < expectedMinSize) {
            throw new IndexOutOfBoundsException("illegal size: " + length + ", expected: " + expectedMinSize);
        }
    }

    private String toString;

    @Override
    public String toString() {
        if (toString == null) {
            toString = "Path{" +
                    "breadcrumbs=" + Arrays.stream(breadcrumbs)
                    .skip(start)
                    .limit(length)
                    .collect(Collectors.joining(SEPARATOR)) +
                    '}';
        }
        return toString;
    }

    public static String toString(@NonNull Path path, @NonNull String separator, int excludeLast) {
        if (excludeLast > path.length) {
            throw new IndexOutOfBoundsException("excludeLast >= path.length");
        }
        return StringUtils.join(path.breadcrumbs, separator, path.start, path.start + path.length - excludeLast);
    }

    public static String toString(@NonNull Path path, int excludeLast) {
        return toString(path, SEPARATOR, excludeLast);
    }

    public static String toString(@NonNull Path path) {
        return toString(path, SEPARATOR, 0);
    }

    public static Path of(@NonNull String... nodes) {
        return nodes.length == 0 ? EMPTY : new Path(Validate.noNullElements(nodes), 0, nodes.length);
    }

    public static Path parse(@NonNull String value, @NonNull String separator) {
        String[] nodes = StringUtils.split(value, separator);
        return Path.of(nodes);
    }

    public static Path parse(@NonNull String value) {
        return parse(value, SEPARATOR);
    }

    private static String[] copy(String[] original, int start, int size) {
        String[] copy = new String[size - start];
        System.arraycopy(original, start, copy, 0, Math.min(original.length, size));
        return copy;
    }

    private static boolean stringArrayEquals(@NonNull String[] a, int a_start, int a_length,
                                             @NonNull String[] a2, int a2_start, int a2_length) {
        if (a_length != a2_length) {
            return false;
        }
        for (int i = 0; i < a_length; i++) {
            String a_value = a[a_start + i];
            String a2_value = a2[a2_start + i];
            if (!a_value.equals(a2_value)) {
                return false;
            }
        }
        return true;
    }

    @NonNull
    @Override
    public Iterator<String> iterator() {
        return new ArrayIterator<>(breadcrumbs, start, start + length);
    }

    @RequiredArgsConstructor
    private static class UnmodifiableArrayList<E> extends AbstractList<E> {
        private final E[] array;
        private final int start, length;

        @Override
        public E get(int index) {
            return array[start + index];
        }

        @Override
        public int size() {
            return length;
        }
    }
}
