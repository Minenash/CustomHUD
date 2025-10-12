package com.minenash.customhud.HudElements.list;

import com.minenash.customhud.data.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface ListProvider {
    List<?> get();

//    default Supplier<?> supplier(ListProvider provider) {
//        return null;
////        return provider instanceof ListFunctioner<?> ? ListManager::getValue : provider::get;
//    }

    record ListFunctioner<T>(Supplier<T> supplier, Function<T,List<?>> function) implements ListProvider {
        @Override public List<?> get() {
            return function.apply(supplier.get());
        }
    }

    RequiredModmenu REGUIRES_MODMENU = new RequiredModmenu();
    class RequiredModmenu implements ListProvider {
        @Override
        public List<?> get() {
            return null;
        }
    }

    class EventListProvider<T> implements ListProvider {
        private final String id;
        private final List<T> entries = new ArrayList<>();

        public EventListProvider(String id) {
            this.id = id;
        }

        public EventListProvider<?> register(Profile profile) {
            profile.listEvents.put(id, this);
            return this;
        }

        public void reset() {
            entries.clear();
        }

        public void add(T entry) {
            entries.add(entry);
        }

        @Override
        public List<?> get() {
            return entries;
        }
    }
}

