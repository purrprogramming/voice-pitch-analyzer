package lilithwittmann.de.voicepitchanalyzer;

import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import de.lilithwittmann.voicepitchanalyzer.activities.RecordingActivity;
import de.lilithwittmann.voicepitchanalyzer.utils.AudioPlayer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class AudioPlayerTest extends ActivityUnitTestCase<RecordingActivity> {
    public AudioPlayerTest() {
        super(RecordingActivity.class);
    }

    @SmallTest
    public void testGivenAudioPlayerPlayedOnce_whenReplayed_thenDoesNotThrowException() throws InterruptedException {
        ByteArrayInputStream blankAudio = new ByteArrayInputStream(new byte[0]);
        AudioPlayer player = new AudioPlayer(blankAudio);
        player.play();
        Thread.sleep(100);

        player.play();
    }

    @SmallTest
    public void testGivenActiveAudioPlayer_whenStopped_thenStopsPlaying() throws InterruptedException {
        InputStream audio = loadResource("/test-audio.pcm");
        AudioPlayer player = new AudioPlayer(audio);
        player.play();

        player.stop();
        
        assertFalse(player.isPlaying());
    }

    private InputStream loadResource(String fileName) {
        return AudioPlayerTest.class.getResourceAsStream(fileName);
    }
}
