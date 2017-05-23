package ai.grakn;

import com.beust.jcommander.Parameter;


public class CommandLineArguments {
    @Parameter(names = { "--text-path" }, description = "Path to file containing text to analyse")
    public String textPath;

    @Parameter(names = { "--keyspace" }, description = "Grakn keyspace")
    public String keyspace = "text_to_grakn_default_keyspace";

    @Parameter(names = { "--grakn-uri" }, description = "Grakn URI")
    public String graknUri = Grakn.DEFAULT_URI;
}
