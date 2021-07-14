package edu.napier.foodel.server.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.napier.foodel.server.HTMLpage;
import net.freeutils.httpserver.HTTPServer.Context;
import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.IOUtils;

public class Installer {

    private static Map<String, Path> installs = new HashMap<>();


    @Context("/install")
    public int install(Request req, Response res) throws IOException {
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

    @Context(value = "/installer/create", methods = {"POST"})
    public int createInstall(Request req, Response res) throws IOException {

        HTMLpage page = new HTMLpage("Your Install");
        res.getHeaders().add("Content-Type", "application/json");

        Map<String, String> params = req.getParams();
        Path tempDir = Files.createTempDirectory("foodel_");
        String randomHash = tempDir.getFileName().toString();
        installs.put(randomHash, tempDir);
        HashMap<String, String> k = new HashMap<>();
        createInstaller(params, randomHash);

        k.put("zip_hash", randomHash);

        String json = new Gson().toJson(k);
        res.send(200, json);
        return 0;
    }

    @Context("/installer/zip")
    public int downloadZip(Request req, Response res) throws IOException {

        if (req.getParams().containsKey("id")) {
            String hash = req.getParams().get("id");

            File zip = Path.of(installs.get(hash).toString(), "foodel.zip").toFile();
            res.getHeaders().add("Content-Disposition", "attachment; filename=\"foodel.zip\"");
            res.getHeaders().add("Content-Type", "application/zip");

            InputStream fs = new FileInputStream(zip);
            res.sendHeaders(200);
            res.sendBody(fs, -1, null);
        } else {
            res.send(404, ":/");
        }

        return 0;
    }

    private Path createInstaller(Map<String, String> map, String hash) throws IOException {

        HttpURLConnection connection = null;
        final URL releasesEndpoint = new URL("https://api.github.com/repos/chriswales95/FoodelFake/releases/latest");
        String accessToken = null;

        connection = (HttpURLConnection) releasesEndpoint.openConnection();
        connection.setRequestMethod("GET");

        // If the repository is private, you'll need a personal access token.
        // Don't hardcode this value as this allows people to access your account with elevated access rights
        if (System.getProperty("GITHUB_PAT") != null) {
            accessToken = System.getProperty("GITHUB_PAT");
        } else if (System.getenv("GITHUB_PAT") != null) {
            accessToken = System.getenv("GITHUB_PAT");
        }

        // Add the access token to the HTTP header
        if (accessToken != null) {
            connection.setRequestProperty("Authorization", String.format("token %s", accessToken));
        }

        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        InputStream is = connection.getInputStream();
        String json = IOUtils.toString(is, "UTF-8");
        is.close();

        HashMap<String, Object> resultMap = new Gson().fromJson(
                json, new TypeToken<HashMap<String, Object>>() {
                }.getType()
        );

        String exeId = null;
        String jarId = null;

        if (connection.getResponseCode() == 200) {
            HashMap<String, Path> assets = new HashMap<>();

            if (resultMap.containsKey("assets")) {
                ArrayList<Map<String, Object>> treemap = (ArrayList<Map<String, Object>>) resultMap.get("assets");

                for (Map<String, Object> entry : treemap) {
                    if (entry.containsKey("name")) {
                        if (entry.get("name").equals("foodel.exe")) {
                            exeId = new DecimalFormat("#").format(entry.get("id"));
                        } else if (entry.get("name").toString().endsWith("dependencies.jar")) {
                            jarId = new DecimalFormat("#").format(entry.get("id"));
                        }
                    }
                }
            }

            // download assets
            ;
            Path tempDir = installs.get(hash);

            if (map.get("os").equals("windows")) {
                Path exe = Paths.get(tempDir.toString(), "foodel.exe");
                downloadAsset(accessToken, exeId, exe);
                assets.put("exe", exe);
            } else {
                Path jar = Paths.get(tempDir.toString(), "foodel.jar");
                downloadAsset(accessToken, jarId, jar);
                assets.put("jar", jar);
            }

            // zip html / js / css etc
            assets.put("config", new File("config").toPath());
            assets.put("html", new File("public_html").toPath());
            assets.put("logs", new File("logs").toPath());

            // zip everything
            return zipFolders(tempDir, assets, map);
        }
        return null;
    }

    private void downloadAsset(String accessToken, String jarId, Path location) throws IOException {
        HttpURLConnection connection;
        final URL assetEndpoint = new URL(
                String.format("https://api.github.com/repos/chriswales95/FoodelFake/releases/assets/%s", jarId));

        connection = (HttpURLConnection) assetEndpoint.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/octet-stream");

        if (accessToken != null) {
            connection.setRequestProperty("Authorization", String.format("token %s", accessToken));
        }

        try (InputStream is = connection.getInputStream()) {
            Files.copy(is, location);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Path zipFolders(Path zipDir, HashMap<String, Path> resources, Map<String, String> params) throws MalformedURLException {
        String zipName = Paths.get(zipDir.toString(), "foodel.zip").toString();
        ZipFile zipFile = new ZipFile(zipName);

        try {
            if (resources.containsKey("jar")) {
                zipFile.addFile(resources.get("jar").toFile());
                Files.delete(resources.get("jar"));
            }

            if (resources.containsKey("exe")) {
                zipFile.addFile(resources.get("exe").toFile());
                Files.delete(resources.get("exe"));
            }

            zipFile.addFolder(resources.get("html").toFile());

            List<Path> htmlFiles = Files.walk(Path.of(new File("config").toURI()))
                    .filter(Files::isRegularFile)
                    .filter(path ->
                            path.getFileName().toString().contains(".html") ||
                                    path.getFileName().toString().contains(".properties")
                    ).filter(path -> !path.getFileName().toString().equals("footer.html"))
                    .collect(Collectors.toList());

            Path newConfigirectory = Files.createDirectory(Paths.get(zipDir.toString(), "config"));
            for (Path p : htmlFiles) {
                Path tartget = Path.of(newConfigirectory.toString(), p.getFileName().toString());
                Files.copy(p, tartget);
            }

            File footer = new File(String.valueOf(Path.of(newConfigirectory.toString(), "footer.html")));
            FileWriter myWriter = new FileWriter(footer);
            myWriter.write("<br><br><br><br><br><br><br>\n" +
                    "<div class=\"footer\">\n" +
                    "  <h3>" + params.get("org") + "</h3>\n" +
                    "</div>");
            myWriter.close();

            // get the relevant files and add a custom footer
            zipFile.addFolder(newConfigirectory.toFile());

            // only add necessary log file
            Path newLogsDirectory = Files.createDirectory(Paths.get(zipDir.toString(), "logs"));
            Files.copy(
                    new File("logs/foodel.log.lck").toPath(),
                    Path.of(newLogsDirectory.toString(), "foodel.log.lck"));

            zipFile.addFolder(newLogsDirectory.toFile());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return zipFile.getFile().toPath();
    }

    public static Path getDownload(String hash) {
        if (Installer.installs.containsKey(hash)) {
            int i = 1;
        }
        return null;
    }

}

