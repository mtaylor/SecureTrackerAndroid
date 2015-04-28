package com.tracker.secure.securetracker;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by mtaylor on 28/04/15.
 */
public class RESTHandler {

    private static final String endpoint = "http://securetracker-mtaylor.rhcloud.com/";

    private static final RESTHandler restHandler = new RESTHandler();

    private HttpClient httpClient;

    private RESTHandler()
    {
        httpClient = new DefaultHttpClient();
    }

    public static RESTHandler getInstance()
    {
        return restHandler;
    }

    public void create(String path, String json) throws IOException {
        StringEntity stringEntity = new StringEntity(json);
        HttpPost post = new HttpPost(endpoint + path);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(stringEntity);
        httpClient.execute(post);
    }
}
