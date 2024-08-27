package com.fastcash.moneytransfer.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class DateFormatter {
	
	private static MessageSource messageSource;
	
	public DateFormatter(MessageSource messageSource) {
        DateFormatter.messageSource = messageSource;
    }
    
    public static String formatDate(LocalDateTime dateTime) {
        Locale locale = LocaleContextHolder.getLocale();

        // Fetch the localized "at" string from the message source
        String atLocalized = messageSource.getMessage("date.at", null, locale);

        // Determine if the locale prefers a 24-hour format
        boolean use24HourFormat = !Locale.US.equals(locale);

        // Create the pattern dynamically
        String timePattern = use24HourFormat ? "HH:mm" : "h:mm a";
        String pattern = String.format("d MMM y, '%s' %s", atLocalized, timePattern);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale);

        return dateTime.format(formatter);
    }
	
	public static String today() {
		// Get today's date
        LocalDate today = LocalDate.now();

        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Format the date
        String formattedDate = today.format(formatter);
        
        return formattedDate;
    }
	
}
