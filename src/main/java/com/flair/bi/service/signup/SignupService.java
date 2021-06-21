package com.flair.bi.service.signup;

import com.flair.bi.domain.DraftUser;
import com.flair.bi.domain.EmailConfirmationToken;
import com.flair.bi.domain.User;
import com.flair.bi.service.DraftUserService;
import com.flair.bi.service.EmailConfirmationTokenService;
import com.flair.bi.service.UserService;
import com.flair.bi.service.email.EmailVerificationService;
import com.flair.bi.service.impl.RealmService;
import com.flair.bi.service.impl.ReplicateRealmResult;
import com.flair.bi.web.rest.dto.RealmDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignupService {

    private final DraftUserService draftUserService;
    private final UserService userService;
    private final RealmService realmService;
    private final EmailVerificationService emailVerificationService;
    private final EmailConfirmationTokenService emailConfirmationTokenService;

    @Transactional
    public void signup(String username, String password, String firstname, String lastname, String email) {
        Optional<User> existingUsername = userService.getUserByLoginNoRealmCheck(username);
        if (existingUsername.isPresent()) {
            throw new SignupException(SignupException.Type.USERNAME_EXISTS);
        }
        Optional<User> existingUserEmail = userService.getUserByEmailAnyRealm(email);
        if (existingUserEmail.isPresent()) {
            throw new SignupException(SignupException.Type.EMAIL_EXISTS);
        }

        DraftUser user = draftUserService.createUser(username, password, firstname,
                lastname, email);
        emailVerificationService.sendConfirmYourEmailEmail(user);
    }

    @Transactional
    public SignUpWithProviderResult signupWithProvider(String username, String password, String firstname, String lastname, String email, String provider) {
        DraftUser user = draftUserService.createUser(username, password, firstname,
                lastname, email, provider);
        EmailConfirmationToken confirmationToken = emailConfirmationTokenService.createToken(user);
        emailVerificationService.confirmEmail(confirmationToken.getToken());
        return SignUpWithProviderResult.builder()
                .emailToken(confirmationToken.getToken())
                .draftUser(user)
                .build();
    }

    @Transactional
    public ConfirmUserResult confirmUser(Long realmId, String emailVerificationToken, String realmCreationToken) {
        RealmDTO realm = realmService.findOne(realmId);
        if (!Objects.equals(realm.getToken(), realmCreationToken)) {
            throw new IllegalStateException("Realm does not belong to that user " + realm + " token " + realmCreationToken);
        }
        EmailConfirmationToken emailConfirmationToken = emailConfirmationTokenService.findByToken(emailVerificationToken);
        DraftUser draftUser = emailConfirmationToken.getDraftUser();

        ReplicateRealmResult result = realmService.replicateRealm(realm.getId(), draftUser);
        draftUserService.deleteUser(draftUser.getId());
        return ConfirmUserResult.builder()
                .jwtToken(result.getJwtToken())
                .build();
    }
}
