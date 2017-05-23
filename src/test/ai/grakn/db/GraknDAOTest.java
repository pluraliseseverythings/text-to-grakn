package ai.grakn.db;

import ai.grakn.Grakn;
import ai.grakn.GraknGraph;
import ai.grakn.GraknSession;
import ai.grakn.GraknTxType;
import org.junit.Test;

import static org.junit.Assert.*;

public class GraknDAOTest {

    private static final String SOME_KEYSPACE = "some_keyspace";

    @Test
    public void init() throws Exception {
        GraknSession graph = Grakn.session(Grakn.IN_MEMORY, SOME_KEYSPACE);
        new GraknDAO(graph).init();
    }

    @Test
    public void insertRelation() throws Exception {
    }

}