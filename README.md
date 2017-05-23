# text-to-grakn
Simple utility that extracts relationships from text and builds a Grakn graph.
 
## How to run
Make sure there's an instance of Grakn running then run as  

```bash
mvn package
java -jar target/text-to-grakn-1.0-SNAPSHOT.jar --keyspace myexample --text-path src/resources/sample_text.txt --grakn-uri localhost:4567
```

Then check the content on the web ui, e.g. on _localhost:4567_

## TODO
Right now a lot of useless information is stored. It would be helpful
to limit the relationships to only the most specific ones (e.g. "Italy is a parliamentary republic"
instead of "Italy is a republic")