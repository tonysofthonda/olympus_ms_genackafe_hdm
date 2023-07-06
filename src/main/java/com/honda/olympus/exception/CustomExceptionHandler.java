package com.honda.olympus.exception;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.honda.olympus.service.NotificationService;
import com.honda.olympus.utils.GenackafeConstants;
import com.honda.olympus.vo.MessageVO;
import com.honda.olympus.vo.ResponseVO;



@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	@Value("${service.name}")
	private String serviceName;	

	@Autowired
	NotificationService notificationService;
	
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {

		String notificationMessage = "106 Error al guardar en MFT. No es posible conectarse a la base de datos";
		MessageVO message = new MessageVO(serviceName, GenackafeConstants.ZERO_STATUS,notificationMessage , "");
		notificationService.generatesNotification(message);
		
		ResponseVO error = new ResponseVO(serviceName,0L,ex.getLocalizedMessage(), "");

		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		Map<String, Object> responseBody = new LinkedHashMap<String, Object>();
		responseBody.put("timestamp", new Date());
		responseBody.put("status", status.value());

		List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage())
				.collect(Collectors.toList());

		responseBody.put("errors", errors);

		return new ResponseEntity<>(responseBody, headers, status);
	}
	
	@ExceptionHandler(GenackafeException.class)
	public final ResponseEntity<Object> handleMonitorException(Exception ex, WebRequest request){
		
		List<String> details = new ArrayList<>();
		
		details.add(ex.getLocalizedMessage());
		ResponseVO error = new ResponseVO(serviceName, GenackafeConstants.ZERO_STATUS,ex.getMessage(), "");
		
		return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
