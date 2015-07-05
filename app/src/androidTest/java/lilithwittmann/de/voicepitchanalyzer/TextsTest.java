package lilithwittmann.de.voicepitchanalyzer;

import android.app.Application;
import android.test.ActivityUnitTestCase;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import lilithwittmann.de.voicepitchanalyzer.models.Texts;

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
