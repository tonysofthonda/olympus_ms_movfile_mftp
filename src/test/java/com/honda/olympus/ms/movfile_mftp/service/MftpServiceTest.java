package com.honda.olympus.ms.movfile_mftp.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.web.server.ResponseStatusException;

import com.honda.olympus.ms.movfile_mftp.client.MftpClient;
import com.honda.olympus.ms.movfile_mftp.client.MftpConfig;


@TestMethodOrder(OrderAnnotation.class)
public class MftpServiceTest 
{
	
	static final String SERVICE_NAME = "ms.movfile_mftp";
	static final String FILE_NAME = "file.txt";
	static final String EMPTY = "";
	
	static MftpConfig config;
	static LogEventService logEventService;
	static NotificationService notificationService;
	static MftpService mftpService;
	
	
	@BeforeAll
	static void beforeAll() {
		config = mock(MftpConfig.class);
		logEventService = mock(LogEventService.class);
		notificationService = mock(NotificationService.class);
		
		mftpService = new MftpService();
		mftpService.setConfig(config);
		mftpService.setLogEventService(logEventService);
		mftpService.setNotificationService(notificationService);
		mftpService.setServiceName(SERVICE_NAME);
	}
	
	
	@Test
	@Order(1)
	void exceptionWithUnreachableFtpServer() 
	{
		MftpClient mftpClient = mock(MftpClient.class);
		when(mftpClient.open()).thenReturn(false);
		
		assertThatThrownBy( () -> mftpService.uploadFile(mftpClient, FILE_NAME) )
			.isInstanceOf(ResponseStatusException.class)
			.hasMessageContaining("Fallo de conexión al sitio MFTP, con los siguientes datos");
	}
	
	
	@Test
	@Order(2)
	void exceptionWithMissingFile() 
	{
		MftpClient mftpClient = mock(MftpClient.class);
		
		when(mftpClient.open()).thenReturn(true);
		when(mftpClient.localFileExists()).thenReturn(false);
		
		assertThatThrownBy( () -> mftpService.uploadFile(mftpClient, FILE_NAME) )
			.isInstanceOf(ResponseStatusException.class)
			.hasMessageContaining("El archivo " + FILE_NAME + " NO existe en la ubicación");
	}
	
	
	@Test
	@Order(3)
	void exceptionWithFailedUpload()
	{
		MftpClient mftpClient = mock(MftpClient.class);
		
		when(mftpClient.open()).thenReturn(true);
		when(mftpClient.localFileExists()).thenReturn(true);
		when(mftpClient.uploadFile()).thenReturn(false);
		
		assertThatThrownBy( () -> mftpService.uploadFile(mftpClient, FILE_NAME) )
			.isInstanceOf(ResponseStatusException.class)
			.hasMessageContaining("El archivo " + FILE_NAME + " NO fue cargado correctamente en el servidor");
	}
	
	
	@Test 
	@Order(4)
	void noExceptionWithOkUpload() 
	{
		MftpClient mftpClient = mock(MftpClient.class);
		
		when(mftpClient.open()).thenReturn(true);
		when(mftpClient.localFileExists()).thenReturn(true);
		when(mftpClient.uploadFile()).thenReturn(true);
		
		assertDoesNotThrow( () -> mftpService.uploadFile(mftpClient, FILE_NAME) );
	}
	
}
