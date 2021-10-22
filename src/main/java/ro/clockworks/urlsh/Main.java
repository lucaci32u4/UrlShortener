package ro.clockworks.urlsh;

import lombok.Getter;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "UrlShortener", version = "UrlShortener 1.0", mixinStandardHelpOptions = true)
public class Main implements Callable<Integer> {

    @CommandLine.Option(names = {"--host", "-H"}, description = "Server address to listen on")
    @Getter
    private String host = "127.0.0.1";

    @CommandLine.Option(names = {"--port", "-p"}, description = "Server port to listen on")
    @Getter
    private int port = 6565;

    @CommandLine.Option(names = {"--store", "-s"}, description = "File where to store URL mappings", required = true)
    @Getter
    private String mvStore = "";

    @CommandLine.Option(names = {"--cache", "-c"}, description = "Cache size, in MBs")
    @Getter
    private int cacheMB = 16;

    @CommandLine.Option(names = { "--title" }, description = "Page title to display", defaultValue = "Clockworks")
    @Getter
    private String title;

    @Override
    public Integer call() throws Exception {

        MappingManager mappingManager = new MappingManager(this);

        Server server = new Server(this, mappingManager);

        Runtime.getRuntime().addShutdownHook(new Thread(mappingManager::close));
        Runtime.getRuntime().addShutdownHook(new Thread(server::close));

        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
    }
}
