package net.dirtcraft.dirtcommons.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Cycler<T> {
    //todo add thread locals if it becomes an issue.

    public static <T> Cycler<T> get(T... elements){
        switch (elements.length){
            case 0: return new Singleton<>(null);
            case 1: return new Singleton<>(elements[0]);
            case 2: return new Switching<>(elements[0], elements[1]);
            default: return new Looping<>(elements);
        }
    }

    public abstract Cycler<T> reset();

    public abstract Cycler<T> last();

    public abstract T get();

    public abstract T reverse();

    public abstract List<T> asList();

    private static class Singleton<T> extends Cycler<T>{
        private final T t;

        private Singleton(T t) {
            this.t = t;
        }

        @Override
        public Singleton<T> reset() {
            return this;
        }

        @Override
        public Singleton<T> last() {
            return this;
        }

        @Override
        public T get() {
            return t;
        }

        @Override
        public T reverse() {
            return t;
        }

        @Override
        public List<T> asList() {
            return Collections.singletonList(t);
        }
    }

    private static class Switching<T> extends Cycler<T>{
        private final T a;
        private final T b;
        private boolean flip;
        private Switching(T a, T b){
            this.a = a;
            this.b = b;
        }

        @Override
        public Switching<T> reset() {
            flip = false;
            return this;
        }

        @Override
        public Switching<T> last() {
            flip = true;
            return this;
        }

        @Override
        @SuppressWarnings("AssignmentUsedAsCondition")
        public T get() {
            return (flip = !flip)? a : b;
        }

        @Override
        @SuppressWarnings("AssignmentUsedAsCondition")
        public T reverse() {
            return (flip = !flip)? a : b;
        }

        @Override
        public List<T> asList() {
            return Arrays.asList(a, b);
        }
    }

    private static class Looping<T> extends Cycler<T>{
        private final List<T> elements;
        private final int last;
        private int idx = 0;
        private Looping(T... others){
            this.elements = Arrays.asList(others);
            this.last = others.length - 1;
        }

        @Override
        public Looping<T> reset(){
            this.idx = 0;
            return this;
        }

        @Override
        public Looping<T> last(){
            this.idx = last;
            return this;
        }

        @Override
        public T get(){
            T result = elements.get(idx);
            idx = idx >= last? 0 : idx+1;
            return result;
        }

        @Override
        public T reverse(){
            T result = elements.get(idx);
            idx = idx <= 0? last : idx-1;
            return result;
        }

        @Override
        public List<T> asList() {
            return elements;
        }
    }
}
