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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;

import com.honda.olympus.exception.FileProcessException;
import com.honda.olympus.exception.GenackafeException;
import com.honda.olympus.vo.TemplateFieldVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenackafeUtils {

	private GenackafeUtils() {
		throw new IllegalStateException("AckgmUtils class");
	}

	public static String getFileName() {

		return new StringBuilder().append(GenackafeConstants.ACK_PREFIX)
				.append(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE))
				.append(GenackafeConstants.FILE_EXT).toString();
	}

	public static void checkFileIfWriteFile(String route, String fileName, String newLine) throws GenackafeException {

		Path path = Paths.get(route, fileName);
		Path dirPath = Paths.get(route);
		try {

			Files.createDirectories(dirPath);

			if (!Files.exists(path)) {

				Files.createFile(path);
				log.debug("Genstaafe:: File created");

			}

			Files.write(path, newLine.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);

		} catch (IOException e) {
			e.printStackTrace();
			log.debug("Genstaafe:: Exception ocurred due to: {}",e.getLocalizedMessage());
			throw new GenackafeException("Error creating/writing file");
		}
	}

	public static JSONObject validateFileTemplate(Integer control) throws FileProcessException {

		try {

			ClassPathResource staticDataResource = new ClassPathResource("genackafeFileTemplate.json");
			File file = staticDataResource.getFile();
			log.debug("Genstaafe:: Resource FileName: {}", staticDataResource.getFilename());

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
					totDiferences.add((obj.getInt("Position_end") - obj.getInt("Position_start")) + 1);

				});

				int sumCaracters = totCaracters.stream().mapToInt(Integer::intValue).sum();
				int sumDiferences = totDiferences.stream().mapToInt(Integer::intValue).sum();

				if (sumCaracters != control || sumDiferences != control) {

					throw new FileProcessException("Incorrect template specification");

				}

			}

			return result;
		} catch (IOException e) {
			throw new FileProcessException("Error reading or processing: processFileTemplate.json file");

		}

	}

	public static List<TemplateFieldVO> readGenAckAfeFileTemplate(JSONObject template) {

		List<TemplateFieldVO> fileValues = new ArrayList<>();

		JSONArray templateFields = template.getJSONArray("template");

		if (templateFields.isEmpty()) {
			return null;
		} else {

			templateFields.forEach(item -> {

				JSONObject obj = (JSONObject) item;

				int start = obj.getInt("Position_start");
				int end = obj.getInt("Position_end");
				int total = obj.getInt("Spaces");
				String fieldName = obj.getString("field");

				fileValues.add(new TemplateFieldVO(fieldName, total, start, end));

			});

		}

		return fileValues;

	}

	public static Optional<TemplateFieldVO> getTemplateValueOfField(List<TemplateFieldVO> template, String fieldName) {

		return template.stream().filter(c -> c.getFieldName().equalsIgnoreCase(fieldName)).findFirst();

	}
	
	
	public static String formatDateTimeStamp(Date date) {
		String pattern = "yyyy-MM-dd";
		if(date == null) {
			return "";
		}
			
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(date);
	}

}
