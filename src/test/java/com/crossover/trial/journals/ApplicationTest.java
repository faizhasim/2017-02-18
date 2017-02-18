package com.crossover.trial.journals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.validation.ValidationException;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@RunWith(JUnit4.class)
public class ApplicationTest {

    @Test(expected = ValidationException.class)
    public void shouldThrowErrorOnInvalidDirectory() {
        String invalidPath = randomAlphanumeric(5);
        Application.validateRootPath(invalidPath);
    }

    @Test
    public void shouldNotThrowErrorOnValidDirectory() {
        String validPath = System.getProperty("user.home");
        Application.validateRootPath(validPath);
    }
}