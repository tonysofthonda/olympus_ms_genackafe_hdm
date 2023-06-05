package com.honda.olympus.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.honda.olympus.dao.AfeAckEvEntity;
import com.honda.olympus.dao.AfeActionEntity;
import com.honda.olympus.dao.AfeColorEntity;
import com.honda.olympus.dao.AfeFixedOrdersEvEntity;
import com.honda.olympus.dao.AfeModelColorEntity;
import com.honda.olympus.dao.AfeModelEntity;
import com.honda.olympus.dao.AfeModelTypeEntity;
import com.honda.olympus.exception.FileProcessException;
import com.honda.olympus.exception.GenackafeException;
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
import com.honda.olympus.vo.GenAckResponseVO;
import com.honda.olympus.vo.MessageVO;
import com.honda.olympus.vo.MoveFileVO;
import com.honda.olympus.vo.TemplateFieldVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GenackafeService {

	@Autowired
	LogEventService logEventService;
	
	@Autowired
	MovFileService movFileService;

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

	@Value("${template.control}")
	private Integer templateControl;
	@Value("{service.success.message}")
	private String successMessage;

	List<TemplateFieldVO> templateData;

	public GenAckResponseVO createFile(MessageVO message) throws FileProcessException,GenackafeException {

		JSONObject template = GenackafeUtils.validateFileTemplate(templateControl);
		this.templateData = GenackafeUtils.readGenAckAfeFileTemplate(template);

		final DateTimeFormatter CURRENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		Boolean success = Boolean.FALSE;

		if (message.getStatus() != GenackafeConstants.ONE_STATUS) {

			logEventService.sendLogEvent(new EventVO(serviceName, GenackafeConstants.ZERO_STATUS,
					"El mensaje tiene un status no aceptado para el proceso " + message.toString(), ""));
			log.info("El mensaje tiene un status no aceptado para el proceso {}",message.toString());
			throw new GenackafeException("El mensaje tiene un status no aceptado para el proceso " + message.toString());

		}

		if (message.getDetails().isEmpty()) {

			logEventService.sendLogEvent(new EventVO(serviceName, GenackafeConstants.ZERO_STATUS,
					"El mensaje no tiene detalles para procesar: " + message.toString(), ""));
			log.info("El mensaje no tiene detalles para procesar: {}",message.toString());
			throw new GenackafeException("El mensaje no tiene detalles para procesar: " + message.toString());

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
				log.info("No se encontro requst_idntfr: {} en la tabla AFE_FIXED_ORDERS_EV",fixedOrderId);
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
				log.info("No se existe el model_color_id: {} en la tabla AFE_MODEL_COLOR",fixedOrders.get(0).getModelColorId());
				event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS, "No se existe el model_color_id: "
						+ fixedOrders.get(0).getModelColorId() + " en la tabla AFE_MODEL_COLOR", "");
				logEventService.sendLogEvent(event);
				// return to main line process loop
				break;
			}

			AfeModelColorEntity modelColorQ2 = modelColors.get(0);

			// QUERY3
			List<AfeModelEntity> models = afeModelRepository.findAllById(modelColors.get(0).getModel_id());

			if (models.isEmpty()) {
				log.info(
						"No se existe el model_id: {} en la tabla AFE_MODEL",modelColors.get(0).getModel_id());
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
				log.info("No existe el model_type: {} para el fixed_order_id: {} ",modelQ3.getModelTypeId(),fixedOrderId);
				event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS, "No existe el model_type: "
						+ modelQ3.getModelTypeId() + " para el fixed_order_id: " + fixedOrderId, fileName);
				logEventService.sendLogEvent(event);

				// return to main line process loop
				break;
			}

			AfeModelTypeEntity modelTypeQ4 = modelTypes.get(0);

			// QUERY5
			List<AfeColorEntity> colors = afeColorRepository.findAllById(modelColorQ2.getColorId());

			if (colors.isEmpty()) {
				log.info(
						"El CODE de COLOR {} NO existe en la tabla AFE_COLOR ",modelColors.get(0).getColorId());
				event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS,
						"El CODE de COLOR " + modelColors.get(0).getColorId() + " NO existe en la tabla AFE_COLOR ",
						fileName);
				logEventService.sendLogEvent(event);

				// return to main line process loop
				break;
			}

			AfeColorEntity colorQ5 = colors.get(0);

			// QUERY6
			List<AfeActionEntity> actions = afeActionRepository.findAllByAction(fixedOrderQ1.getActionId());

			if (actions.isEmpty()) {
				log.info(
						"La ACTION {} NO existe en la tabla AFE_ACTION ",fixedOrders.get(0).getActionId());
				event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS,
						"La ACTION " + fixedOrders.get(0).getActionId() + " NO existe en la tabla AFE_ACTION ",
						fileName);
				logEventService.sendLogEvent(event);

				// return to main line process loop
				break;
			}

			AfeActionEntity actionQ6 = actions.get(0);

			// QUERY7
			List<AfeAckEvEntity> acks = afeAckEvRepository.findAllByFixedOrderId(fixedOrderId);

			if (acks.isEmpty()) {
				log.info("No existe el fixed_order_id: {} en la tabla AFE_ACK_EV",fixedOrderId);
				event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS,
						"No existe el fixed_order_id: " + fixedOrderId + " en la tabla AFE_ACK_EV", "");
				logEventService.sendLogEvent(event);
				break;
			}

			try {
				AfeAckEvEntity ackQ7 = acks.get(0);

				fileLine.append(completeSpaces("A", "XPROD-DIV-CD"));
				fileLine.append(completeSpaces("M", "PLANT-ID"));
				fileLine.append(
						completeSpaces("" + LocalDate.now().format(CURRENT_DATE_FORMATTER), "GM-PROD-WEEK-START-DAY"));
				fileLine.append(completeSpaces(modelQ3.getCode(), "GM-ORD-DUE-DT"));
				fileLine.append(completeSpaces(modelTypeQ4.getModelType(), "MDL-ID"));
				fileLine.append(completeSpaces(colorQ5.getCode(), "MDL-MFG-COLOR-ID"));
				fileLine.append(completeSpaces(colorQ5.getExteriorCode(), "EXTR-COLOR-CD"));
				fileLine.append(completeSpaces(colorQ5.getInteriorCode(), "INT-COLOR-CD"));
				fileLine.append(completeSpaces(actionQ6.getAction(), "GM-ORD-ACK-ACTION"));
				fileLine.append(completeSpaces(fixedOrderQ1.getOrderNumber(), "GM-ORD-ACK-VEH-ORD-NO"));
				fileLine.append(completeSpaces(ackQ7.getAckStatus(), "GM-ORD-ACK-REQST-STATUS"));
				fileLine.append(completeSpaces(ackQ7.getAckMsg(), "GM-ORD-ACK-MSG"));

				String timeStamp = GenackafeUtils.formatDateTimeStamp(ackQ7.getLastChangeTimestamp());
				fileLine.append(completeSpaces(timeStamp, "GM-ORD-ACK-VO-LAST-CHG-TMSTP"));
				fileLine.append(completeSpaces("" + fixedOrderQ1.getRequestId(), "GM-ORD-ACK-REQST-ID"));

				log.info("Length: " + fileLine.length());
				log.info(fileLine.toString());

				fileLine = completeLineSpaces(fileLine,templateControl);

				GenackafeUtils.checkFileIfWriteFile(folderSource, fileName, fileLine.toString());
			} catch (GenackafeException e) {
				log.info(
						"El archivo " + fileName + " NO fue creado correctamente en la ubicación: " + folderSource);
				event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS,
						"El archivo " + fileName + " NO fue creado correctamente en la ubicación: " + folderSource, "");
				logEventService.sendLogEvent(event);
				break;
			}

			success = Boolean.TRUE;
			fileLine.setLength(0);
			
			movFileService.sendMoveFileMessage(new MoveFileVO(1L, successMessage, fileName));

		}

		return new GenAckResponseVO(success, fileName);

	}

	private String completeSpaces(String value, String templeateField) throws GenackafeException {

		StringBuilder spaces = new StringBuilder();
		Optional<TemplateFieldVO> field;
		field = GenackafeUtils.getTemplateValueOfField(this.templateData, templeateField);
		if (field.isPresent()) {
			if (field.get().total != value.length()) {
				if (field.get().total > value.length()) {
					// complete
					spaces.append(value);
					int difference = field.get().total - value.length();

					for (int i = 0; i < difference; i++) {
						spaces.append(" ");
					}
				} else {
					// error
					log.info("Filed incorrect: {} with value: {}", templeateField, value);
					throw new GenackafeException("Filed incorrect: " + templeateField + " with value: " + value);
				}
			} else {
				spaces.append(value);
			}

			return spaces.toString();
		}

		throw new GenackafeException("Filed incorrect: " + templeateField + " with value: " + value);
	}

	private StringBuilder completeLineSpaces(StringBuilder line, Integer templateControl) {

		if (line.length() < templateControl) {
			Integer difference = templateControl - line.length();
			for (Integer i = 0; i < difference; i++) {

				line.append(" ");
			}
			log.info("Line completed with {} characters",difference);
		}

		return line;

	}

}
