package ro.clockworks.urlsh;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.http.util.RateLimit;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Server {
    private static final Pattern idurlPattern = Pattern.compile("^[a-z]+$");
    private static final Pattern protoPattern = Pattern.compile("^[a-zA-Z0-9]+://.+$");

    private final Javalin server;
    private final MappingManager mappingManager;

    public Server(Main params, MappingManager mappingManager) {
        this.mappingManager = mappingManager;
        server = Javalin.create(c -> {
            c.showJavalinBanner = false;
        });

        server.get("/", ctx -> {
            ctx.res.setContentType("text/html");
            ctx.result(Server.class.getResourceAsStream("/site/index.html"));
        });
        serveResource("bootstrap.css", "text/css");
        serveResource("bootstrap.js", "text/javascript");
        serveResource("jquery.js", "text/javascript");
        serveResource("main.js", "text/javascript");
        serveResource("popper.js", "text/javascript");

        server.post("/", ctx -> {
            try {
                new RateLimit(ctx).requestPerTimeUnit(15, TimeUnit.MINUTES);
            } catch (RuntimeException e) {
                ctx.status(429); // too many requests
                return;
            }

            String body = ctx.body();

            if (body.isEmpty() || body.isBlank() || !body.contains(".")) {
                ctx.status(403); // go to hell
                return;
            }


            body = body.strip();
            if (!body.startsWith("https://") && !body.startsWith("http://")) {
                if (protoPattern.matcher(body).matches()) {
                    ctx.status(400); // bad protocol
                    return;
                }
                body = "http://" + body;
            }

            String proto =  (body.startsWith("https://")) ? "https://" :  "http://";


            String urlid = mappingManager.createMapping(body);
            String result = proto + ctx.host() + "/" + urlid;
            ctx.result(result);
        });

        server.get("/:urlid", ctx -> {
            String urlid = ctx.pathParam("urlid");
            if (!idurlPattern.matcher(urlid).matches()) {
                ctx.status(403); // go to hell
                return;
            }
            String location = mappingManager.getMapping(urlid);
            if (location == null) {
                ctx.status(404);
                return;
            }
            ctx.redirect(location);

        });

        server.start(params.getHost(), params.getPort());
    }

    public void serveResource(String res, String mime) {
        String resp = "/" + res;
        String resr = "/site/" + res;
        server.get(resp, ctx -> {
            ctx.res.setContentType(mime);
            ctx.result(Server.class.getResourceAsStream(resr));
        });
    }

    public void close() {
        server.stop();
    }
}
