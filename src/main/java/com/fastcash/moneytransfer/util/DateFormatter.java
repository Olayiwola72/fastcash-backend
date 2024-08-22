package com.fastcash.moneytransfer.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateFormatter {
	
	public static String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM y, 'at' h:mm a", Locale.US);
        return dateTime.format(formatter); // Convert to uppercase Locale.US
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
