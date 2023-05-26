package com.honda.olympus.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GenackafeUtils {

	private GenackafeUtils() {
		throw new IllegalStateException("AckgmUtils class");
	}

	public static String getFileName() {
		return new StringBuilder().append(AckgmConstants.ACK_PREFIX)
				.append(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)).append(AckgmConstants.FILE_EXT)
				.toString();
	}

}
