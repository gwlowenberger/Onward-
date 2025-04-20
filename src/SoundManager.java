import java.io.File;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public final class SoundManager {
    private static Clip uiElementSound, invalidTileSound, validTileSound;
    private static boolean soundOn = true;
    
    public SoundManager() {
    }

    @SuppressWarnings("UseSpecificCatch")
    public static void uiClicked() {
        if (!soundOn) {
            return;
        }

        try {
            uiElementSound = AudioSystem.getClip();
            uiElementSound.open(AudioSystem.getAudioInputStream(new File("Sounds/Click.wav")));
        } catch (Exception e) {}
        
        uiElementSound.start();
        uiElementSound.setFramePosition(0);
    }

    @SuppressWarnings("UseSpecificCatch")
    public static void invalidTileClicked() {
        if (!soundOn) {
            return;
        }

        try {
            invalidTileSound = AudioSystem.getClip();
            invalidTileSound.open(AudioSystem.getAudioInputStream(new File("Sounds/InvalidTile.wav")));
        } catch (Exception e) {}

        invalidTileSound.start();
        invalidTileSound.setFramePosition(0);
    }

    @SuppressWarnings("UseSpecificCatch")
    public static void validTileClicked() {
        if (!soundOn) {
            return;
        }

        try {
            validTileSound = AudioSystem.getClip();
            validTileSound.open(AudioSystem.getAudioInputStream(new File("Sounds/Woosh.wav")));
        } catch (Exception e) {}

        validTileSound.start();
        validTileSound.setFramePosition(0);
    }

    @SuppressWarnings("UseSpecificCatch")
    public static void cloudClicked() {
        if (!soundOn) {
            return;
        }

        try {
            validTileSound = AudioSystem.getClip();
            validTileSound.open(AudioSystem.getAudioInputStream(new File("Sounds/Woosh.wav")));
        } catch (Exception e) {}

        validTileSound.start();
        validTileSound.setFramePosition(0);
    }

    public static int toggleSound() {
        soundOn = !soundOn;
        if (soundOn) {
            return 0; //0 is always the number for the default state
        }
        else
        {
            return 1;
        }
    }

    public static boolean isEnabled() {
        return soundOn;
    }
}