package com.honda.olympus.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.honda.olympus.exception.FileProcessException;
import com.honda.olympus.exception.GenackafeException;
import com.honda.olympus.service.GenackafeService;
import com.honda.olympus.vo.GenAckResponseVO;
import com.honda.olympus.vo.MessageEventVO;
import com.honda.olympus.vo.ResponseVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
public class GenackafeController {
	@Value("${service.success.message}")
	private String responseMessage;

	@Value("${service.name}")
	private String name;

	@Value("${service.version}")
	private String version;

	@Value("${service.profile}")
	private String profile;

	@Value("${service.name}")
	private String serviceName;

	@Autowired
	private GenackafeService genackafeService;

	@PostMapping(path = "/event", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseVO> generateAckownledment(@Valid @RequestBody MessageEventVO message)
			throws GenackafeException, FileProcessException, IOException {
		log.debug(message.toString());

		GenAckResponseVO response = genackafeService.createFile(message);

		if (response.getSuccess()) {
			return new ResponseEntity<>(
					new ResponseVO(serviceName, 1L, responseMessage, response.getFileName()),
					HttpStatus.OK);
		}

		return new ResponseEntity<>(
				new ResponseVO(serviceName, 0L,
						"No se encontraron líneas con suficiente información para ser procesadas", response.getFileName()),
				HttpStatus.BAD_REQUEST);

	}

}
