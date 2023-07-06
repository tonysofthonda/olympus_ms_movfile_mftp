package com.honda.olympus.ms.movfile_mftp.service;

import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.honda.olympus.ms.movfile_mftp.client.MftpClient;
import com.honda.olympus.ms.movfile_mftp.client.MftpConfig;
import com.honda.olympus.ms.movfile_mftp.domain.Event;

import static com.honda.olympus.ms.movfile_mftp.domain.Status.*;

import lombok.Setter;


@Setter
@Service
public class MftpService 
{
	
	private static final String MSG_CONNECTION_ERROR = "106 Error al guardar en MFT. No es posible conectarse al MFT";
	private static final String MSG_SEARCH_ERROR = "El archivo %s NO existe en la ubicación %s";
	private static final String MSG_UPLOAD_ERROR = "106 Error al guardar en MFT. No es posible conectarse al MFT en la interface de AFE-AHM";
	private static final String MSG_OUTBOUND_ERROR = "La ruta '%s' NO existe en el servidor MFTP";
	private static final String MSG_UPLOAD_SUCCESS = "Guardado en MFT. El archivo %s fue guardado con éxito en MFT";

	
	@Autowired private MftpConfig config;
	@Autowired private LogEventService logEventService;
	@Autowired private NotificationService notificationService;
	
	
	@Value("${service.name}")
	private String serviceName; 
	
	
	public void uploadFile(String fileName) {
		MftpClient client = new MftpClient(config, fileName);
		uploadFile(client, fileName);
	}
	
	
	public void uploadFile(MftpClient client, String fileName) 
	{	
		// connect to mftp server
		if (!client.open()) {
			Event event = connectionErrorEvent(fileName);
			logEventService.logEvent(event);
			notificationService.sendNotification(event);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, event.getMsg());
		}
		
		// search local file
		if (!client.localFileExists()) {
			client.close();
			Event event = searchErrorEvent(fileName);
			logEventService.logEvent(event);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, event.getMsg());
		}
		
		// upload file
		if (client.uploadFile()) {
			client.close();
			client.deleteLocalFile();
			Event event = uploadOkEvent(fileName);
			logEventService.logEvent(event);
			notificationService.sendNotification(event);
		} else {
			client.close();
			Event event = client.remoteDirFound() ?  uploadErrorEvent(fileName) : outboundErrorEvent(fileName);
			logEventService.logEvent(event);
			notificationService.sendNotification(event);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, event.getMsg());
		}
		
	}
	
	
	private Event connectionErrorEvent(String fileName) {
		return new Event(serviceName, _FAIL, MSG_CONNECTION_ERROR, fileName);
	}
	
	private Event searchErrorEvent(String fileName) {
		return new Event(serviceName, _FAIL, format(MSG_SEARCH_ERROR, fileName, config.getSource()), fileName);
	}
	
	private Event uploadOkEvent(String fileName) {
		return new Event(serviceName, _SUCCESS, format(MSG_UPLOAD_SUCCESS, fileName), fileName);
	}
	
	private Event uploadErrorEvent(String fileName) {
		return new Event(serviceName, _FAIL, MSG_UPLOAD_ERROR, fileName);
	}
	
	private Event outboundErrorEvent(String fileName) {
		return new Event(serviceName, _FAIL, format(MSG_OUTBOUND_ERROR, config.getOutbound()), fileName);
	}
	
}
