package com.gdx.kaps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
    public static <T> Optional<T> getRandomFrom(Stream<? extends T> stream) {
        return getRandomFrom(stream.collect(Collectors.toList()));
    }

    public static <T> Optional<T> getRandomFrom(Collection<? extends T> collection) {
        if (collection.size() == 0) return Optional.empty();
        return Optional.of(
          new ArrayList<>(collection).get(new Random().nextInt(collection.size()))
        );
    }
}
