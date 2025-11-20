package use_case.service;

import data_access.persistence.in_memory.InMemorySignUpDataAccessObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.dto.SignUpInputData;
import use_case.dto.SignUpOutputData;
import use_case.port.outgoing.SignUpPort;

import static org.junit.jupiter.api.Assertions.*;

public class SignUpTest {

    private InMemorySignUpDataAccessObject db;
    private MockPresenter signUpPresenter;
    private SignUpService signUpService;

    @BeforeEach
    void setup() {
        db = new InMemorySignUpDataAccessObject();
        signUpPresenter = new MockPresenter();
        signUpService = new SignUpService(db, signUpPresenter);
    }

    @Test
    void SignUpServicePwdEmptyTest() {
        signUpService.execute(new SignUpInputData("123", null, "1111"));
        assertFalse(signUpPresenter.getIsSuccess());
        assertEquals("Password is empty, please try again", signUpPresenter.getMessage());
    }

    @Test
    void SignUpServicePwdEmptyStringTest() {
        signUpService.execute(new SignUpInputData("123", "", "1111"));
        assertFalse(signUpPresenter.getIsSuccess());
        assertEquals("Password is empty, please try again", signUpPresenter.getMessage());
    }

    @Test
    void SignUpServiceSuccessTest() {
        signUpService.execute(new SignUpInputData("123", "123123", "1111"));
        assertTrue(signUpPresenter.getIsSuccess());
        assertEquals("success", signUpPresenter.getMessage());
        assertEquals("123", db.getUserByEmail("123").getEmail());
    }

    private static class MockPresenter implements SignUpPort {
        private boolean isSuccess = false;
        private String message = "";

        @Override
        public void prepareFailView(SignUpOutputData signUpOutputData) {
            message = signUpOutputData.getMessage();
            isSuccess = false;
        }

        @Override
        public void prepareSuccessView(SignUpOutputData signUpOutputData) {
            message = signUpOutputData.getMessage();
            isSuccess = true;
        }

        public boolean getIsSuccess() {
            return isSuccess;
        }

        public String getMessage() {
            return message;
        }
    }
}
