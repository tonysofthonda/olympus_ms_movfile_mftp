package com.honda.olympus.ms.movfile_mftp.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.web.server.ResponseStatusException;

import com.honda.olympus.ms.movfile_mftp.domain.Message;
import com.honda.olympus.ms.movfile_mftp.domain.Status;


@TestMethodOrder(OrderAnnotation.class)
public class MovFileServiceTest 
{
	
	static final String SERVICE_NAME = "ms.movfile_mftp";
	
	static MftpService mftpService;
	static LogEventService logEventService;
	static MovFileService movFileService;
	
	
	@BeforeAll
	static void beforeAll() 
	{
		mftpService = mock(MftpService.class);
		logEventService = mock(LogEventService.class);
		movFileService = new MovFileService();
		
		movFileService.setMftpService(mftpService);
		movFileService.setLogEventService(logEventService);
		movFileService.setServiceName(SERVICE_NAME);
	}
	
	
	@Test
	@Order(1)
	void exceptionWithInvalidStatus() {
		Message message = new Message(Status._FAIL, Status.FAIL, "");
		
		assertThatThrownBy(() -> movFileService.uploadFile(message))
			.isInstanceOf(ResponseStatusException.class)
			.hasMessageContaining("El mensaje tiene un status no aceptado para el proceso ");
	}
	
	
	@Test
	@Order(2)
	void exceptionWithInvalidFile() {
		Message message = new Message(Status._SUCCESS, Status.SUCCESS, "");
		
		assertThatThrownBy( () -> movFileService.uploadFile(message) )
			.isInstanceOf(ResponseStatusException.class)
			.hasMessageContaining("NO se recibió el nombre del archivo");
	}
	
	
	@Test
	@Order(3)
	void noExceptionWithValidMessage() {
		Message message = new Message(Status._SUCCESS, Status.SUCCESS, "file.txt");
		assertDoesNotThrow(() -> movFileService.uploadFile(message));
	}

}
