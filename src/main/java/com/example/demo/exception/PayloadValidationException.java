package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * https://stackabuse.com/how-to-return-http-status-codes-in-a-spring-boot-application/
 * https://www.baeldung.com/spring-response-status
 * // https://stackoverflow.com/questions/24292373/spring-boot-rest-controller-how-to-return-different-http-status-codes
 * A nice way is to use Spring's ResponseStatusException
 *
 *
 * https://stackoverflow.com/questions/62561211/spring-responsestatusexception-does-not-return-reason
 * Basically, all you need to do is add server.error.include-message=always to your application.properties file, and now your message field should be populated.
 */
//@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PayloadValidationException extends ResponseStatusException {
    public PayloadValidationException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }
}
