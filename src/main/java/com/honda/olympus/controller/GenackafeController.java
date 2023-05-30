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

import com.honda.olympus.exception.GenackafeException;
import com.honda.olympus.service.GenackafeService;
import com.honda.olympus.vo.MessageVO;
import com.honda.olympus.vo.ResponseVO;

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
	public  ResponseEntity<ResponseVO> monitorFiles(@RequestBody MessageVO message) throws GenackafeException,IOException {
		System.out.println(message.toString());
		
		
		if(genackafeService.createFile(message)) {
			return new ResponseEntity<>(new ResponseVO(0,"Error",""), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>(new ResponseVO(1,responseMessage,""), HttpStatus.OK);
	}

}
