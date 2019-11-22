package org.bbr.examples.utils;

import com.google.gson.Gson;
import org.bbr.examples.service.system.web.Request;
import org.bbr.examples.service.system.web.Response;

public class JsonProcessor {

    public Request jsonStringToRequest(String payload) {
        return new Gson().fromJson(payload, Request.class);
    }

    public Response jsonStringToResponse(String payload) {
        return new Gson().fromJson(payload, Response.class);
    }

    public String pojoToJsonString(Object o) {
        return new Gson().toJson(o);
    }
}
