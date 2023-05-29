package com.honda.olympus.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.honda.olympus.dao.AfeAckEvEntity;
import com.honda.olympus.dao.AfeActionEntity;
import com.honda.olympus.dao.AfeColorEntity;
import com.honda.olympus.dao.AfeFixedOrdersEvEntity;
import com.honda.olympus.dao.AfeModelColorEntity;
import com.honda.olympus.dao.AfeModelEntity;
import com.honda.olympus.dao.AfeModelTypeEntity;
import com.honda.olympus.repository.AfeAckEvRepository;
import com.honda.olympus.repository.AfeActionRepository;
import com.honda.olympus.repository.AfeColorRepository;
import com.honda.olympus.repository.AfeFixedOrdersEvRepository;
import com.honda.olympus.repository.AfeModelColorRepository;
import com.honda.olympus.repository.AfeModelRepository;
import com.honda.olympus.repository.AfeModelTypeRepository;
import com.honda.olympus.utils.GenackafeConstants;
import com.honda.olympus.utils.GenackafeUtils;
import com.honda.olympus.vo.EventVO;
import com.honda.olympus.vo.MessageVO;

public class GenackafeService {

	@Autowired
	LogEventService logEventService;

	@Autowired
	private AfeFixedOrdersEvRepository afeFixedOrdersEvRepository;

	@Autowired
	private AfeModelColorRepository afeModelColorRepository;

	@Autowired
	private AfeModelRepository afeModelRepository;

	@Autowired
	AfeModelTypeRepository modelTypeRepository;

	@Autowired
	AfeColorRepository afeColorRepository;

	@Autowired
	AfeActionRepository afeActionRepository;

	@Autowired
	AfeAckEvRepository afeAckEvRepository;

	@Value("${service.name}")
	private String serviceName;

	@Value("${folder.source}")
	private String folderSource;

	public void createFile(MessageVO message) {

		final DateTimeFormatter CURRENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		Boolean success = Boolean.FALSE;

		Calendar currentTieme = new GregorianCalendar();

		if (message.getStatus() != GenackafeConstants.ONE_STATUS) {

			logEventService.sendLogEvent(new EventVO(serviceName, GenackafeConstants.ZERO_STATUS,
					"El mensaje tiene un status no aceptado para el proceso " + message.toString(), ""));
			System.out.println("El mensaje tiene un status no aceptado para el proceso " + message.toString());

		}

		if (message.getDetails().isEmpty()) {

			logEventService.sendLogEvent(new EventVO(serviceName, GenackafeConstants.ZERO_STATUS,
					"El mensaje no tiene detalles para procesar: " + message.toString(), ""));
			System.out.println("El mensaje no tiene detalles para procesar: " + message.toString());

		}

		String fileName = GenackafeUtils.getFileName();

		Iterator<Long> it = message.getDetails().iterator();
		EventVO event;
		StringBuilder fileLine;

		while (it.hasNext()) {
			Long fixedOrderId = it.next();
			fileLine = new StringBuilder();

			// QUERY1
			List<AfeFixedOrdersEvEntity> fixedOrders = afeFixedOrdersEvRepository.findAllById(fixedOrderId);

			if (fixedOrders.isEmpty()) {
				System.out
						.println("No se encontro requst_idntfr: " + fixedOrderId + " en la tabla AFE_FIXED_ORDERS_EV");
				event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS,
						"No se encontro requst_idntfr: " + fixedOrderId + " en la tabla AFE_FIXED_ORDERS_EV", "");
				logEventService.sendLogEvent(event);
				// return to main line process loop
				break;
			}
			
			AfeFixedOrdersEvEntity fixedOrderQ1 = fixedOrders.get(0);

			// QUERY2
			List<AfeModelColorEntity> modelColors = afeModelColorRepository
					.findAllById(fixedOrders.get(0).getModelColorId());

			if (modelColors.isEmpty()) {
				System.out.println("No se existe el model_color_id: " + fixedOrders.get(0).getModelColorId()
						+ " en la tabla AFE_MODEL_COLOR");
				event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS, "No se existe el model_color_id: "
						+ fixedOrders.get(0).getModelColorId() + " en la tabla AFE_MODEL_COLOR", "");
				logEventService.sendLogEvent(event);
				// return to main line process loop
				break;
			}

