package use_case.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidationUtilTest {

    @Test
    void ValidateEmailSuccessfulTest() {
        assertTrue(ValidationUtil.validateEmail("123@abc.com"));
        assertTrue(ValidationUtil.validateEmail("15823121@163.com"));
    }

    @Test
    void ValidateEmailFailTest() {
        assertFalse(ValidationUtil.validateEmail("12@b"));
        assertFalse(ValidationUtil.validateEmail("12"));
        assertFalse(ValidationUtil.validateEmail("12@"));
        assertFalse(ValidationUtil.validateEmail("@b"));
        assertFalse(ValidationUtil.validateEmail(""));
        assertFalse(ValidationUtil.validateEmail(null));
        assertFalse(ValidationUtil.validateEmail("@"));
        assertFalse(ValidationUtil.validateEmail(".abc"));
    }
}
