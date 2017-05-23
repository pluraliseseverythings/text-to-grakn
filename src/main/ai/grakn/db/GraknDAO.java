package ai.grakn.db;

import ai.grakn.GraknGraph;
import ai.grakn.GraknSession;
import ai.grakn.GraknTxType;
import ai.grakn.concept.*;
import edu.stanford.nlp.ie.util.RelationTriple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * We are keeping things simple. Everything in the KB is an "entity". Relations have entities
 * as subject and object.
 */
public class GraknDAO {
    private static Logger log = LoggerFactory.getLogger(GraknDAO.class);

    private final GraknSession session;
    private GraknGraph graph;
    private boolean initialized = false;
    private EntityType sentenceEntity = null;
    private ResourceType<String> entityName;
    private ResourceType<String> relationshipName;
    private RoleType rObject;
    private RoleType rSubject;
    private RelationType relationType;

    public GraknDAO(GraknSession session) {
        this.session = session;
    }

    public void init() {
        this.graph = session.open(GraknTxType.WRITE);
        this.sentenceEntity = graph.putEntityType("sentence-entity");
        this.entityName = graph.putResourceType("entity-name", ResourceType.DataType.STRING);
        this.sentenceEntity.resource(entityName);
        this.rSubject = graph.putRoleType(String.join("-", "relationship", "subject"));
        this.rObject = graph.putRoleType(String.join("-", "relationship", "object"));
        this.sentenceEntity.plays(rSubject);
        this.sentenceEntity.plays(rObject);
        this.relationshipName = graph.putResourceType("relationship-name", ResourceType.DataType.STRING);
        this.relationType = graph
                .putRelationType("sentence-relationship")
                .relates(rSubject)
                .relates(rObject);
        this.relationType.resource(relationshipName);
        this.initialized = true;
    }

    public void insertRelation(RelationTriple relation) {
        if (!initialized) {
            throw new RuntimeException("Grakn not initialized");
        }
        Entity subjectEntity = putEntity(relation.subjectLemmaGloss());
        Entity objectEntity = putEntity(relation.objectLemmaGloss());

        Resource<String> rName = relationshipName.putResource(relation.relationLemmaGloss());
        Relation r = relationType.addRelation().addRolePlayer(rSubject, subjectEntity).addRolePlayer(rObject, objectEntity);
        r.resource(rName);
    }

    private Entity putEntity(String name) {
        Resource<String> subjectName = entityName
                .putResource(name);
        Entity subjectEntity = sentenceEntity.addEntity().resource(subjectName);
        log.info("Added {} to db", subjectName.getValue());
        return subjectEntity;
    }

    public void close() {
        graph.commit();
    }
}
