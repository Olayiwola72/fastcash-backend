package com.fastcash.moneytransfer.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class DateFormatterTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private DateFormatter dateFormatter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Default Locale to US
        LocaleContextHolder.setLocale(Locale.US);
    }

    @Test
    public void testFormatDate_withDefaultLocale() {
        // Set up the mock behavior for the MessageSource
        when(messageSource.getMessage("date.at", null, Locale.US)).thenReturn("at");

        // Set a fixed datetime
        LocalDateTime dateTime = LocalDateTime.of(2023, 8, 27, 14, 30);

        // Expected format: "27 Aug 2023, at 2:30 PM"
        String expected = "27 Aug 2023, at 2:30 PM";

        // Call the method under test
        String result = DateFormatter.formatDate(dateTime);

        // Assert the result is as expected
        assertEquals(expected, result);
    }

    @Test
    public void testFormatDate_withDifferentLocale() {
        // Change the locale to French
        LocaleContextHolder.setLocale(Locale.FRANCE);

        // Set up the mock behavior for the MessageSource
        when(messageSource.getMessage("date.at", null, Locale.FRANCE)).thenReturn("à");

        // Set a fixed datetime
        LocalDateTime dateTime = LocalDateTime.of(2023, 8, 27, 14, 30);

        // Expected format in French: "27 août 2023, à 14:30"
        String expected = "27 août 2023, à 14:30";

        // Call the method under test
        String result = DateFormatter.formatDate(dateTime);

        // Assert the result is as expected
        assertEquals(expected, result);
    }
    
    @Test
    public void testFormatDate_withGermanLocale() {
        // Change the locale to German
        LocaleContextHolder.setLocale(Locale.GERMANY);

        // Set up the mock behavior for the MessageSource
        when(messageSource.getMessage("date.at", null, Locale.GERMANY)).thenReturn("um");

        // Set a fixed datetime
        LocalDateTime dateTime = LocalDateTime.of(2023, 8, 27, 14, 30);

        // Expected format in German: "27 Aug. 2023, um 14:30"
        String expected = "27 Aug. 2023, um 14:30";

        // Call the method under test
        String result = DateFormatter.formatDate(dateTime);

        // Assert the result is as expected
        assertEquals(expected, result);
    }

    @Test
    public void testToday_withDateFormat() {
        // Call the method under test
        String result = DateFormatter.today();

        // Define the expected pattern for the date string: dd/MM/yyyy
        String expectedPattern = "\\d{2}/\\d{2}/\\d{4}";

        // Assert that the result matches the expected pattern
        assertTrue(result.matches(expectedPattern), "Date should match the pattern dd/MM/yyyy");
    }
}
