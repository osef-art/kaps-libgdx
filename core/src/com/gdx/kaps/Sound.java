package com.gdx.kaps;

import com.badlogic.gdx.Gdx;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Sound {


    enum SoundRecord {
        ALARM("alarm"),
        CANT("cant"),
        CLEARED("cleared"),
        COLOR("color"),
        EXPLOSION("explosion"),
        FIRE("fire", 2),
        FLIP("flip", 3),
        GAME_OVER("game_over"),
        HOLD("hold"),
        IMPACT("impact", 4),
        LINE("line"),
        LIST_FILES("list_files"),
        MATCH_FIVE("match_five"),
        MOVE("move"),
        OOF("oof"),
        PAINT("paint", 3),
        PAUSE("pause"),
        PLOP("plop", 5),
        SLICE("slice", 2),
        START("start"),
        VIRUS("virus"),
        WHOOSH("whoosh"),
        ;

        private final String name;
        private final Path path;
        private final int set;

        SoundRecord(String name) {
            this(name, 1);
        }

        SoundRecord(String name, int n) {
            if (n < 1) throw new IllegalArgumentException("Must have at least one instance of file '" + name + ".wav'");
            path = Path.of("android/assets/sounds/" + name);
            this.name = name;
            set = n;
        }

        public static String pathOf(String name) {
            return Arrays.stream(values())
              .filter(s -> s.name.equals(name))
              .flatMap(s -> {
                  if (s.set > 1) {
                      return IntStream.range(0, s.set).mapToObj(n -> s.path.toString() + n  + ".wav");
                  }
                  return Stream.of(s.path.toString() + ".wav");
              })
              .findAny()
              .orElseThrow(() -> new IllegalArgumentException("Can't find sound of name '" + name + ".wav'"));
        }
    }
    private static com.badlogic.gdx.audio.Sound currentSound;

    public static void play(String name) {
        if (currentSound != null) currentSound.dispose();
        currentSound = Gdx.audio.newSound(
          Gdx.files.internal(SoundRecord.pathOf(name))
        );
        currentSound.play();
    }
}