			// QUERY3
			List<AfeModelEntity> models = afeModelRepository.findAllById(modelColors.get(0).getModel_id());

			if (models.isEmpty()) {
				System.out.println(
						"No se existe el model_id: " + modelColors.get(0).getModel_id() + " en la tabla AFE_MODEL");
				event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS, "No se existe el model_color_id: "
						+ modelColors.get(0).getModel_id() + " en la tabla AFE_MODEL", "");
				logEventService.sendLogEvent(event);
				// return to main line process loop
				break;
			}
			
			AfeModelEntity modelQ3 = models.get(0);

			// QUERY4
			List<AfeModelTypeEntity> modelTypes = modelTypeRepository.findAllById(modelQ3.getModelTypeId());
			if (modelTypes.isEmpty()) {
				System.out.println("No existe el model_type: " + modelQ3.getModelTypeId()
						+ " para el fixed_order_id: " + fixedOrderId);
				event = new EventVO(
						serviceName, GenackafeConstants.ZERO_STATUS, "No existe el model_type: "
								+ modelQ3.getModelTypeId() + " para el fixed_order_id: " + fixedOrderId,
						fileName);
				logEventService.sendLogEvent(event);

				// return to main line process loop
				return;
			}
			
			AfeModelTypeEntity modelTypeQ4 = modelTypes.get(0);

			// QUERY5
			List<AfeColorEntity> colors = afeColorRepository.findAllById(modelColors.get(0).getColorId());

			if (colors.isEmpty()) {
				System.out.println(
						"El CODE de COLOR " + modelColors.get(0).getColorId() + " NO existe en la tabla AFE_COLOR ");
				event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS,
						"El CODE de COLOR " + modelColors.get(0).getColorId() + " NO existe en la tabla AFE_COLOR ",
						fileName);
				logEventService.sendLogEvent(event);

				// return to main line process loop
				return;
			}
			
			AfeColorEntity color = colors.get(0);

			// QUERY6
			List<AfeActionEntity> actions = afeActionRepository.findAllByAction(fixedOrders.get(0).getActionId());

			if (actions.isEmpty()) {
				System.out.println(
						"La ACTION " + fixedOrders.get(0).getActionId() + " NO existe en la tabla AFE_ACTION ");
				event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS,
						"La ACTION " + fixedOrders.get(0).getActionId() + " NO existe en la tabla AFE_ACTION ",
						fileName);
				logEventService.sendLogEvent(event);

				// return to main line process loop
				return;
			}
			
			AfeActionEntity actionQ6 = actions.get(0);
			

			// QUERY7
			List<AfeAckEvEntity> acks = afeAckEvRepository.findAllByFixedOrderId(fixedOrderId);

			if (acks.isEmpty()) {
				System.out.println("No existe el fixed_order_id: " + fixedOrderId + " en la tabla AFE_ACK_EV");
				event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS,
						"No existe el fixed_order_id: " + fixedOrderId + " en la tabla AFE_ACK_EV", "");
				logEventService.sendLogEvent(event);
				return;
			}
			
			AfeAckEvEntity ackQ7 = acks.get(0);
			
			
			fileLine.append("A");
			fileLine.append("M");
			fileLine.append(LocalDate.now().format(CURRENT_DATE_FORMATTER));
			fileLine.append(modelQ3.getCode());
			fileLine.append(modelTypeQ4.getModelType());
			fileLine.append(color.getCode());
			fileLine.append(color.getExteriorCode());
			fileLine.append(color.getInteriorCode());
			fileLine.append(actionQ6.getAction());
			fileLine.append(fixedOrderQ1.getOrderNumber());
			fileLine.append(ackQ7.getAckStatus());
			fileLine.append(ackQ7.getAckMsg());
			fileLine.append(ackQ7.getLastChangeTimestamp());
			fileLine.append(fixedOrderQ1.getRequestId());
			

		}

	}

}
