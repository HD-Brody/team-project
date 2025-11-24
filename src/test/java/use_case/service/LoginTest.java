package use_case.service;

import data_access.persistence.in_memory.InMemoryLoginInfoStorageDataAccessObject;
import data_access.persistence.in_memory.InMemorySessionInfoDataAccessObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import use_case.dto.LoginInputData;
import use_case.dto.LoginOutputData;
import use_case.port.outgoing.LoginOutputPort;

public class LoginTest {

    private LoginService loginService;
    private InMemoryLoginInfoStorageDataAccessObject userRepository;
    private MockPresenter loginPresenter;
    private InMemorySessionInfoDataAccessObject sessionStorage;

    @BeforeEach
    void setup() {
        userRepository = new InMemoryLoginInfoStorageDataAccessObject();
        loginPresenter = new MockPresenter();
        sessionStorage = new InMemorySessionInfoDataAccessObject();
        loginService = new LoginService(userRepository, sessionStorage, loginPresenter);

        userRepository.setUserByEmail("123@abc.com", "40bd001563085fc35165329ea1ff5c5ecbdbbeef");
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
        loginService.execute(new LoginInputData("123@abc.com", "123"));
        assertTrue(loginPresenter.getSuccessCalled());
    }

    @Test
    void LoginServiceExecuteFailTest() {
        loginService.execute(new LoginInputData("12111111113@abc.com", "123"));
        assertFalse(loginPresenter.getSuccessCalled());
        assertEquals("User not found", loginPresenter.getSuccessDataMsg());
    }

    @Test
    void LoginServiceExecutePwdDontMatchTest() {
        loginService.execute(new LoginInputData("123@abc.com", "123213123"));
        assertFalse(loginPresenter.getSuccessCalled());
        assertEquals("Password doesn't match, please try again", loginPresenter.getSuccessDataMsg());
    }

    @Test
    void LoginServiceExecutePwdEmptyTest() {
        loginService.execute(new LoginInputData("123@abc.com", null));
        assertFalse(loginPresenter.getSuccessCalled());
        assertEquals("Password can't be empty", loginPresenter.getSuccessDataMsg());
    }

    @Test
    void LoginSuccessfulStoreSessionTest() {
        loginService.execute(new LoginInputData("123@abc.com", "123"));
        assertEquals("123@abc.com", sessionStorage.getSession().getEmail());
    }

    @Test
    void ValidateEmailSuccessfulTest() {
        assertTrue(loginService.validateEmail("123@abc.com"));
        assertTrue(loginService.validateEmail("15823121@163.com"));
    }

    @Test
    void ValidateEmailFailTest() {
        assertFalse(loginService.validateEmail("12@b"));
        assertFalse(loginService.validateEmail("12"));
        assertFalse(loginService.validateEmail("12@"));
        assertFalse(loginService.validateEmail("@b"));
        assertFalse(loginService.validateEmail(""));
        assertFalse(loginService.validateEmail(null));
        assertFalse(loginService.validateEmail("@"));
        assertFalse(loginService.validateEmail(".abc"));
    }

    private static final class MockPresenter implements LoginOutputPort {
        private boolean successCalled = false;
        private LoginOutputData data = null;

        @Override
        public void prepareSuccessView(LoginOutputData loginOutputData) {
            successCalled = true;
            data = loginOutputData;
        }

        @Override
        public void prepareFailView(LoginOutputData loginOutputData) {
            successCalled = false;
            data = loginOutputData;
        }

        public boolean getSuccessCalled() {
            return successCalled;
        }

        public String getSuccessDataMsg() {
            return data.getMessage();
        }
    }
}
