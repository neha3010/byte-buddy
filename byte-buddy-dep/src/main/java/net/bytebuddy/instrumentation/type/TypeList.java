package net.bytebuddy.instrumentation.type;

import net.bytebuddy.instrumentation.method.bytecode.stack.StackSize;
import org.objectweb.asm.Type;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementations represent a list of type descriptions.
 */
public interface TypeList extends List<TypeDescription> {

    /**
     * Returns a list of internal names of all types represented by this list.
     *
     * @return An array of all internal names or {@code null} if the list is empty.
     */
    String[] toInternalNames();

    /**
     * Returns the sum of the size of all types contained in this list.
     *
     * @return The sum of the size of all types contained in this list.
     */
    int getStackSize();

    @Override
    TypeList subList(int fromIndex, int toIndex);

    /**
     * Implementation of a type list for an array of loaded types.
     */
    static class ForLoadedType extends AbstractList<TypeDescription> implements TypeList {

        /**
         * The loaded types this type list represents.
         */
        private final Class<?>[] type;

        /**
         * Creates a new type list for an array of loaded types.
         *
         * @param type The types to be represented by this list.
         */
        public ForLoadedType(Class<?>... type) {
            this.type = type;
        }

        /**
         * Creates a new type list for an array of loaded types.
         *
         * @param types The types to be represented by this list.
         */
        public ForLoadedType(List<Class<?>> types) {
            type = types.toArray(new Class<?>[types.size()]);
        }

        @Override
        public TypeDescription get(int index) {
            return new TypeDescription.ForLoadedType(type[index]);
        }

        @Override
        public int size() {
            return type.length;
        }

        @Override
        public String[] toInternalNames() {
            String[] internalNames = new String[type.length];
            int i = 0;
            for (Class<?> aType : type) {
                internalNames[i++] = Type.getInternalName(aType);
            }
            return internalNames.length == 0 ? null : internalNames;
        }

        @Override
        public int getStackSize() {
            return StackSize.sizeOf(Arrays.asList(type));
        }

        @Override
        public TypeList subList(int fromIndex, int toIndex) {
            return new Explicit(super.subList(fromIndex, toIndex));
        }
    }

    /**
     * A wrapper implementation of an explicit list of types.
     */
    static class Explicit extends AbstractList<TypeDescription> implements TypeList {

        /**
         * The list of type descriptions this list represents.
         */
        private final List<? extends TypeDescription> typeDescriptions;

        /**
         * Creates an immutable wrapper.
         *
         * @param typeDescriptions The list of types to be represented by this wrapper.
         */
        public Explicit(List<? extends TypeDescription> typeDescriptions) {
            this.typeDescriptions = typeDescriptions;
        }

        @Override
        public TypeDescription get(int index) {
            return typeDescriptions.get(index);
        }

        @Override
        public int size() {
            return typeDescriptions.size();
        }

        @Override
        public String[] toInternalNames() {
            String[] internalNames = new String[typeDescriptions.size()];
            int i = 0;
            for (TypeDescription typeDescription : typeDescriptions) {
                internalNames[i++] = typeDescription.getInternalName();
            }
            return internalNames.length == 0 ? null : internalNames;
        }

        @Override
        public int getStackSize() {
            int stackSize = 0;
            for (TypeDescription typeDescription : typeDescriptions) {
                stackSize += typeDescription.getStackSize().getSize();
            }
            return stackSize;
        }

        @Override
        public TypeList subList(int fromIndex, int toIndex) {
            return new Explicit(super.subList(fromIndex, toIndex));
        }
    }

    /**
     * An implementation of an empty type list.
     */
    static class Empty extends AbstractList<TypeDescription> implements TypeList {

        @Override
        public TypeDescription get(int index) {
            throw new IndexOutOfBoundsException("index = " + index);
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public String[] toInternalNames() {
            return null;
        }

        @Override
        public int getStackSize() {
            return 0;
        }

        @Override
        public TypeList subList(int fromIndex, int toIndex) {
            if (fromIndex == toIndex && toIndex == 0) {
                return this;
            } else if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
            } else {
                throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
            }
        }
    }
}
