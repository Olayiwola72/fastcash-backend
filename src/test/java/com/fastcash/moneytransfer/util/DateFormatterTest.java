package com.fastcash.moneytransfer.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DateFormatterTest {
	
	@Test
    void testFormatDate() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 6, 20, 15, 47);
        
        assertEquals("20 Jun 2024, at 3:47 PM", DateFormatter.formatDate(dateTime));
    }
}
