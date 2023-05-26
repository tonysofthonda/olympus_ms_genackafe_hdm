package com.honda.olympus.service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.honda.olympus.dao.AfeFixedOrdersEvEntity;
import com.honda.olympus.dao.AfeModelColorEntity;
import com.honda.olympus.dao.AfeModelEntity;
import com.honda.olympus.dao.AfeModelTypeEntity;
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

	@Value("${service.name}")
	private String serviceName;

	@Value("${folder.source}")
	private String folderSource;

	public void createFile(MessageVO message) {

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

		while (it.hasNext()) {
			Long fixedOrderId = it.next();

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
			List<AfeModelEntity> models = afeModelRepository
					.findAllById(modelColors.get(0).getModel_id());

			if (models.isEmpty()) {
				System.out.println("No se existe el model_id: " + fixedOrders.get(0).getModelColorId()
						+ " en la tabla AFE_MODEL");
				event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS, "No se existe el model_color_id: "
						+ fixedOrders.get(0).getModelColorId() + " en la tabla AFE_MODEL", "");
				logEventService.sendLogEvent(event);
				// return to main line process loop
				break;
			}
			
			
			List<AfeModelTypeEntity> modelTypes = modelTypeRepository.findAllById(models.get(0).getModelTypeId());
			if (modelTypes.isEmpty()) {
				System.out.println("No existe el model_type: "+models.get(0).getModelTypeId()+" para el fixed_order_id: "+fixedOrderId);
				event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS,
						"No existe el model_type: "+models.get(0).getModelTypeId()+" para el fixed_order_id: "+fixedOrderId, fileName);
				logEventService.sendLogEvent(event);

				// return to main line process loop
				return;
			}
			
			

		}

	}

}
