package in.kyle.ezskypeezlife.internal.packet;

import com.google.gson.JsonObject;
import in.kyle.ezskypeezlife.EzSkype;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kyle on 10/5/2015.
 */
@ToString(of = {"url", "request", "postData", "contentType", "timeout", "proxy"})
public class WebConnectionBuilder {
    
    @Setter
    @Getter
    private String url;
    @Setter
    private HTTPRequest request;
    @Getter
    private StringBuilder postData;
    @Getter
    private Map<String, String> headers;
    @Setter
    private ContentType contentType;
    @Setter
    private int timeout = -1;
    @Getter
    private HttpURLConnection connection;
    @Setter
    private boolean showErrors = true;
    @Setter
    private Proxy proxy;
    @Setter
    private InputStream writeStream;
    @Setter
    private Logger logger;
    
    public WebConnectionBuilder() {
        this.request = HTTPRequest.GET;
        this.postData = new StringBuilder();
        this.headers = new HashMap<>();
        this.contentType = ContentType.WWW_FORM;
        this.logger = EzSkype.LOGGER;
    }
    
    public void addHeaders(EzSkype ezSkype) {
        addHeader("RegistrationToken", ezSkype.getSkypeSession().getRegToken());
        addHeader("X-Skypetoken", ezSkype.getSkypeSession().getXToken());
    }
    
    /**
     * Adds a non encoded HTTP request header
     *
     * @param key   - The header key
     * @param value - The header value
     */
    public WebConnectionBuilder addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }
    
    /**
     * Sets the location prefix of the remote server
     * (String format %s...)
     * This only for urls that need it
     *
     * @param prefix - The prefix of the server EG: bs1-
     */
    public WebConnectionBuilder locationPrefix(String prefix) {
        this.url = String.format(url, prefix);
        return this;
    }
    
    /**
     * Adds a encrypted post parameter to the request
     *
     * @param key   - The key
     * @param value - The value, will be encrypted
     */
    public WebConnectionBuilder addEncodedPostData(String key, String value) {
        try {
            addPostData(key, URLEncoder.encode(value, "UTF-8"));
        } catch (UnsupportedEncodingException ignored) {
        }
        return this;
    }
    
    /**
     * Adds a post parameter to the request
     *
     * @param key   - The key
     * @param value - The value, will be encrypted
     */
    public WebConnectionBuilder addPostData(String key, String value) {
        if (postData.length() != 0) {
            postData.append("&");
        }
        postData.append(key).append("=").append(value);
        return this;
    }
    
    /**
     * Sets the data to post if the request type is POST
     *
     * @param postData - The data to be posted, must be encrypted first
     */
    public WebConnectionBuilder setPostData(String postData) {
        this.postData = new StringBuilder(postData);
        return this;
    }
    
    public Document getAsDocument() throws IOException {
        return Jsoup.parse(send());
    }
    
    /**
     * Executes the web request
     *
     * @return - The string response from the web server
     * @throws IOException
     */
    public String send() throws IOException {
        logger.debug("  Opening connection {} to: {}", request.name(), url);
        if (proxy == null) {
            connection = (HttpURLConnection) new URL(url).openConnection();
        } else {
            connection = (HttpURLConnection) new URL(url).openConnection(proxy);
        }
        connection.setRequestProperty("Content-Type", contentType.getValue());
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64)");
        connection.setRequestMethod(request.name());
    
        if (timeout != -1) {
            connection.setConnectTimeout(timeout);
        }
        
        headers.forEach(connection::addRequestProperty);
    
        logger.debug("  Connection info: {}", this.toString());
        logger.debug("  Headers: {}", getHeaders());
        if (request == HTTPRequest.POST || request == HTTPRequest.PUT) {
            connection.setDoOutput(true);
            byte[] data;
    
            if (writeStream != null) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                IOUtils.copy(writeStream, os);
                data = os.toByteArray();
            } else {
                data = postData.toString().getBytes("UTF-8");
            }
    
            logger.debug("  Posting data: {}", postData);
            connection.addRequestProperty("Content-Length", Integer.toString(data.length));
            connection.getOutputStream().write(data);
            connection.getOutputStream().close();
        }
        
        InputStream inputStream;
        
        try {
            inputStream = connection.getInputStream();
        } catch (Exception e) {
            if (showErrors) {
                if (connection != null && connection.getErrorStream() != null) {
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(connection.getErrorStream(), writer);
                    String string = writer.toString();
                    logger.error("  An error occurred while sending data to server\n  Data:\n" + string, e);
                } else {
                    logger.error("  An error occurred while sending data to server\n  In = null, Connection: " + connection, e);
                }
            }
    
            throw e;
        }
        
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            response.append("\n").append(line);
        }
        connection.getInputStream().close();
    
        if (response.length() < 1000) {
            logger.debug("  Response: (code={}, data={})", connection.getResponseCode(), response.toString().replace("\n", ""));
        }
    
        return response.toString();
    }
    
    public JsonObject getAsJsonObject() throws IOException {
        return EzSkype.GSON.fromJson(send(), JsonObject.class);
    }
}
