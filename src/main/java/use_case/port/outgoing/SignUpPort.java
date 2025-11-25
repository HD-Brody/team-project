package use_case.port.outgoing;

import use_case.dto.SignUpOutputData;

public interface SignUpPort {

    void prepareSuccessView(SignUpOutputData signUpOutputData);

    void prepareFailView(SignUpOutputData signUpOutputData);
}
