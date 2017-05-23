package ai.grakn.text.extract;

import java.io.File;
import java.util.Scanner;
import org.junit.Test;

public class RelationExtractorTest {
    @Test
    public void extractRelations() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("sample_text_short.txt").getFile());
        RelationExtractor relationExtractor = new RelationExtractor(new Scanner(file));
        while(relationExtractor.hasNext()) {
            relationExtractor.next();
        }
    }

}