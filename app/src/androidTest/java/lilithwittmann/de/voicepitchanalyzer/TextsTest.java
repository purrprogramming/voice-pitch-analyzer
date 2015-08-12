package de.lilithwittmann.voicepitchanalyzer;

import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import de.lilithwittmann.voicepitchanalyzer.RecordingListActivity;
import de.lilithwittmann.voicepitchanalyzer.models.Texts;

/**
 * Created by lilith on 7/5/15.
 */
public class TextsTest extends ActivityUnitTestCase<RecordingListActivity> {

    public TextsTest() {
        super(RecordingListActivity.class);
    }

    Texts texts;
    protected void setUp() throws Exception {
        super.setUp();
        this.texts = new Texts();
    }
    @SmallTest
    public void testGetText(){
        this.texts.getText("de", 3);
        assertEquals(1,1);
    }
}
