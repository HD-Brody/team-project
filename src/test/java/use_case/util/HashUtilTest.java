package use_case.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HashUtilTest {

    @Test
    void PasswordHashEmptyTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            HashUtil.hash(null);
        });
    }

    @Test
    void PasswordHashSuccessfulTest() {
        String hash1;

        try {
            hash1 = HashUtil.hash("123");
        }
        catch (Exception e) {
            hash1 = null;
        }

        assertEquals("40bd001563085fc35165329ea1ff5c5ecbdbbeef", hash1);
    }
}
