package com.honda.olympus.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.honda.olympus.dao.AfeAckEvEntity;
import com.honda.olympus.dao.AfeActionEntity;
import com.honda.olympus.dao.AfeFixedOrdersEvEntity;
import com.honda.olympus.dao.AfeOrdersHistoryEntity;
import com.honda.olympus.repository.AfeAckEvRepository;
import com.honda.olympus.repository.AfeActionRepository;
import com.honda.olympus.repository.AfeFixedOrdersEvRepository;
import com.honda.olympus.repository.AfeOrdersHistoryRepository;
import com.honda.olympus.utils.AckgmConstants;
import com.honda.olympus.utils.AckgmMessagesHandler;
import com.honda.olympus.vo.EventVO;
import com.honda.olympus.vo.MaxTransitCallVO;
import com.honda.olympus.vo.MaxTransitResponseVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AckgmHdmService {

	@Value("${maxtransit.timewait}")
	private Long timeWait;

	@Value("${service.name}")
	private String serviceName;
	
	@Autowired
	private AckgmMessagesHandler ackgmMessagesHandler;

	@Autowired
	private MaxTransitService maxTransitService;

	@Autowired
	private AfeFixedOrdersEvRepository afeFixedOrdersEvRepository;

	@Autowired
	private AfeOrdersHistoryRepository afeOrdersHistoryRepository;

	@Autowired
	AfeAckEvRepository afeAckEvRepository;

	@Autowired
	private AfeActionRepository afeActionRepository;

	public void callAckgmCheckHd() {
		EventVO event;

		Boolean successFlag = Boolean.FALSE;

		MaxTransitCallVO maxTransitMessage = new MaxTransitCallVO();

		maxTransitMessage.setRequest(AckgmConstants.ACK);

		List<MaxTransitResponseVO> maxTransitData = maxTransitService.generateCallMaxtransit(maxTransitMessage);

		if (maxTransitData.isEmpty()) {
/*
			logEventService.sendLogEvent(new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
					"La respuesta de MAXTRANSIT no tiene elementos: " + maxTransitData.toString(), ""));
			log.debug("MAXTRANSIT empty response");
			*/
			ackgmMessagesHandler.createAndLogMessage(maxTransitData);
		}

		// Node 4
		Iterator<MaxTransitResponseVO> it = maxTransitData.iterator();
		while (it.hasNext()) {
			MaxTransitResponseVO maxTransitDetail = it.next();
			Long rqstIdentifier = maxTransitDetail.getRqstIdentfr();
			log.debug("----rqstIdentifier----:: " + rqstIdentifier);

			if (rqstIdentifier < 0) {
				/*logEventService.sendLogEvent(new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
						"No tiene un valor mayor o igual a cero reqst_identfr, " + rqstIdentifier
								+ " respuesta de MAXTRANSIT: " + maxTransitDetail.toString(),
						""));

				log.debug("No tiene un valor mayor o igual a cero reqst_identfr:" + rqstIdentifier);
				*/
				ackgmMessagesHandler.createAndLogMessage(rqstIdentifier, maxTransitDetail);
				break;

			}

			// QUERY1
			List<AfeFixedOrdersEvEntity> fixedOrders = afeFixedOrdersEvRepository.findAllByRqstId(rqstIdentifier);

			if (fixedOrders.isEmpty()) {
				/*log.debug(
						"No se encontro requst_idntfr: {} en la tabla AFE_FIXED_ORDERS_EV",rqstIdentifier);
				event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
						"No se encontro requst_idntfr: " + rqstIdentifier + " en la tabla AFE_FIXED_ORDERS_EV", "");
				logEventService.sendLogEvent(event); */

				ackgmMessagesHandler.createAndLogMessage(rqstIdentifier);
				
				// return to main line process loop
				break;
			}

			AfeFixedOrdersEvEntity fixedOrder = fixedOrders.get(0);

			if (AckgmConstants.ACCEPTED_STATUS.equalsIgnoreCase(maxTransitDetail.getReqstStatus())) {
				log.debug("Start:: Accepted flow");
				try {
					// QUERY2
					AfeAckEvEntity ackEntity = new AfeAckEvEntity();

					ackEntity.setFixedOrderId(fixedOrder.getId());
					ackEntity.setAckStatus(maxTransitDetail.getReqstStatus());

					JSONArray jsArray = new JSONArray(maxTransitDetail.getMesagge());
					
					ackEntity.setAckMsg(jsArray.toString());
					ackEntity.setAckRequestTimestamp(new Date());
					afeAckEvRepository.saveAndFlush(ackEntity);
				} catch (Exception e) {
					/*
					log.debug(
							"Fallo en la ejecución del query de inserción en la tabla AFE_FIXED_ORDERS_EV con el query ");
					event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
							"Fallo en la ejecución del query de inserción en la tabla AFE_FIXED_ORDERS_EV con el query ",
							"");
					logEventService.sendLogEvent(event);*/
					//TODO
					ackgmMessagesHandler.createAndLogMessage("");
					break;
				}

				if (finalFlow(maxTransitDetail, fixedOrder)) {
					successFlag = Boolean.TRUE;
					log.debug("End:: Accepted flow");
				}

			} else {

				if (AckgmConstants.FAILED_STATUS.equalsIgnoreCase(maxTransitDetail.getReqstStatus())) {

					if (failedFlow(fixedOrder.getId(), maxTransitDetail)) {

						if (finalFlow(maxTransitDetail, fixedOrder)) {
							successFlag = Boolean.TRUE;

						}
					}

				}

				if (AckgmConstants.CANCELED_STATUS.equalsIgnoreCase(maxTransitDetail.getReqstStatus())) {
					if (canceledFlow(fixedOrder.getId(), maxTransitDetail)) {
						if (finalFlow(maxTransitDetail, fixedOrder)) {
							successFlag = Boolean.TRUE;

						}
					}
				}

				// request_status invalid
				/*
				log.debug("El reqst_status no es valido: {}",maxTransitDetail.getReqstStatus());
				event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
						"El reqst_status no es valido: " + maxTransitDetail.getReqstStatus(), "");
				logEventService.sendLogEvent(event);*/
				
				ackgmMessagesHandler.createAndLogMessage(maxTransitDetail);

			}
			
			

		}
		
		if(successFlag) {
			/*log.debug("-----SUCCESS-----");
			event = new EventVO(serviceName, AckgmConstants.ONE_STATUS,"SUCCESS", "");
			logEventService.sendLogEvent(event);*/
			
			ackgmMessagesHandler.successMessage();
		}

	}

	private Boolean failedFlow(final Long fixedOrderId, MaxTransitResponseVO maxTransitDetail) {
		EventVO event;
		log.debug("Start:: Failed Flow ");

		// QUERY6
		List<AfeAckEvEntity> acks = afeAckEvRepository.findAllByFixedOrderId(fixedOrderId);

		if (acks.isEmpty()) {
			
			/*log.debug("No existe el fixed_order_id: {} en la tabla AFE_ACK_EV",fixedOrderId);
			event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
					"No existe el fixed_order_id: " + fixedOrderId + " en la tabla AFE_ACK_EV", "");
			logEventService.sendLogEvent(event);
			*/
			ackgmMessagesHandler.createAndLogMessageFixedOrderAck(fixedOrderId);
			return Boolean.FALSE;
		}

		try {
			// QUERY7
			acks.get(0).setAckStatus(maxTransitDetail.getReqstStatus());
			
			JSONArray jsArray = new JSONArray(maxTransitDetail.getMesagge());
			
			acks.get(0).setAckMsg(jsArray.toString());
			acks.get(0).setUpdateTimeStamp(new Date());
			afeAckEvRepository.saveAndFlush(acks.get(0));
		} catch (Exception e) {
			/*log.debug(
					"Fallo en la ejecución del query de actualización en la tabla AFE_FIXED_ORDERS_EV con el query ");
			event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
					"Fallo en la ejecución del query de actualización en la tabla AFE_FIXED_ORDERS_EV con el query ",
					"");
			logEventService.sendLogEvent(event);*/
			
			ackgmMessagesHandler.createAndLogMessageQueryFailed("");
			return Boolean.FALSE;
		}

		log.debug("End:: Failed Flow ");

		return Boolean.TRUE;

	}

	private Boolean canceledFlow(final Long fixedOrderId, MaxTransitResponseVO maxTransitDetail) {
		log.debug("Start:: Failed Flow ");
		EventVO event;
		
		List<AfeAckEvEntity> acks = afeAckEvRepository.findAllByFixedOrderId(fixedOrderId);

		if (acks.isEmpty()) {
			/*
			log.debug("No existe el fixed_order_id: {} en la tabla AFE_ACK_EV",fixedOrderId);
			event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
					"No existe el fixed_order_id: " + fixedOrderId + " en la tabla AFE_ACK_EV", "");
			logEventService.sendLogEvent(event);*/
			
			ackgmMessagesHandler.createAndLogMessageFixedOrderAck(fixedOrderId);
			return Boolean.FALSE;
		}
		
		if(!acks.get(0).getAckStatus().equalsIgnoreCase(AckgmConstants.FAILED_STATUS)){
			
			try {
				// QUERY7
				acks.get(0).setAckStatus(maxTransitDetail.getReqstStatus());
				
				JSONArray jsArray = new JSONArray(maxTransitDetail.getMesagge());
				
				acks.get(0).setAckMsg(jsArray.toString());
				acks.get(0).setUpdateTimeStamp(new Date());
				afeAckEvRepository.saveAndFlush(acks.get(0));
			} catch (Exception e) {
				/*
				log.debug(
						"Fallo en la ejecución del query de actualización en la tabla AFE_FIXED_ORDERS_EV con el query ");
				event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
						"Fallo en la ejecución del query de actualización en la tabla AFE_FIXED_ORDERS_EV con el query ",
						"");
				logEventService.sendLogEvent(event);*/
				
				ackgmMessagesHandler.createAndLogMessageQueryFailed("");
				
				return Boolean.FALSE;
			}
			
		}else {
			/*
			log.debug("La orden: {} tiene un esatus: {} NO es posible cancelarla en la tabla AFE_ACK_EV",fixedOrderId,AckgmConstants.FAILED_STATUS);
			event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
					"La orden: " + fixedOrderId + " tiene un esatus: "+AckgmConstants.FAILED_STATUS+" NO es posible cancelarla en la tabla AFE_ACK_EV", "");
			logEventService.sendLogEvent(event);
			*/
			ackgmMessagesHandler.createAndLogMessageNoCancelOrder(fixedOrderId);
			return Boolean.FALSE;
			
		}

		log.debug("End:: Failed Flow ");

		return Boolean.TRUE;

	}

	private Boolean finalFlow(MaxTransitResponseVO maxTransitDetail, AfeFixedOrdersEvEntity fixedOrder) {

		log.debug("Start:: finalFlow");
		EventVO event;
		try {
			// QUERY3
			fixedOrder.setEnvioFlag(Boolean.FALSE);
			fixedOrder.setUpdateTimeStamp(new Date());
			afeFixedOrdersEvRepository.saveAndFlush(fixedOrder);

		} catch (Exception e) {
			log.debug("Fallo en la ejecución del query de actualización en la tabla AFE_ACK_EV con el query ");
			event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
					"Fallo en la ejecución del query de inserción en la tabla AFE_ACK_EV con el query ", "");
			logEventService.sendLogEvent(event);
			return Boolean.FALSE;
		}

		// QUERY4
		List<AfeActionEntity> actions = afeActionRepository.findAllByAction(maxTransitDetail.getAction());
		if (actions.isEmpty()) {
			log.debug(
					"No se encontro la accion: \"+maxTransitDetail.getAction()+\" en la tabla AFE_ACTION  con el query\"");
			event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS, "No se encontro la accion: "
					+ maxTransitDetail.getAction() + " en la tabla AFE_ACTION  con el query", "");
			logEventService.sendLogEvent(event);
			return Boolean.FALSE;
		}

		// QUERY5
		AfeOrdersHistoryEntity orderHistory = new AfeOrdersHistoryEntity();

		try {
			orderHistory.setActionId(actions.get(0).getId());
			orderHistory.setFixedOrderId(fixedOrder.getId());
			orderHistory.setCreationTimeStamp(new Date());
			afeOrdersHistoryRepository.save(orderHistory);

			log.debug("El proceso fue exitoso para la orden: {}", fixedOrder.getId());
			event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
					"El proceso fue exitoso para la orden: " + fixedOrder.getId(), "");
			logEventService.sendLogEvent(event);
		} catch (Exception e) {
			log.debug("Fallo de inserción en la tabla AFE_ORDER_HISOTRY");
			event = new EventVO(serviceName, AckgmConstants.ZERO_STATUS,
					"Fallo de inserción en la tabla AFE_ORDER_HISOTRY con el query ", "");
			logEventService.sendLogEvent(event);
			return Boolean.FALSE;

		}

		log.debug("End:: finalFlow");
		return Boolean.TRUE;

	}

}
