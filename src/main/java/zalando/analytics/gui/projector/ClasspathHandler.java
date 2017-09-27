package zalando.analytics.gui.projector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.*;
import java.net.URISyntaxException;

/**
 * Jersey Web handler method taken from tutorial
 */
@Path("/")
public class ClasspathHandler {

    // The main directory on classpath
    public static final String MAIN_WEB_FILE_DIRECTORY = "static";

    // Logger for this class
    private Logger logger = LogManager.getLogger(ClasspathHandler.class);

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public String indexPage(@Context UriInfo uriInfo) {
        logger.info("/ requested");
        return html("index");
    }

    @GET
    @Path("{file}.html")
    @Produces(MediaType.TEXT_HTML)
    public String html(@PathParam("file") String file) {
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream(
                            MAIN_WEB_FILE_DIRECTORY + "/" + file + ".html"), "UTF8"));

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = br.readLine();
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            return "";
        }
    }

    @GET
    @Path("{file}.css")
    @Produces("text/css")
    public String css(@PathParam("file") String file) {
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream(
                            MAIN_WEB_FILE_DIRECTORY + "/" + file + ".css"), "UTF8"));


            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = br.readLine();
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            return "";
        }
    }

    @GET
    @Path("{file}.js")
    @Produces("text/javascript")
    public String javascript(@PathParam("file") String file) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream(
                            MAIN_WEB_FILE_DIRECTORY + "/" + file + ".js"), "UTF8"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = br.readLine();
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            return "";
        }
    }

    @GET
    @Path("{file}.png")
    @Produces("image/png")
    public byte[] getPNG(@PathParam("file") String filename) throws URISyntaxException {
        logger.info(filename + " requested");

        InputStream in = this.getClass().getClassLoader().getResourceAsStream(
                MAIN_WEB_FILE_DIRECTORY + "/" + filename + ".png");

        byte[] contents = new byte[20000];
        try {
            in.read(contents);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contents;
    }

    @GET
    @Path("{file}")
    public Response file(@PathParam("file") String file) {
        return Response.status(404).entity("Could not find '" + file + "'!").build();
    }

}
