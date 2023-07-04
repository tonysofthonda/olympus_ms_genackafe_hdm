package com.honda.olympus.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.honda.olympus.dao.AfeAckMsgEntity;
import com.honda.olympus.dao.AfeActionEvEntity;
import com.honda.olympus.dao.AfeColorEntity;
import com.honda.olympus.dao.AfeDivisionEntity;
import com.honda.olympus.dao.AfeFixedOrdersEvEntity;
import com.honda.olympus.dao.AfeModelColorEntity;
import com.honda.olympus.dao.AfeModelEntity;
import com.honda.olympus.dao.AfeOrdersActionHistoryEntity;
import com.honda.olympus.dao.AfePlantEntity;
import com.honda.olympus.exception.FileProcessException;
import com.honda.olympus.exception.GenackafeException;
import com.honda.olympus.repository.AfeAckEvRepository;
import com.honda.olympus.repository.AfeAckMsgRepository;
import com.honda.olympus.repository.AfeActionRepository;
import com.honda.olympus.repository.AfeColorRepository;
import com.honda.olympus.repository.AfeDivisionRepository;
import com.honda.olympus.repository.AfeFixedOrdersEvRepository;
import com.honda.olympus.repository.AfeModelColorRepository;
import com.honda.olympus.repository.AfeModelRepository;
import com.honda.olympus.repository.AfeModelTypeRepository;
import com.honda.olympus.repository.AfeOrdersActionHistoryRepository;
import com.honda.olympus.repository.AfePlantRepository;
import com.honda.olympus.utils.GenackMessagesHandler;
import com.honda.olympus.utils.GenackafeConstants;
import com.honda.olympus.utils.GenackafeUtils;
import com.honda.olympus.vo.EventVO;
import com.honda.olympus.vo.GenAckResponseVO;
import com.honda.olympus.vo.MessageEventVO;
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

	@Autowired
	AfeDivisionRepository afeDivisionRepository;

	@Autowired
	AfePlantRepository afePlantRepository;

	@Autowired
	AfeOrdersActionHistoryRepository afeOrdersActionHistoryRepository;

	@Autowired
	AfeAckMsgRepository afeAckMsgRepository;

	@Value("${service.name}")
	private String serviceName;

	@Value("${folder.source}")
	private String folderSource;

	@Value("${template.control}")
	private Integer templateControl;
	@Value("{service.success.message}")
	private String successMessage;

	List<TemplateFieldVO> templateData;

	@Autowired
	GenackMessagesHandler genackMessagesHandler;

	@Autowired
	NotificationService notificationService;

	private String output;

	public GenAckResponseVO createFile(MessageEventVO message) throws FileProcessException, GenackafeException {

		JSONObject template = GenackafeUtils.validateFileTemplate(templateControl);
		this.templateData = GenackafeUtils.readGenAckAfeFileTemplate(template);

		final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Boolean success = Boolean.FALSE;

		if (!message.getStatus().equals(GenackafeConstants.ONE_STATUS)) {

			genackMessagesHandler.createAndLogMessageStatusFail(message.getStatus());
			throw new GenackafeException(
					"El mensaje tiene un status no aceptado para el proceso " + message.toString());

		}

		if (message.getDetails().isEmpty()) {

			genackMessagesHandler.createAndLogMessageDetailFail(message.toString());
			throw new GenackafeException("El mensaje no tiene detalles para procesar: " + message.toString());

		}

		String fileName = GenackafeUtils.getFileName();

		this.output = folderSource + fileName;

		Iterator<Long> it = message.getDetails().iterator();
		EventVO event;
		StringBuilder fileLine;

		while (it.hasNext()) {
			Long fixedOrderId = it.next();
			fileLine = new StringBuilder();

			// QUERY1
			List<AfeFixedOrdersEvEntity> fixedOrders = afeFixedOrdersEvRepository.findAllById(fixedOrderId);

			if (fixedOrders.isEmpty()) {
				log.info("No se encontro el fixed_order_id: {} en la tabla AFE_FIXED_ORDERS_EV con el query {}",
						fixedOrderId, "SELECT o FROM AfeFixedOrdersEvEntity o WHERE o.id = :id ");
				event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS,
						"No se encontro requst_idntfr: " + fixedOrderId + " en la tabla AFE_FIXED_ORDERS_EV", "");
				logEventService.sendLogEvent(event);

				String notificationMessage = "107 Error al guardar en MFT. No se puede guardar correctamente debido a los datos";
				MessageVO messageNotification = new MessageVO(serviceName, GenackafeConstants.ZERO_STATUS,
						notificationMessage, "");
				notificationService.generatesNotification(messageNotification);

				// return to main line process loop
				continue;
			}

			AfeFixedOrdersEvEntity fixedOrderQ1 = fixedOrders.get(0);
			Long idQ1 = fixedOrders.get(0).getId();
			String requestIdQ1 = fixedOrders.get(0).getRequestId().trim();
			Long modelColorId = fixedOrders.get(0).getModelColorId();
			Long actionIdQ1 = fixedOrders.get(0).getActionId();
			Date weekStrtDatQ1 = fixedOrders.get(0).getProdWeekStartDay();
			Date ordDueDteQ1 = fixedOrders.get(0).getOrdDueDt();
			String orderNumberQ1 = fixedOrders.get(0).getOrderNumber().trim();

			// QUERY2
			List<AfeModelColorEntity> modelColors = afeModelColorRepository.findAllById(modelColorId);

			if (modelColors.isEmpty()) {
				genackMessagesHandler.createAndLogMessageModelColorNoExists(modelColorId, idQ1,
						"SELECT * FROM MODEL_COLOR_ID WHERE ID");
				continue;
			}

			AfeModelColorEntity modelColorQ2 = modelColors.get(0);
			Long modelIdQ2 = modelColors.get(0).getModel_id();
			Long colorIdQ2 = modelColors.get(0).getColorId();

			// QUERY3
			List<AfeModelEntity> models = afeModelRepository.findAllById(modelIdQ2);

			if (models.isEmpty()) {

				genackMessagesHandler.createAndLogMessageModelNoExist(modelIdQ2,
						"SELECT * FROM MODEL_COLOR WHERE  WHERE ID");

				continue;
			}

			AfeModelEntity modelQ3 = models.get(0);
			Long divisionIdQ3 = modelQ3.getDivisionId();
			Long plantIdQ3 = modelQ3.getPlantId();
			Long mdlIdQ3 = modelQ3.getId();
			String mdlType = "";

			if (modelQ3.getModelTypeId() == 1) {
				mdlType = "KC";
			}

			if (modelQ3.getModelTypeId() == 2) {
				mdlType = "KA";
			}

			// QUERY4
			Optional<AfeDivisionEntity> afeDivision = afeDivisionRepository.findById(divisionIdQ3);
			if (!afeDivision.isPresent()) {

				genackMessagesHandler.createAndLogMessageDvisionNoExist(divisionIdQ3, fixedOrderId,
						"SELECT * FROM AFE_PLANT WHERE ID");
				continue;
			}

			String abbrevationQ4 = afeDivision.get().getAbbreviation().trim();

			// QUERY5
			Optional<AfePlantEntity> afePlant = afePlantRepository.findById(plantIdQ3);
			if (!afePlant.isPresent()) {

				genackMessagesHandler.createAndLogMessagePlantNoExist(divisionIdQ3, fixedOrderId,
						"SELECT * FROM AFE_DIVISION WHERE ID");
				continue;
			}

			String abbrevationQ5 = afePlant.get().getAbbreviation().trim();

			// QUERY6
			List<AfeColorEntity> colors = afeColorRepository.findAllById(colorIdQ2);

			if (colors.isEmpty()) {

				genackMessagesHandler.createAndLogMessageColorNoExists(colorIdQ2, fixedOrderId,
						"SELEC * FROM AFE_COLOR WHERE ID");

				continue;
			}

			AfeColorEntity colorQ6 = colors.get(0);

			String colorCodeQ6 = colorQ6.getCode().trim();
			String colorExtCodeQ6 = colorQ6.getExteriorCode().trim();
			String colorIntCodeQ6 = colorQ6.getInteriorCode().trim();

			// QUERY7
			List<AfeActionEvEntity> actions = afeActionRepository.findAllByAction(actionIdQ1);

			if (actions.isEmpty()) {
				genackMessagesHandler.createAndLogMessageActionNoExists(actionIdQ1, fixedOrderId,
						"SELECT * FROM AFE_ACTION_EV WHERE ID");
				continue;
			}

			AfeActionEvEntity actionEntQ7 = actions.get(0);

			String actionQ7 = actionEntQ7.getAction();

			// QUERY8
			List<AfeOrdersActionHistoryEntity> ordersHistories = afeOrdersActionHistoryRepository
					.findAllByFixedOrder(idQ1);

			if (ordersHistories.isEmpty()) {

				genackMessagesHandler.createAndLogMessageOrdersHistoryNoExists(fixedOrderId,
						"SELECT * FROM AFE_ORDER_ACTION_HISTORY WHERE FIXED_ORDER_ID ORDER BY DESC");
				continue;
			}

			Long idQ8 = ordersHistories.get(0).getId();

			// QUERY9
			List<AfeAckMsgEntity> ackMesagges = afeAckMsgRepository.findAllByFixedOrderId(idQ8);

			if (ackMesagges.isEmpty()) {
				log.debug("No existen ack_messages for orders_histories {} ", idQ8);
				genackMessagesHandler.createAndLogMessageNoAckMessageExists(idQ8, fixedOrderId,
						"SELECT * FROM AFE_ACK_MSG WHERE ID ");
				continue;
			}

			String ackStatusQ9 = ackMesagges.get(0).getAckStatus().trim();
			String ackStatusMsgQ9 = ackMesagges.get(0).getAckMesage().trim();
			Date timeStpQ9 = (Date) ackMesagges.get(0).getLastChangeTimestamp();

			try {

				fileLine.append(completeSpaces(abbrevationQ4, "XPROD-DIV-CD"));
				fileLine.append(completeSpaces(abbrevationQ5, "PLANT-ID"));

				fileLine.append(completeSpaces("" + formatter.format(weekStrtDatQ1), "GM-PROD-WEEK-START-DAY"));
				fileLine.append(completeSpaces("" + formatter.format(ordDueDteQ1), "GM-ORD-DUE-DT"));

				fileLine.append(completeSpaces("" + mdlIdQ3, "MDL-ID"));
				fileLine.append(completeSpaces(mdlType, "MDL-TYP-CD"));
				fileLine.append("   "); // "MDL-OPT-PKG-CD"

				fileLine.append(completeSpaces(colorCodeQ6, "MDL-MFG-COLOR-ID"));
				fileLine.append(completeSpaces(colorExtCodeQ6, "EXTR-COLOR-CD"));
				fileLine.append(completeSpaces(colorIntCodeQ6, "INT-COLOR-CD"));
				fileLine.append(completeSpaces(actionQ7, "GM-ORD-ACK-ACTION"));
				fileLine.append(completeSpaces(orderNumberQ1, "GM-ORD-ACK-VEH-ORD-NO"));
				fileLine.append(completeSpaces(ackStatusQ9, "GM-ORD-ACK-REQST-STATUS"));
				fileLine.append(completeSpaces(ackStatusMsgQ9, "GM-ORD-ACK-MSG"));

				String timeStamp = GenackafeUtils.formatDateTimeStamp(timeStpQ9);
				fileLine.append(completeSpaces(timeStamp, "GM-ORD-ACK-VO-LAST-CHG-TMSTP"));
				fileLine.append(completeSpaces("" + requestIdQ1, "GM-ORD-ACK-REQST-ID"));
				fileLine.append("                                                               ");

				log.debug("Length: " + fileLine.length());
				log.debug(fileLine.toString());

				fileLine = completeLineSpaces(fileLine, templateControl);

				try {

					addLineToFile(fileLine.toString());

				} catch (IOException e) {
					log.error("Line not added due to: {} ", e.getLocalizedMessage());
					continue;
				}

				// GenackafeUtils.checkFileIfWriteFile(folderSource, fileName,
				// fileLine.toString());
			} catch (GenackafeException e) {
				log.info("El archivo {} NO fue creado correctamente en la ubicación: {}", fileName, folderSource);
				event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS,
						"El archivo " + fileName + " NO fue creado correctamente en la ubicación: " + folderSource, "");
				logEventService.sendLogEvent(event);
				continue;
			}

			success = Boolean.TRUE;
			fileLine.setLength(0);

		}

		if (success) {
			log.debug("Genackafe:: Se creo correctamente el archivo {} en la ubicación:{} ", fileName, folderSource);
			event = new EventVO(serviceName, GenackafeConstants.ZERO_STATUS,
					"Se creo correctamente el archivo " + fileName + " en la ubicación: " + folderSource, "");
			logEventService.sendLogEvent(event);
			movFileService.sendMoveFileMessage(new MoveFileVO(1L, successMessage, fileName));
			return new GenAckResponseVO(success, fileName);
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
					log.debug("Field incorrect: {} with value: {}", templeateField, value);
					throw new GenackafeException("Field incorrect: " + templeateField + " with value: " + value);
				}
			} else {
				spaces.append(value);
			}

			return spaces.toString();
		}

		throw new GenackafeException("Field incorrect: " + templeateField + " with value: " + value);
	}

	private StringBuilder completeLineSpaces(StringBuilder line, Integer templateControl) {

		if (line.length() < templateControl) {
			Integer difference = templateControl - line.length();
			for (Integer i = 0; i < difference; i++) {

				line.append(" ");
			}
			log.debug("Line completed with {} characters", difference);
		}

		return line;

	}

	private void addLineToFile(String line) throws IOException {

		String newLine = line + "\n";
		final Path path = Paths.get(this.output);

		if (path == null) {
			Files.createFile(path);
		}

		Files.write(path, newLine.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);

	}

}
