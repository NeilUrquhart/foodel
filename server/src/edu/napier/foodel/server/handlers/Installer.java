package edu.napier.foodel.server.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.napier.foodel.server.HTMLpage;
import edu.napier.foodel.server.ServerProperties;
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
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.IOUtils;

/**
 * Encapsulates all the functionality required to allow users to get an install
 */
public class Installer {

    /**
     * Hashmap to store installs and their location
     */
    private static final Map<String, Path> installs = new HashMap<>();


    @Context("/install")
    public int install(Request req, Response res) throws IOException {
        HTMLpage page = new HTMLpage("Foodel Installer");

        // Add specific styling for the home page
        page.addToHeader("<link rel='stylesheet' href='/static/home.css'>");
        page.addToHeader("<link rel='stylesheet' href='/static/installer.css'>");
        page.addToHeader("<script type=\"text/javascript\" src='/static/installer.js'></script");

        String content = Files.readString(Paths.get("config/installer.html"), StandardCharsets.UTF_8);
        page.addToBody(content);
        res.getHeaders().add("Content-Type", "text/html");
        res.send(200, page.html());

        return 0;
    }

    /**
     * create an install via a POST request
     */
    @Context(value = "/installer/create", methods = {"POST"})
    public int createInstall(Request req, Response res) throws IOException {

        // get params from the post request
        Map<String, String> params = req.getParams();

        // Return JSON since the job was created from a javascript function
        // easier to deal with json
        res.getHeaders().add("Content-Type", "application/json");

        // Create a temporary directory for the install. Don't necessarily want to store it forever.
        Path tempDir = Files.createTempDirectory("foodel_");

        // use directory as a hash id for the job. Will allow us to retrieve it later
        String randomHash = tempDir.getFileName().toString();
        installs.put(randomHash, tempDir);

        HashMap<String, String> k = new HashMap<>();
        createInstaller(params, randomHash);

        k.put("zip_hash", randomHash);

        String json = new Gson().toJson(k);
        res.send(200, json);
        return 0;
    }

    /**
     * Download a zip of an install
     */
    @Context("/installer/download")
    public int downloadZip(Request req, Response res) throws IOException {

        // id of the zip is contained in the headers
        if (req.getParams().containsKey("id")) {
            String hash = req.getParams().get("id");

            // get the zip file
            File zip = Path.of(installs.get(hash).toString(), "foodel.zip").toFile();

            // set the required headers to send a file
            res.getHeaders().add("Content-Disposition", "attachment; filename=\"foodel.zip\"");
            res.getHeaders().add("Content-Length", String.valueOf(zip.length()));
            res.getHeaders().add("Content-Type", "application/zip");

            // get an input stream of the zip
            InputStream fs = new FileInputStream(zip);

            // can't use the send method because we're not sending text
            // send the headers and status first and then send the body
            res.sendHeaders(200);
            res.sendBody(fs, -1, null);
        } else {
            res.send(404, "This install archive doesn't exist or is no longer available.");
        }

        return 0;
    }

    /**
     * Create an install
     *
     * @param map  the parameters submitted through the post request
     * @param hash the id of the install job
     * @throws IOException ioexception
     */
    private void createInstaller(Map<String, String> map, String hash) throws IOException {

        // get resources via http. may be worth caching the results
        HttpURLConnection connection = null;

        // this is the github API endpoint
        final URL releasesEndpoint = new URL("https://api.github.com/repos/chriswales95/FoodelFake/releases/latest");
        String accessToken = null;

        // open the HTTP connection
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

        // set the HTTP headers.
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

        // get the JSON string
        InputStream is = connection.getInputStream();
        String json = IOUtils.toString(is, "UTF-8");
        is.close();

        // use Gson to get a map of the results
        HashMap<String, Object> resultMap = new Gson().fromJson(
                json, new TypeToken<HashMap<String, Object>>() {
                }.getType()
        );

        String exeId = null;
        String jarId = null;
        String jreId = null;

        // 200 signals that the request finished normally
        if (connection.getResponseCode() == 200) {
            HashMap<String, Path> assets = new HashMap<>();

            // the response includes a key called 'assets' which details files from the release.
            if (resultMap.containsKey("assets")) {
                ArrayList<Map<String, Object>> treemap = (ArrayList<Map<String, Object>>) resultMap.get("assets");

                // iterate over the map entries and pick out relevant assets
                for (Map<String, Object> entry : treemap) {
                    if (entry.containsKey("name")) {
                        if (entry.get("name").equals("foodel.exe")) {
                            exeId = new DecimalFormat("#").format(entry.get("id"));
                        } else if (entry.get("name").toString().endsWith("dependencies.jar")) {
                            jarId = new DecimalFormat("#").format(entry.get("id"));
                        } else if (entry.get("name").toString().endsWith("jre.zip")) {
                            jreId = new DecimalFormat("#").format(entry.get("id"));
                        }
                    }
                }
            }

            // get the directory
            Path tempDir = installs.get(hash);

            // Get exe or jar
            if (map.get("os").equals("windows")) {

                // if windows, get the exe and jre
                Path exe = Paths.get(tempDir.toString(), "foodel.exe");
                Path jre = Paths.get(tempDir.toString(), "jre.zip");

                downloadAsset(accessToken, exeId, exe);
                downloadAsset(accessToken, jreId, jre);

                assets.put("exe", exe);
                assets.put("jre", jre);
            } else {

                // if mac, only get the jar
                // would be good to have a dmg file at some point
                Path jar = Paths.get(tempDir.toString(), "foodel.jar");
                downloadAsset(accessToken, jarId, jar);
                assets.put("jar", jar);
            }

            // zip html / js / css etc
            assets.put("config", new File("config").toPath());
            assets.put("html", new File("public_html").toPath());
            assets.put("logs", new File("logs").toPath());

            // zip assets
            zipFolders(tempDir, assets, map);
        }
    }

