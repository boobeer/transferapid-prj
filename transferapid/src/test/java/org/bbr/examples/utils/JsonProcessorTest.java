package org.bbr.examples.utils;

import org.bbr.examples.service.system.web.Request;
import org.bbr.examples.service.system.web.Response;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public class JsonProcessorTest {

    @Test
    public void jsonStringToPojo() {
        // given
        JsonProcessor jsonProcessor = new JsonProcessor();
        Request request = Utils.buildRequest();
        String requestJson = "{\n" +
                "  \"amount\": 23412.83,\n" +
                "  \"paymentId\": 8534,\n" +
                "  \"senderAccountId\": 8788787,\n" +
                "  \"recipientAccountId\": 111,\n" +
                "  \"action\": \"Send\",\n" +
                "  \"transferType\": 2\n" +
                "}";
        // when
        Request actualRequest = jsonProcessor.jsonStringToRequest(requestJson);
        // then
        assertThat(request.getPaymentId(), is(actualRequest.getPaymentId()));
        assertThat(request.getSenderAccountId(), is(actualRequest.getSenderAccountId()));
        assertThat(request.getRecipientAccountId(), is(actualRequest.getRecipientAccountId()));
        assertThat(request.getAction(), is(actualRequest.getAction()));
        assertThat(request.getTransferType(), is(actualRequest.getTransferType()));
        System.out.println(actualRequest);
    }

    @Test
    public void pojoToJsonString() {
        // given
        JsonProcessor jsonProcessor = new JsonProcessor();
        Response response = Utils.buildResponse();
        String expectedJson = "{\n" +
                "  \"message\": \"Message\",\n" +
                "  \"transfer\": {\n" +
                "    \"sender\": {\n" +
                "      \"client\": {\n" +
                "        \"id\": 7,\n" +
                "        \"type\": \"Person\"\n" +
                "      },\n" +
                "      \"account\": {\n" +
                "        \"id\": 77777,\n" +
                "        \"client\": {\n" +
                "          \"id\": 7,\n" +
                "          \"type\": \"Person\"\n" +
                "        },\n" +
                "        \"total\": 777.1\n" +
                "      }\n" +
                "    },\n" +
                "    \"recipient\": {\n" +
                "      \"client\": {\n" +
                "        \"id\": 8,\n" +
                "        \"type\": \"Person\"\n" +
                "      },\n" +
                "      \"account\": {\n" +
                "        \"id\": 888,\n" +
                "        \"client\": {\n" +
                "          \"id\": 8,\n" +
                "          \"type\": \"Person\"\n" +
                "        },\n" +
                "        \"total\": 2222.8\n" +
                "      }\n" +
                "    },\n" +
                "    \"payment\": {\n" +
                "      \"id\": 111,\n" +
                "      \"senderAccountId\": 77777,\n" +
                "      \"recipientAccountId\": 888,\n" +
                "      \"status\": \"Approved\",\n" +
                "      \"total\": 123.7,\n" +
                "      \"commission\": 7432.87\n" +
                "    }\n" +
                "  }\n" +
                "}";
        // when
        String json = jsonProcessor.pojoToJsonString(response);
        System.out.println(json);
        // then
        assertEquals(expectedJson.replaceAll("\\s*", ""),
                json.replaceAll("\\s*", ""));
    }

}