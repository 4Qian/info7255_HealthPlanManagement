package com.example.demo.service;

import com.example.demo.permissions.ResourcePermission;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.OAuth2Utils;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * https://www.googleapis.com/oauth2/v3/certs
 *
 * 1. Validate the structure of a JWT
 * 2. Create an “allowed list” that contains valid values for iss claims
 * 3. Base64decode JWT header, payload
 * 4. Retrive Alg and Kid from Header
 * 5. Retrive iss from  payload
 * 6. Compare the value of iss to that stored  in the “allowed list”
 * 5.  If iss value in allow list, use JWKS to retrieve public key. Otherwise, signature invalid
 * 6. Verify signature
 * 7. Validate any other claims such as scope, aud, exp, etc.
 *
 * https://oauth2.googleapis.com/tokeninfo?id_token=xyz123
 */
@Component
public class AuthorizationService {
    private static final String BEARER_HEADER_PREFIX = "Bearer ";
    private static final JacksonFactory jacksonFactory = new JacksonFactory();
    private static final List<String> EXPECTED_CLIENT_IDs = List.of(
            "873551672347-r0qhhf0bi455gbsakcf7360avfu97ql6.apps.googleusercontent.com");
    private static final List<String> allowedIssuerList = List.of("https://accounts.google.com");
    private static final GoogleIdTokenVerifier VERIFIER = new GoogleIdTokenVerifier.Builder(new ApacheHttpTransport(), jacksonFactory)
            .setAudience(EXPECTED_CLIENT_IDs).build();

    private Map<String, ResourcePermission> resourcePermissions = new HashMap<>();
    private Set<String> allUsers = new HashSet<>() {
        {
            add("103288737371605585692"); // maggie
            add("100284234332891791065"); //qz
        }
    };

    /**
     * https://developers.google.com/identity/sign-in/web/backend-auth#calling-the-tokeninfo-endpoint
     *
     * @param idToken
     * @param operation
     * @param planKey
     * @return
     */
    public boolean authorizeIdToken(String idToken, ResourcePermission.Operation operation, String planKey) {
        if (idToken == null || !idToken.startsWith(BEARER_HEADER_PREFIX)) {
            return false;
        }
        idToken = idToken.substring(BEARER_HEADER_PREFIX.length());
        System.out.println(idToken);
        GoogleIdToken googleIdToken = null;
        try {
            // https://developers.google.com/identity/sign-in/web/backend-auth#calling-the-tokeninfo-endpoint
            // The GoogleIdTokenVerifier.verify() method verifies the JWT signature, the aud claim, the iss claim, and the exp claim.
            googleIdToken = VERIFIER.verify(idToken);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        if (googleIdToken == null) {
            System.out.println("== not a valid google tokenId, signature: audience clain, issuer clain or expiry claim are invalid");
            return false;
        }
        boolean isOperationAllowed = isUserOperationAllowed(googleIdToken.getPayload().getSubject(), operation, planKey);
        if (!isOperationAllowed) {
            System.out.println("=== user does not have permission to operate on resource plan key: " + planKey);
            return false;
        }
        return true;
    }

    /**
     * https://wstutorial.com/misc/jwt-java-public-key-rsa.html
     * <dependency>
     *     <groupId>io.jsonwebtoken</groupId>
     *     <artifactId>jjwt</artifactId>
     *     <version>0.9.1</version>
     * </dependency>
     * @throws NoSuchAlgorithmException
     */
    private PublicKey generatePublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // https://www.viralpatel.net/java-create-validate-jwt-token/
        KeyFactory rsa256KeyFactory = KeyFactory.getInstance("RSA256");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(""));
        PublicKey publicKey = rsa256KeyFactory.generatePublic(keySpec);
        return publicKey;
    }

    private boolean isUserOperationAllowed(String subject, ResourcePermission.Operation operation, String planKey) {
        return allUsers.contains(subject);
//        return "112875225019570076726".equals(subject);
//        if (!allUsers.contains(subject)) {
//            return false;
//        }
//        if (planKey == null) {
//            return true;
//        }
//
//        return resourcePermissions.getOrDefault(planKey, ResourcePermission.NONE_PERMISSION_INSTANCE)
//                .ownerAllowedOperations.get(subject).contains(operation);

    }
}
