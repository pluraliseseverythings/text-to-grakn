package ai.grakn.text.extract;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations.RelationTriplesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelationExtractor implements Iterator<RelationTriple> {

    private static Logger log = LoggerFactory.getLogger(RelationExtractor.class);

    private final Iterator<String> reader;
    private final StanfordCoreNLP pipeline;
    private Iterator<CoreMap> coreMaps;
    private Iterator<RelationTriple> relationTriples;

    public RelationExtractor(Scanner reader) {
        this.reader = reader.useDelimiter("\\n");
        Properties props = new Properties();
        props.setProperty("openie.resolve_coref", "true");
        props.setProperty("openie.triple.all_nominals", "false");
        props.setProperty("annotators",
            "tokenize,ssplit,parse,pos,lemma,ner,depparse,mention,coref,dcoref,natlog,openie");
        this.pipeline = new StanfordCoreNLP(props);
    }

    private RelationTriple extractNextRelation() {
        if (this.relationTriples != null && this.relationTriples.hasNext()) {
            RelationTriple triple = this.relationTriples.next();
            // Print the triple
            log.info(triple.confidence + ":\t" +
                    triple.subjectLemmaGloss() + "->\t" +
                    triple.relationLemmaGloss() + "->\t" +
                    triple.objectLemmaGloss());
            return triple;
        } else if (this.coreMaps != null && this.coreMaps.hasNext()) {
            CoreMap sentence = this.coreMaps.next();
            // Iterate over the triples in the sentence
            this.relationTriples = sentence.get(RelationTriplesAnnotation.class).iterator();
            return extractNextRelation();
        } else {
            // content is a line with possibly multiple sentences
            Annotation doc = new Annotation(this.reader.next());
            pipeline.annotate(doc);
            this.coreMaps = doc.get(SentencesAnnotation.class).iterator();
            return extractNextRelation();
        }
    }

    @Override
    public boolean hasNext() {
        return (this.relationTriples != null && this.relationTriples.hasNext())
            || (this.coreMaps != null && this.coreMaps.hasNext())
            || this.reader.hasNext();
    }

    @Override
    public RelationTriple next() {
        return extractNextRelation();
    }
}