    /**
     * @param accessToken the access token for GitHub
     * @param assetId     the id of the asset to download via GitHub
     * @param location    where to download the asset
     * @throws IOException ioexception
     */
    private void downloadAsset(String accessToken, String assetId, Path location) throws IOException {

        // Create a HTTP connection
        HttpURLConnection connection;

        // Github asset endpoint
        final URL assetEndpoint = new URL(
                String.format("https://api.github.com/repos/chriswales95/FoodelFake/releases/assets/%s", assetId));

        // Open connection and set headers
        connection = (HttpURLConnection) assetEndpoint.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/octet-stream");

        // Add the access token if we have one
        if (accessToken != null) {
            connection.setRequestProperty("Authorization", String.format("token %s", accessToken));
        }

        // Try and get an input stream and copy it to the file system
        try (InputStream is = connection.getInputStream()) {
            Files.copy(is, location);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create zip folder
     *
     * @param zipDir    where to create the zip
     * @param resources resources downloaded from github
     * @param params    the details from the POST request
     */
    private void zipFolders(Path zipDir, HashMap<String, Path> resources, Map<String, String> params) {
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

                ZipFile j = new ZipFile(resources.get("jre").toFile());
                j.extractAll(zipDir.toString());

                // add the JRE
                zipFile.addFolder(Paths.get(zipDir.toString(), "jdk-16.0.2").toFile());
            }

            zipFile.addFolder(resources.get("html").toFile());

            // loop through the html files and filter to the files we need.
            // basically filter out the footer and default properties
            List<Path> htmlFiles = Files.walk(Path.of(new File("config").toURI()))
                    .filter(Files::isRegularFile)
                    .filter(path ->
                            path.getFileName().toString().contains(".html") &&
                                    !path.getFileName().toString().contains(".properties")
                    ).filter(path -> !path.getFileName().toString().equals("footer.html"))
                    .collect(Collectors.toList());

            // copy filtered files
            Path newConfigirectory = Files.createDirectory(Paths.get(zipDir.toString(), "config"));
            for (Path p : htmlFiles) {
                Path tartget = Path.of(newConfigirectory.toString(), p.getFileName().toString());
                Files.copy(p, tartget);
            }

            // Rather than copy and edit footer, just write a new one since it's small.
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

            // add osm data
            Path dataDirectory = Files.createDirectory(Paths.get(zipDir.toString(), "data"));
            Path osmDirectory = Files.createDirectory(Paths.get(dataDirectory.toString(), "osm"));

            // Find out if we have local osm that we can give the user based on their choice
            ServerProperties properties = ServerProperties.getInstance();

            // Check if osm location is described in the properties file
            if (properties.get("osm_data") != null) {
                String osmFileName = properties.get("osmfile");
                Path osmLocation = new File(properties.get("osm_data")).toPath();
                Path chosenOsmFile;

                switch (params.get("mapArea")) {
                    case "scotland":
                        chosenOsmFile = Paths.get(osmLocation.toString(), "scotland-latest.osm.pbf");
                        break;
                    case "england":
                        chosenOsmFile = Paths.get(osmLocation.toString(), "england-latest.osm.pbf");
                        break;
                    case "wales":
                        chosenOsmFile = Paths.get(osmLocation.toString(), "wales-latest.osm.pbf");
                        break;
                    case "ireland":
                        chosenOsmFile = Paths.get(osmLocation.toString(), "ireland-and-northern-ireland-latest");
                        break;
                    default:
                        chosenOsmFile = Paths.get(osmLocation.toString(), "great-britain-latest.osm.pbf");
                        break;
                }

                // if we have the chosen file, copy it to the zip.
                if (Files.exists(chosenOsmFile)) {
                    Files.copy(chosenOsmFile, Paths.get(osmDirectory.toString(), osmFileName));
                    zipFile.addFolder(dataDirectory.toFile());
                }
            }

            // Add the necessary configuration file
            // shared and local configurations are slightly different


            String chosenConfiguration = params.get("deviceInstallType") + "_config";
            if (properties.get(chosenConfiguration) != null) {
                ZipParameters parameters = new ZipParameters();
                parameters.setFileNameInZip("config/server.properties");

                Path configPath;

                configPath = Path.of(properties.get(chosenConfiguration));

                zipFile.addFile(configPath.toFile(), parameters);
            }

            // add sample files for people to play with
            Path sampleData = Path.of(properties.get("sample_data"));
            zipFile.addFolder(sampleData.toFile());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

