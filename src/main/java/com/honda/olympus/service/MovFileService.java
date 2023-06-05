package com.honda.olympus.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.honda.olympus.vo.MoveFileVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MovFileService {
	
	@Value("${move.file.service.url}")
	private String moveFileURI;

	public void sendMoveFileMessage(MoveFileVO message) {
		try {
			log.info("Calling MoveFile service");
			log.info(message.toString());
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			RestTemplate restTemplate = new RestTemplate();

			HttpEntity<MoveFileVO> requestEntity = new HttpEntity<>(message, headers);

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(moveFileURI, requestEntity,
					String.class);

			log.info("MoveFile called with Status Code: {}",responseEntity.getStatusCode());
			log.info("Message: " + responseEntity.getBody());
		} catch (Exception e) {
			log.info("Error calling MoveFIle service {}",e.getLocalizedMessage());
		}

	}

}
