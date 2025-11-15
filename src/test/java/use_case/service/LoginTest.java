package use_case.service;

import data_access.persistence.in_memory.InMemoryUserInfoStorageDataAccessObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import use_case.dto.LoginInputData;
import use_case.dto.LoginOutputData;
import use_case.port.outgoing.LoginOutputPort;

public class LoginTest {

    private LoginService loginService;
    private InMemoryUserInfoStorageDataAccessObject userRepository;
    private MockPresenter loginPresenter;

    @BeforeEach
    void setup() {
        userRepository = new InMemoryUserInfoStorageDataAccessObject();
        loginPresenter = new MockPresenter();
        loginService = new LoginService(userRepository, loginPresenter);

        userRepository.setUserByUserID("123", "40bd001563085fc35165329ea1ff5c5ecbdbbeef");
    }

    @Test
    void PasswordHashEmptyTest() {
        assertThrows(IllegalArgumentException.class, () -> {
            loginService.passwordHashing(null);
        });
    }

    @Test
    void PasswordHashSuccessfulTest() {
        String hash1;

        try {
            hash1 = loginService.passwordHashing("123");
        }
        catch (Exception e) {
            hash1 = null;
        }

        assertEquals("40bd001563085fc35165329ea1ff5c5ecbdbbeef", hash1);
    }

    @Test
    void LoginServiceExecuteSuccessTest() {
        loginService.execute(new LoginInputData("123", "123"));
        assertTrue(loginPresenter.getSuccessCalled());
        assertFalse(loginPresenter.getfailCalled());
    }

    @Test
    void LoginServiceExecuteFailTest() {
        loginService.execute(new LoginInputData("1", "123"));
        assertTrue(loginPresenter.getfailCalled());
        assertFalse(loginPresenter.getSuccessCalled());
        assertEquals("User not found", loginPresenter.getFailDataMsg());
    }

    @Test
    void LoginServiceExecutePwdDontMatchTest() {
        loginService.execute(new LoginInputData("123", "123213123"));
        assertTrue(loginPresenter.getfailCalled());
        assertFalse(loginPresenter.getSuccessCalled());
        assertEquals("Password don't match, please try again", loginPresenter.getFailDataMsg());
    }

    @Test
    void LoginServiceExecutePwdEmptyTest() {
        loginService.execute(new LoginInputData("123", null));
        assertTrue(loginPresenter.getfailCalled());
        assertFalse(loginPresenter.getSuccessCalled());
        assertEquals("Password can't be empty", loginPresenter.getFailDataMsg());
    }

    private static final class MockPresenter implements LoginOutputPort {
        private boolean successCalled = false;
        private boolean failCalled = false;
        private LoginOutputData successData = null;
        private LoginOutputData failData = null;

        @Override
        public void prepareSuccessView(LoginOutputData loginOutputData) {
            successCalled = true;
            successData = loginOutputData;
            return;
        }

        @Override
        public void prepareFailView(LoginOutputData loginOutputData) {
            failCalled = true;
            failData = loginOutputData;
            return;
        }

        public boolean getSuccessCalled() {
            return successCalled;
        }

        public boolean getfailCalled() {
            return failCalled;
        }

        public String getSuccessDataMsg() {
            return successData.getMessage();
        }

        public String getFailDataMsg() {
            return failData.getMessage();
        }
    }
}
