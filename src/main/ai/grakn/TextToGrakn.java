package ai.grakn;

import ai.grakn.db.GraknDAO;
import ai.grakn.text.extract.RelationExtractor;
import com.beust.jcommander.JCommander;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import edu.stanford.nlp.ie.util.RelationTriple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextToGrakn {

    private static Logger log = LoggerFactory.getLogger(TextToGrakn.class);

    public static void main(String[] args) {
        CommandLineArguments commandLineArguments = new CommandLineArguments();
        JCommander.newBuilder()
            .addObject(commandLineArguments)
            .build()
            .parse(args);

        String textPath = commandLineArguments.textPath;
        Scanner reader = null;
        try {
            reader = new Scanner(new FileReader(textPath));
        } catch (FileNotFoundException e) {
            log.error("Could not find given text file {}", textPath);
            System.exit(-1);
        }
        RelationExtractor relationExtractor = new RelationExtractor(reader);
        GraknSession session = Grakn.session(commandLineArguments.graknUri, commandLineArguments.keyspace);
        GraknDAO graknDAO = new GraknDAO(session);
        log.info("Initializing new graph");
        boolean initialized = graknDAO.init();
        if (!initialized) {
            log.error("Could not initialize graph, terminating");
            System.exit(-1);
        }
        while (relationExtractor.hasNext()) {
            graknDAO.insertRelation(relationExtractor.next());
        }
        graknDAO.close();
    }


}
