package org.bbr.examples.service.system.io.fs;

import org.bbr.examples.error.TransferException;
import org.bbr.examples.ioc.Factory;
import org.bbr.examples.ioc.FactorySpy;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;

public class FileStoreTest {

    private FileStore store;

    @Before
    public void init() {
        initMocks(this);
        Factory factory = FactorySpy.getTestingFactory();
        doReturn(factory).when(factory).customInit();
        store = factory.getFileStoreSingleton();
    }

    @Test
    public void testReadJson() throws TransferException {
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
                "        \"totalBlocked\": 777.1\n" +
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
                "        \"totalBlocked\": 2222.8\n" +
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
        String json = store.content("response.json");
        // then
        assertEquals(expectedJson.replaceAll("\\s*", ""),
                json.replaceAll("\\s*", ""));
    }
}