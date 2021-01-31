package com.gdx.kaps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class SoundStream {
    public enum SoundRecord {
        FIRE("fire", 2),
        FLIP("flip", 3),
        PAINT("paint", 3),
        SLICE("slice", 2),
        ;

        private final String name;
        private final int set;

        SoundRecord(String name, int n) {
            if (n < 1) throw new IllegalArgumentException("Must have at least one instance of file '" + name + ".wav'");
            this.name = name;
            set = n;
        }

        private static Optional<String> randomIndex(String name) {
            return Arrays.stream(values())
                     .filter(s -> s.name.equals(Objects.requireNonNull(name)))
                     .map(s -> new Random().nextInt(s.set) + "")
                     .findFirst();
        }

        public static String pathOf(String name) {
            return "android/assets/sounds/" + name + SoundRecord.randomIndex(name).orElse("") + ".wav";
        }
    }
    private Sound sound;

    public void play(String name) {
        if (sound != null) sound.dispose();
        sound = Gdx.audio.newSound(
          Gdx.files.internal(SoundRecord.pathOf(name))
        );
        sound.setVolume(sound.play(), 0.15f);
    }
}
