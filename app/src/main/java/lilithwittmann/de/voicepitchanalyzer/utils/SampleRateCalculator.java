package lilithwittmann.de.voicepitchanalyzer.utils;

import android.media.AudioRecord;

/**
 * Created by lilith on 7/6/15.
 */
public class SampleRateCalculator {

    public static int getMaxSupportedSampleRate() {
    /*
     * Valid Audio Sample rates
     *
     * @see <a
     * href="http://en.wikipedia.org/wiki/Sampling_%28signal_processing%29"
     * >Wikipedia</a>
     */
        final int validSampleRates[] = new int[]{
                47250, 44100, 44056, 37800, 32000, 22050, 16000, 11025, 4800, 8000,};
    /*
     * Selecting default audio input source for recording since
     * AudioFormat.CHANNEL_CONFIGURATION_DEFAULT is deprecated and selecting
     * default encoding format.
     */
        for (int i = 0; i < validSampleRates.length; i++) {
            int result = AudioRecord.getMinBufferSize(validSampleRates[i],
                    android.media.AudioFormat.CHANNEL_IN_MONO,
                    android.media.AudioFormat.ENCODING_PCM_16BIT);
            if (result != AudioRecord.ERROR
                    && result != AudioRecord.ERROR_BAD_VALUE && result > 0) {
                // return the mininum supported audio sample rate
                return validSampleRates[i];
            }
        }
        // If none of the sample rates are supported return -1 handle it in
        // calling method
        return -1;
    }
}
