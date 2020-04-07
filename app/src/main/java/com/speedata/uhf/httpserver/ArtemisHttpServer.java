package com.speedata.uhf.httpserver;

import android.util.Log;

import com.koushikdutta.async.http.Multimap;
import com.koushikdutta.async.http.body.AsyncHttpRequestBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.speedata.uhf.MyApp;
import com.speedata.uhf.MyService;
import com.speedata.uhf.adapter.UhfCardBean;
import com.speedata.uhf.adapter.UhfInfoBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xin Qian on 2019/1/17.
 */

public class ArtemisHttpServer implements HttpServerRequestCallback {
    private static final String TAG = "ArtemisHttpServer";

    private static ArtemisHttpServer mInstance;

    public static int PORT_DEFALT = 6789;

    AsyncHttpServer mServer = new AsyncHttpServer();


    public static ArtemisHttpServer getInstance() {
        if (mInstance == null) {
            synchronized (ArtemisHttpServer.class) {
                if (mInstance == null) {
                    mInstance = new ArtemisHttpServer();
                }
            }
        }
        return mInstance;
    }

    public void start() {
        Log.d(TAG, "Starting http server...");
        mServer.get("[\\d\\D]*", this);
        mServer.post("[\\d\\D]*", this);
        mServer.listen(PORT_DEFALT);
    }

    public void stop() {
        Log.d(TAG, "Stopping http server...");
        mServer.stop();
    }

    private void sendResponse(AsyncHttpServerResponse response, JSONObject json) {
        // Enable CORS
        response.getHeaders().add("Access-Control-Allow-Origin", "*");
        response.send(json);
    }

    @Override
    public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
        String uri = request.getPath();
        Log.d(TAG, "onRequest " + uri);

        Object params;
        if (request.getMethod().equals("GET")) {
            params = request.getQuery();
        } else if (request.getMethod().equals("POST")) {
            String contentType = request.getHeaders().get("Content-Type");
            if (contentType.equals("application/json")) {
                params = ((AsyncHttpRequestBody<JSONObject>) request.getBody()).get();
            } else {
                params = ((AsyncHttpRequestBody<Multimap>) request.getBody()).get();
            }
        } else {
            Log.d(TAG, "Unsupported Method");
            return;
        }

        if (params != null) {
            Log.d(TAG, "params = " + params.toString());
        }

        switch (uri) {
            case "/uhf":
                handleDevicesRequest(params, response);
                break;
            default:
                handleInvalidRequest(params, response);
                break;
        }
    }

    private void handleDevicesRequest(Object params, AsyncHttpServerResponse response) {
        // Send JSON format response
        try {
            JSONArray array = new JSONArray();
            for (UhfInfoBean uhfInfoBean : MyApp.list) {
                array.put(uhfInfoBean.toString());
            }
            JSONObject json = new JSONObject();
            json.put("uhf", array.toString());
            MyApp.list.clear();
            sendResponse(response, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleInvalidRequest(Object params, AsyncHttpServerResponse response) {
        JSONObject json = new JSONObject();
        try {
            json.put("error", "Invalid API");
            sendResponse(response, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}