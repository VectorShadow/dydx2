package definitions;

public interface LoginResponseHandler {

    void loginResponseAccountAlreadyConnected();

    void loginResponseAccountDoesNotExist();

    void loginResponseDuplicateAccountCreation();

    void loginResponseIncorrectPassword();

    void loginResponseSuccess();
}
