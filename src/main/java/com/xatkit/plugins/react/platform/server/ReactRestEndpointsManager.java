package com.xatkit.plugins.react.platform.server;

import com.xatkit.core.XatkitException;
import com.xatkit.core.server.HttpMethod;
import com.xatkit.core.server.RestHandlerException;
import com.xatkit.core.server.RestHandlerFactory;
import com.xatkit.core.server.XatkitServer;
import com.xatkit.core.server.XatkitServerUtils;
import com.xatkit.plugins.react.platform.utils.ReactUtils;
import org.apache.commons.configuration2.Configuration;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * Manages the REST endpoints of the {@link com.xatkit.plugins.react.platform.ReactPlatform}.
 * <p>
 * This class registers the {@code admin/*} handlers that are used to test a react-based bot through the browser.
 */
public class ReactRestEndpointsManager {

    /**
     * The pattern used to print the location of the Xatkit server in the rendered {@code admin.html} file.
     */
    private static final String BASE_SERVER_LOCATION_PATTERN = "#xatkit\\.server";

    /**
     * The pattern used to print the location of the ReactPlatform's server in the rendered {@code admin.html} file.
     */
    private static final String SERVER_LOCATION_PATTERN = "#xatkit\\.react_server";

    /**
     * The pattern used to print the username in the rendered {@code admin.html} file.
     * <p>
     * This pattern is replaced with a random name from {@link #TEST_CLIENT_NAMES} when the page is displayed to
     * simulate multiple users.
     */
    private static final String USERNAME_PATTERN = "#xatkit\\.username";

    /**
     * A {@link List} of test client names used to render the {@code /admin} page and simulate multiple users.
     *
     * @see #TEMPLATE_FILLED_COUNT
     */
    private static List<String> TEST_CLIENT_NAMES = Arrays.asList("Bob", "Alice", "Gwendal", "Jordi");

    /**
     * The {@link List} of CSS files that can be served by the endpoints.
     */
    private static List<String> CSS_FILE_PATHS = Arrays.asList("/admin/css/xatkit.min.css");

    /**
     * The {@link List} of JS files that can be served by the endpoints.
     */
    private static List<String> JS_FILE_PATHS = Arrays.asList("/admin/js/xatkit.min.js");

    /**
     * A counter used to render the {@code /admin} page with random client names.
     *
     * @see #TEST_CLIENT_NAMES
     */
    private static int TEMPLATE_FILLED_COUNT = 0;

    /**
     * The {@link XatkitServer} to register the endpoints to.
     */
    private XatkitServer xatkitServer;

    /**
     * The location of the ReactPlatform's server managing the socket.io connexion.
     */
    private String reactServerURL;

    /**
     * The Xatkit server location (public URL and port) to use from the returned HTML and Javascript files.
     *
     * @see XatkitServerUtils#SERVER_PUBLIC_URL_KEY
     * @see XatkitServerUtils#DEFAULT_SERVER_LOCATION
     */
    private String serverURL;

    /**
     * A flag specifying whether the testing page should be enabled or not.
     * <p>
     * This flag is initialized from the provided {@link Configuration} (see
     * {@link ReactUtils#REACT_ENABLE_TESTING_PAGE}). If enabled, the testing page can be accessed at {@code
     * public_url/admin}, where {@code public_url} is either the specified URL of the Xatkit server, or the default
     * {@code localhost:5000} value.
     *
     * @see ReactUtils#REACT_ENABLE_TESTING_PAGE
     */
    private boolean enableTestingPage;

    /**
     * Constructs a {@link ReactRestEndpointsManager} with the provided {@code xatkitServer} and {@code configuration}.
     *
     * @param xatkitServer  the {@link XatkitServer} to register the endpoints to
     * @param configuration the Xatkit configuration
     */
    public ReactRestEndpointsManager(XatkitServer xatkitServer, Configuration configuration) {
        this.xatkitServer = xatkitServer;
        int socketServerPort = configuration.getInt(ReactUtils.REACT_SERVER_PORT_KEY,
                ReactUtils.DEFAULT_REACT_SERVER_PORT);

        this.reactServerURL = configuration.getString(ReactUtils.REACT_SERVER_PUBLIC_URL,
                "http://localhost:" + socketServerPort);
        int xatkitServerPort = configuration.getInt(XatkitServerUtils.SERVER_PORT_KEY,
                XatkitServerUtils.DEFAULT_SERVER_PORT);
        this.serverURL = configuration.getString(XatkitServerUtils.SERVER_PUBLIC_URL_KEY,
                XatkitServerUtils.DEFAULT_SERVER_LOCATION + ":" + xatkitServerPort);
        this.enableTestingPage = configuration.getBoolean(ReactUtils.REACT_ENABLE_TESTING_PAGE,
                ReactUtils.DEFAULT_REACT_ENABLE_TESTING_PAGE);
    }

    /**
     * Registers the REST endpoints if the testing page is enabled in the Xatkit {@link Configuration}.
     * <p>
     * This method registers the {@code admin/*} handlers that are used to test a react-based bot through the browser.
     *
     * @see ReactUtils#REACT_ENABLE_TESTING_PAGE
     */
    public void registerRestEndpoints() {
        if (enableTestingPage) {
            this.registerAdminHTMLEndpoint();
            this.registerAdminCSSEndpoints();
            this.registerAdminJSEndpoints();
        }
    }

    /**
     * Registers the endpoint returning the HTML testing page located at {@code /admin}.
     */
    private void registerAdminHTMLEndpoint() {
        this.xatkitServer.registerRestEndpoint(HttpMethod.GET, "/admin",
                RestHandlerFactory.createEmptyContentRestHandler((headers, params, content) -> {
                    InputStream is = this.getClass().getClassLoader().getResourceAsStream("admin/admin.html");
                    if (isNull(is)) {
                        throw new RestHandlerException("Cannot return the resource admin/admin.html");
                    }
                    BasicHttpEntity entity = new BasicHttpEntity();
                    InputStream entityContent = replaceHtmlTemplates(is);
                    entity.setContent(entityContent);
                    entity.setContentType(ContentType.TEXT_HTML.getMimeType());
                    entity.setContentEncoding(StandardCharsets.UTF_8.name());
                    return entity;
                }));
    }

    /**
     * Registers the endpoints returning the CSS files accessed by the testing page.
     */
    private void registerAdminCSSEndpoints() {
        for (String cssPath : CSS_FILE_PATHS) {
            this.xatkitServer.registerRestEndpoint(HttpMethod.GET, cssPath,
                    RestHandlerFactory.createEmptyContentRestHandler((headers, params, content) -> {
                        return createEntityForAdminResource(cssPath.substring(1));
                    }));
        }
    }

    /**
     * Registers the endpoints returning the JS files accessed by the testing page.
     */
    private void registerAdminJSEndpoints() {
        for (String jsPath : JS_FILE_PATHS) {
            this.xatkitServer.registerRestEndpoint(HttpMethod.GET, jsPath,
                    RestHandlerFactory.createEmptyContentRestHandler((headers, params, content) -> {
                        return createEntityForAdminResource(jsPath.substring(1));
                    }));
        }
    }

    /**
     * Replaces the template values in the provided {@code from} {@link InputStream} by {@link Configuration} values.
     * <p>
     * This method ensures that the chatbox integrated in the testing page targets the socket.io server (using the
     * public URL and port number specified in the {@link Configuration}).
     *
     * @param from the {@link InputStream} to replace the template values from
     * @return an {@link InputStream} with the template values replaced
     * @throws XatkitException if an error occurred when processing the {@code from} {@link InputStream}
     */
    private InputStream replaceHtmlTemplates(InputStream from) {
        TEMPLATE_FILLED_COUNT++;
        BufferedReader reader = new BufferedReader(new InputStreamReader(from));
        StringBuilder builder = new StringBuilder();
        try {
            while (reader.ready()) {
                builder.append(reader.readLine());
            }
        } catch (IOException e) {
            throw new XatkitException(MessageFormat.format("An error occurred when replacing templates in {0}, see " +
                    "attached exception", this.getClass().getSimpleName()), e);
        }
        String content = builder.toString();
        content = content.replaceAll(SERVER_LOCATION_PATTERN, reactServerURL);
        String clientName = TEST_CLIENT_NAMES.get(TEMPLATE_FILLED_COUNT % TEST_CLIENT_NAMES.size());
        content = content.replaceAll(USERNAME_PATTERN, clientName);
        content = content.replaceAll(BASE_SERVER_LOCATION_PATTERN, serverURL);
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates an {@link HttpEntity} for the provided {@code uri} representing an {@code /admin} resource.
     * <p>
     * This {@link HttpEntity} is returned as is by the {@link XatkitServer}.
     *
     * @param uri the URI of the {@code /admin} resource to wrap in an {@link HttpEntity}
     * @return the created {@link HttpEntity}
     * @throws RestHandlerException if the provided {@code uri} does not correspond to an existing file in the classpath
     */
    private HttpEntity createEntityForAdminResource(String uri) throws RestHandlerException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(uri);
        if (isNull(is)) {
            throw new RestHandlerException(MessageFormat.format("Cannot return the resource {0}", uri));
        }
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(is);
        if (uri.endsWith(".css")) {
            entity.setContentType("text/css");
        } else if (uri.endsWith(".js")) {
            entity.setContentType("application/javascript");
        }
        entity.setContentEncoding(StandardCharsets.UTF_8.name());
        return entity;
    }
}
