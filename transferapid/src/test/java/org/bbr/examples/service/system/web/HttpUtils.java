package org.bbr.examples.service.system.web;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bbr.examples.utils.JsonProcessor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.bbr.examples.service.system.web.EmbeddedServer.TRANSFER_API_URI;
import static org.bbr.examples.service.system.web.TransferServlet.*;

public class HttpUtils {

    private static final String SERVER_URL = "http://localhost:8282" + TRANSFER_API_URI;
    private static final JsonProcessor jsonProcessor = new JsonProcessor();


    public static Response doGet(Request request) throws IOException, URISyntaxException {
        URIBuilder builder = new URIBuilder(SERVER_URL)
                .setParameter(PARAMETER_ACTION, request.getAction().name())
                .setParameter(PARAMETER_SENDER_ACCOUNT_ID, request.getSenderAccountId().toString())
                .setParameter(PARAMETER_RECIPIENT_ACCOUNT_ID, request.getRecipientAccountId().toString())
                .setParameter(PARAMETER_TRANSFER_TYPE, request.getTransferType().toString())
                .setParameter(PARAMETER_PAYMENT_ID, request.getPaymentId().toString());
        HttpClient client = HttpClientBuilder.create().build();
        URI uri = builder.build();
        System.out.println("REQUEST ---> " + uri);
        HttpGet httpGetRequest = new HttpGet(uri);
        return getServiceResponse(client.execute(httpGetRequest));
    }

    public static Response doPost(Request request) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPostRequest = new HttpPost(SERVER_URL);
        String body = jsonProcessor.pojoToJsonString(request);
        System.out.println("REQUEST ---> " + body);
        StringEntity stringEntity = new StringEntity(body);
        httpPostRequest.setHeader("Content-type", "application/json");
        httpPostRequest.setEntity(stringEntity);
        return getServiceResponse(client.execute(httpPostRequest));
    }

    private static Response getServiceResponse(HttpResponse httpResponse) throws IOException {
        String body = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8.name());
        System.out.println("RESPONSE <--- " + body);
        return jsonProcessor.jsonStringToResponse(body);
    }
}
