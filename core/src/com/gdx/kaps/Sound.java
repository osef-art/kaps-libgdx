package com.gdx.kaps;

import com.badlogic.gdx.Gdx;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
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
        IMPACT("impact"),
        LINE("line"),
        LIST_FILES("list_files"),
        MATCH_FIVE("match_five"),
        MOVE("move"),
        OOF("oof"),
        PAINT("paint", 3),
        PAUSE("pause"),
        PLOP("plop"),
        PLOP0("plop0"),
        PLOP1("plop1"),
        PLOP2("plop2"),
        PLOP3("plop3"),
        PLOP4("plop4"),
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

        private static SoundRecord ofName(String name) {
            for (var sound : values()) {
                if (sound.name.equals(name)) return sound;
            }
            throw new IllegalArgumentException("Can't find a sound of name '" + name + ".wav'");
        }

        public static String pathOf(String name) {
            var pathList = Stream.of(ofName(name))
                             .flatMap(s -> {
                                 if (s.set > 1) {
                                     return IntStream.range(0, s.set).mapToObj(n -> s.path.toString() + n  + ".wav");
                                 }
                                 return Stream.of(s.path.toString() + ".wav");
                             })
                             .collect(Collectors.toList());
            return pathList.get(new Random().nextInt(pathList.size()));
        }
    }
    private static com.badlogic.gdx.audio.Sound currentSound;

    public static void play(String name) {
        if (currentSound != null) currentSound.dispose();
        currentSound = Gdx.audio.newSound(
          Gdx.files.internal(SoundRecord.pathOf(name))
        );
        currentSound.setVolume(currentSound.play(), 0.05f);
    }
}
