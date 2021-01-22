package com.gdx.kaps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;

public class Utils {
    public static <T> Optional<T> getRandomFrom(Collection<? extends T> collection) {
        var list = new ArrayList<>(collection);
        if (list.size() == 0) return Optional.empty();
        return Optional.of(
          list.get(new Random().nextInt(list.size()))
        );
    }
}
