package com.honda.olympus.ms.movfile_mftp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.honda.olympus.ms.movfile_mftp.domain.Event;
import com.honda.olympus.ms.movfile_mftp.domain.Message;
import com.honda.olympus.ms.movfile_mftp.domain.Status;

import lombok.Setter;


@Setter
@Service
public class MovFileService 
{
	
	private static final String MSG_STATUS_ERROR = "El mensaje tiene un status no aceptado para el proceso %s";
	private static final String MSG_FILE_ERROR = "NO se recibió el nombre del archivo";
	
	
	@Autowired
	private MftpService mftpService;
	
	@Autowired
	private LogEventService logEventService;
	
	
	@Value("${service.name}")
	private String serviceName; 
	
	
	public void uploadFile(Message message) 
	{
		if (message.getStatus() == Status._SUCCESS) 
		{
			if (StringUtils.hasText(message.getFile())) {
				mftpService.uploadFile(message.getFile());
			}
			else {
				Event event = fileErrorEvent();
				logEventService.logEvent(event);
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, event.msg());
			}
		}
		else {
			Event event = statusErrorEvent(message);
			logEventService.logEvent(event);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, event.msg());
		}
	}
	
	
	private Event statusErrorEvent(Message message) {
		return new Event(serviceName, Status._FAIL, String.format(MSG_STATUS_ERROR, message), message.getFile());
	}
	
	private Event fileErrorEvent() {
		return new Event(serviceName, Status._FAIL, MSG_FILE_ERROR, "");
	}
	
}
