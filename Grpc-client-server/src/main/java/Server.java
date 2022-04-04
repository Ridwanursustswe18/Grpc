import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {
        io.grpc.Server server = ServerBuilder.forPort(2047).addService(new ServiceUser()).build();
        server.start();
        logger.info("Server started at port " + server.getPort());

        server.awaitTermination();
    }
}
