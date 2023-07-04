package com.honda.olympus.utils;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.honda.olympus.service.LogEventService;
import com.honda.olympus.vo.EventVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GenackMessagesHandler {

	@Autowired
	LogEventService logEventService;

	@Value("${service.name}")
	private String serviceName;
	
	@Value("${service.success.message}")
	private String successMessage;

	private static final String NO_ACCEPTED_STATUS = "El mensaje tiene un status no aceptado para el proceso: %s";
	private static final String DETAIL_FAIL = "El mensaje no tiene detalles para procesar: %s ";
	private static final String FIXED_ORDER_NO_EXIST = "NO se encontro el  fixed_order_id: %s en la tabla AFE_FIXED_ORDERS_EV con el query: %s";
	private static final String MODEL_COLOR_NO_EXISTS = "No se existe el model_color_id: %s en la tabla AFE_MODEL_COLOR para el fixed_order_id %s con el query: %s";
	private static final String MODEL_NO_EXIST = "No existe el model_id: %s en la tabla AFE_MODEL para el fixed_order_id: %s con el query: %s";
	private static final String PLANT_NO_EXISTS = "No existe el plant id: %s en la tabla AFE_PLANT para el fixed_order_id: %s con el query: %s";
	private static final String DIVISION_NO_EXISTS = "No existe el division id: %s en la tabla AFE_DIVISION para el fixed_order_id: %s con el query: %s";
	private static final String COLOR_NO_EXISTS = "No existe el colo id: %s para el fixed_order_id: %s con el query: %s";
	private static final String ORDER_HISTORY_NO_EXISTS = "NO existe la historia de la acción en la tabla AFE_ORDER_ACTION_HISTORY para le fixed_order_id: %s con el query: %s";
	private static final String ACTION_NO_EXISTS = "NO existe el action id: %s en la tabla AFE_ACTION_EV para el fixed_order_id %s con el query: %s";
	private static final String NO_ACK_MESSAGES_EXISTS = "NO existe los mensajes de la historia: %s en la tabla AFE_ACK_MSG para el fixed_order_id: %s con el query: %s";
	private static final String ACTION_SUCCESS = "Inserción exitosa de la línea %s en la tabla AFEfIXED_ORDER_EV y en la tabla AFE_ORDER_HISTORY";
	

	private String message = null;
	EventVO event = null;

	public void createAndLogMessageStatusFail(Long status) {

		this.message = String.format(NO_ACCEPTED_STATUS, status);
		this.event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	public void createAndLogMessageDetailFail(String message) {

		this.message = String.format(DETAIL_FAIL, message);
		this.event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}

	public void createAndLogMessageNoFixedOrders(String fixedOrderId,String query) {

		this.message = String.format(FIXED_ORDER_NO_EXIST, fixedOrderId, query);
		this.event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	
	public void createAndLogMessageModelColorNoExists(Long modelColorId,Long fixedOrderId,String query) {

		this.message = String.format(MODEL_COLOR_NO_EXISTS,modelColorId,fixedOrderId,query);
		this.event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	

	public void createAndLogMessageModelNoExist(Long modelId,String query) {

		this.message = String.format(MODEL_NO_EXIST, modelId, query);
		this.event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS, message, "");

		sendAndLog();
	} 
	
	public void createAndLogMessageDvisionNoExist(Long divisionId,Long fixedOrderId,String query) {

		this.message = String.format(DIVISION_NO_EXISTS, divisionId,fixedOrderId, query);
		this.event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	public void createAndLogMessagePlantNoExist(Long plantId,Long fixedOrderId,String query) {

		this.message = String.format(PLANT_NO_EXISTS, plantId,fixedOrderId, query);
		this.event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	
	public void createAndLogMessageColorNoExists(Long colorId,Long fixedOrderId,String query) {

		this.message = String.format(COLOR_NO_EXISTS, colorId,fixedOrderId, query);
		this.event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	public void createAndLogMessageActionNoExists(Long actionId,Long fixedOrderId,String query) {

		this.message = String.format(ACTION_NO_EXISTS, actionId,fixedOrderId, query);
		this.event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	
	public void createAndLogMessageOrdersHistoryNoExists(Long fixedOrderId,String query) {

		this.message = String.format(ORDER_HISTORY_NO_EXISTS, fixedOrderId, query);
		this.event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}

	public void createAndLogMessageNoAckMessageExists(Long orderActionHistoryId,Long idFixedOrder,String query) {

		this.message = String.format(NO_ACK_MESSAGES_EXISTS, orderActionHistoryId, idFixedOrder,query);
		this.event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS, message, "");

		sendAndLog();
	}
	

	public void successMessage(String line) {
	
		this.message = String.format(ACTION_SUCCESS,line);
		this.event = new EventVO(serviceName, GenackafeConstants.ONE_STATUS, message, "");

		sendAndLog();
	}
	

	

	private void sendAndLog() {
		logEventService.sendLogEvent(this.event);
		log.debug(this.message);
	}

}
