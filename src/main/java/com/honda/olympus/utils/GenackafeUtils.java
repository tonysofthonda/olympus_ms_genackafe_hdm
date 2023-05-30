package com.honda.olympus.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.honda.olympus.exception.GenackafeException;

public class GenackafeUtils {

	private GenackafeUtils() {
		throw new IllegalStateException("AckgmUtils class");
	}

	public static String getFileName() {

		LocalDateTime now = LocalDateTime.now();
		int hour = now.getHour();
		int minute = now.getMinute();
		int second = now.getSecond();

		return new StringBuilder().append(GenackafeConstants.ACK_PREFIX)
				.append(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE))
				.append(hour).append(minute).append(second).append(GenackafeConstants.FILE_EXT).toString();
	}

	public static void checkFileIfWriteFile(String route, String fileName, String newLine ) throws GenackafeException {

		Path path = Paths.get(route, fileName);
		Path dirPath = Paths.get(route);
		try {
			
			Files.createDirectories(dirPath);
			
			if (!Files.exists(path)) {

				Files.createFile(path);
				System.out.println("File created");

			}

			Files.write(path, newLine.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
			throw new GenackafeException("Error creating/writing file");
		}
	}

}
