package com.honda.olympus.controller;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.honda.olympus.exception.FileProcessException;
import com.honda.olympus.exception.GenackafeException;
import com.honda.olympus.service.GenackafeService;
import com.honda.olympus.vo.GenAckResponseVO;
import com.honda.olympus.vo.MessageVO;
import com.honda.olympus.vo.ResponseVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class GenackafeController {
	@Value("${service.success.message}")
	private String responseMessage;

	@Value("${service.name}")
	private String name;

	@Value("${service.version}")
	private String version;

	@Value("${service.profile}")
	private String profile;

	@Autowired
	private GenackafeService genackafeService;

	@PostMapping(path = "/event", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseVO> generateAckowledment(@RequestBody MessageVO message)
			throws GenackafeException, FileProcessException, IOException {
		log.info(message.toString());
		
		
		GenAckResponseVO response = genackafeService.createFile(message);
		if (response.getSuccess()) {
			return new ResponseEntity<>(new ResponseVO(1, responseMessage, response.getFileName()), HttpStatus.OK);
		}

		return new ResponseEntity<>(new ResponseVO(0, "No puede insertar lineas al archivo: "+response.getFileName(), response.getFileName()), HttpStatus.BAD_REQUEST);
		
	}

}
