package com.honda.olympus.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.ResourceUtils;

import com.honda.olympus.exception.FileProcessException;
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
	
	public static JSONObject validateFileTemplate(Integer control) throws FileProcessException {

		try {
			File file = ResourceUtils.getFile("classpath:genackfeFileTemplate.json");
			InputStream inputStream = new FileInputStream(file);
			StringBuilder responseStrBuilder = new StringBuilder();

			try (BufferedReader bR = new BufferedReader(new InputStreamReader(inputStream))) {
				String line = "";

				while ((line = bR.readLine()) != null) {

					responseStrBuilder.append(line);
				}

			} finally {
				inputStream.close();
			}

			JSONObject result = new JSONObject(responseStrBuilder.toString());

			JSONArray templateFields = result.getJSONArray("template");

			if (templateFields.isEmpty()) {
				return null;
			} else {

				final List<Integer> totCaracters = new ArrayList<>();
				final List<Integer> totDiferences = new ArrayList<>();

				templateFields.forEach(item -> {

					JSONObject obj = (JSONObject) item;
					totCaracters.add(obj.getInt("Spaces"));
					totDiferences.add((obj.getInt("Position_end") - obj.getInt("Position_start"))+1);
	
				});
				
				int sumCaracters = totCaracters.stream().mapToInt(Integer::intValue).sum();
				int sumDiferences = totDiferences.stream().mapToInt(Integer::intValue).sum();
				
				if(sumCaracters != control || sumDiferences != control) {
					
					throw new FileProcessException("Incorrect template specification");
					
				}

			}

			return result;
		} catch (IOException e) {
			throw new FileProcessException("Error reading or processing: processFileTemplate.json file");
			
			
		}

	}

}
