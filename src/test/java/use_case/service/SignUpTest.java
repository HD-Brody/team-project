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
    private SignUpInteractor signUpInteractor;

    @BeforeEach
    void setup() {
        db = new InMemorySignUpDataAccessObject();
        signUpPresenter = new MockPresenter();
        signUpInteractor = new SignUpInteractor(db, signUpPresenter);
    }

    @Test
    void SignUpServicePwdEmptyTest() {
        db.cleanDB();
        signUpInteractor.execute(new SignUpInputData("123@abc.com", null, "1111"));
        assertFalse(signUpPresenter.getIsSuccess());
        assertEquals("Password is empty, please try again", signUpPresenter.getMessage());
    }

    @Test
    void SignUpServicePwdEmptyStringTest() {
        db.cleanDB();
        signUpInteractor.execute(new SignUpInputData("123@abc.com", "", "1111"));
        assertFalse(signUpPresenter.getIsSuccess());
        assertEquals("Password is empty, please try again", signUpPresenter.getMessage());
    }

    @Test
    void SignUpServiceSuccessTest() {
        db.cleanDB();
        signUpInteractor.execute(new SignUpInputData("123@abc.com", "123123", "1111"));
        assertTrue(signUpPresenter.getIsSuccess());
        assertEquals("success", signUpPresenter.getMessage());
        assertEquals("123@abc.com", db.getUserByEmail("123@abc.com").getEmail());
    }

    @Test
    void SignUpServiceEmailInvalidTest() {
        db.cleanDB();
        signUpInteractor.execute(new SignUpInputData("123", "123123", "1111"));
        assertFalse(signUpPresenter.getIsSuccess());
        assertEquals("Email is invalid, please try again", signUpPresenter.getMessage());
    }

    @Test
    void SignUpServiceNicknameInvalidTest() {
        db.cleanDB();
        signUpInteractor.execute(new SignUpInputData("123@abc.com", "123123", ""));
        assertFalse(signUpPresenter.getIsSuccess());
        assertEquals("Name is empty, please try again", signUpPresenter.getMessage());
        signUpInteractor.execute(new SignUpInputData("123@abc.com", "123123", null));
        assertFalse(signUpPresenter.getIsSuccess());
        assertEquals("Name is empty, please try again", signUpPresenter.getMessage());
    }

    @Test
    void SignUpServiceNameRepeatingTest() {
        db.cleanDB();
        signUpInteractor.execute(new SignUpInputData("123@abc.com", "123123", "admin"));
        assertTrue(signUpPresenter.getIsSuccess());
        assertEquals("success", signUpPresenter.getMessage());
        assertEquals("123@abc.com", db.getUserByEmail("123@abc.com").getEmail());
        signUpInteractor.execute(new SignUpInputData("345@ads.ca", "122243", "admin"));
        assertFalse(signUpPresenter.getIsSuccess());
        assertEquals("Name has been used, please try again", signUpPresenter.getMessage());
    }

    @Test
    void SignUpServiceSwitchViewTest() {
        db.cleanDB();
        signUpInteractor.switchView();
        assertTrue(signUpPresenter.getIsSuccess());
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
