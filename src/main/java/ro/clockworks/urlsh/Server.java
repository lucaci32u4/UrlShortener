package ro.clockworks.urlsh;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.http.util.RateLimit;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.DefaultTemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Server {
    private static final Pattern idurlPattern = Pattern.compile("^[a-z]+$");
    private static final Pattern protoPattern = Pattern.compile("^[a-zA-Z0-9]+://.+$");

    private final Javalin server;
    private final MappingManager mappingManager;
    private final TemplateEngine templateEngine = new TemplateEngine();
    private final Context templateContext = new Context();

    public Server(Main params, MappingManager mappingManager) {

        ClassLoaderTemplateResolver defaultTemplateResolver = new ClassLoaderTemplateResolver();
        defaultTemplateResolver.setTemplateMode(TemplateMode.HTML);
        defaultTemplateResolver.setPrefix("/site/");
        defaultTemplateResolver.setSuffix(".html");
        defaultTemplateResolver.setCharacterEncoding("UTF-8");
        defaultTemplateResolver.setCacheable(true);
        templateEngine.setTemplateResolver(defaultTemplateResolver);
        templateContext.setVariable("title", params.getTitle());

        this.mappingManager = mappingManager;
        server = Javalin.create(c -> {
            c.showJavalinBanner = false;
        });

        server.get("/", ctx -> {
            ctx.res.setContentType("text/html");
            ctx.result(templateEngine.process("index.html", templateContext));
        });
        serveResource("favicon.ico", "image/png");
        serveResource("_bootstrap.css", "text/css");
        serveResource("_bootstrap.js", "text/javascript");
        serveResource("_jquery.js", "text/javascript");
        serveResource("_main.js", "text/javascript");
        serveResource("_popper.js", "text/javascript");

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
                body = "https://" + body;
            }

            String proto = (body.startsWith("https://")) ? "https://" : "http://";


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
            var out = Server.class.getResourceAsStream(resr);
            if (out == null) {
                ctx.status(404);
                return;
            }
            ctx.res.setContentType(mime);
            ctx.result(out);
        });
    }

    public void close() {
        mappingManager.close();
        server.stop();
    }
}
