package com.infotecs.intern;

import com.infotecs.intern.model.service.PairService;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InternApplicationTests {

    @Autowired
    PairService pairService;

    @Test
    void ttlCheck() {
        String testKey = "testKey";
        String testValue = "testValue";
        Integer ttl = 1;

        pairService.savePair(testKey, testValue, ttl);
        String result = pairService.getValueByKey(testKey);
        assertNotEquals("null", result);
        await().pollDelay(Durations.ONE_SECOND).until(() -> true);
        result = pairService.getValueByKey(testKey);
        assertEquals("null", result);
    }

    @Test
    void testDelete() {
        String testKey = "testKey";
        String testValue = "testValue";
        Integer ttl = 60;

        pairService.savePair(testKey, testValue, ttl);
        String result = pairService.getValueByKey(testKey);
        assertNotEquals("null", result);
        pairService.deletePairByKey(testKey);
        result = pairService.getValueByKey(testKey);
        assertEquals("null", result);
    }

    @Test
    void testAdd() {
        String testKey = "testKey";
        String testValue = "testValue";
        Integer ttl = 60;

        for (int i = 1; i < 10; i++) {
            pairService.savePair(testKey + i, testValue + i, ttl);
            String result = pairService.getValueByKey(testKey + i);
            assertNotEquals("null", result);
        }
    }
}
