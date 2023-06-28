package com.honda.olympus.ms.movfile_mftp.service;

import com.honda.olympus.ms.movfile_mftp.client.FtpClient;
import com.honda.olympus.ms.movfile_mftp.client.SftpClient;
import com.jcraft.jsch.SftpException;
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

import java.util.List;
import java.util.Vector;


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

//	@Autowired
//	private SftpClient sftpClient;


	@Value("${service.name}")
	private String serviceName;

	@Value("${mftp.outbound}")
	private String outbound;

	@Value("${sftp.host}")
	private String sftpHost;

	@Value("${sftp.port}")
	private int sftpPort;

	@Value("${sftp.user}")
	private String sftpUser;

	@Value("${sftp.password}")
	private String sftpPassword;
	
	
	public void uploadFile(Message message)
	{
		if (message.getStatus() == Status._SUCCESS) 
		{
			if (StringUtils.hasText(message.getFile())) {
				FtpClient ftpClient = new FtpClient(sftpHost, sftpPort, sftpUser, sftpPassword, outbound);

				try {
					ftpClient.connect();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}


//				try {
//					this.sftpClient.listFiles(this.outbound);
//				} catch (SftpException e) {
//					throw new RuntimeException(e);
//				}
//				mftpService.uploadFile(message.getFile());
			}
			else {
				Event event = fileErrorEvent();
				logEventService.logEvent(event);
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, event.getMsg());
			}
		}
		else {
			Event event = statusErrorEvent(message);
			logEventService.logEvent(event);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, event.getMsg());
		}
	}
	
	
	private Event statusErrorEvent(Message message) {
		return new Event(serviceName, Status._FAIL, String.format(MSG_STATUS_ERROR, message), message.getFile());
	}
	
	private Event fileErrorEvent() {
		return new Event(serviceName, Status._FAIL, MSG_FILE_ERROR, "");
	}
	
}
