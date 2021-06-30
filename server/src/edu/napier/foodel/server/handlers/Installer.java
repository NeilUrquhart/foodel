package edu.napier.foodel.server.handlers;

import edu.napier.foodel.server.HTMLpage;
import net.freeutils.httpserver.HTTPServer.ContextHandler;
import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Installer implements ContextHandler {

    @Override
    public int serve(Request req, Response res) throws IOException {
        HTMLpage page = new HTMLpage("Foodel Installer");

        // Add specific styling for the home page
        page.addToHeader("<link rel='stylesheet' href='/static/home.css'>");
        page.addToHeader("<script type=\"text/javascript\" src='/static/installer.js'></script");

        String content = Files.readString(Paths.get("config/installer.html"), StandardCharsets.UTF_8);
        page.addToBody(content);
        res.getHeaders().add("Content-Type", "text/html");
        res.send(200, page.html());

        return 0;
    }

}