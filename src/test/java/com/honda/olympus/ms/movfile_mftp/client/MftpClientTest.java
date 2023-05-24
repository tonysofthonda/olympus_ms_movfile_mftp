package com.honda.olympus.ms.movfile_mftp.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import com.honda.olympus.ms.movfile_mftp.util.FileUtil;



@TestMethodOrder(OrderAnnotation.class)
public class MftpClientTest 
{
	
	static final String HOST = "localhost";
	static final int PORT = 111;
	static final String USER = "hdm_qa";
	static final String PWD = "pass@457";
	static final String SOURCE = "%s/source_/";
	static final String OUTBOUND = "/outbound_/";
	
	static final String FILE_NAME = "file.txt";
	static final String FILE_CONTENT = "content";
	
	static final String TMP_DIR = System.getProperty("java.io.tmpdir");
	
	static FakeFtpServer fakeFtpServer;
	static MftpConfig mftpConfig;
	static MftpClient mftpClient;
	
	
	
	@BeforeAll
	static void beforeAll() throws IOException {
		loadFakeFtpServer();
		loadMftpConfig();
		loadMftpClient();
		loadSampleFile();
	}
	
	static void loadFakeFtpServer() {
		fakeFtpServer = new FakeFtpServer();
		fakeFtpServer.addUserAccount(new UserAccount(USER, PWD, OUTBOUND));
		
		FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add( new DirectoryEntry(OUTBOUND) );
        
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(PORT);
        fakeFtpServer.start();
        
	}
	
	static void loadMftpConfig() {
		mftpConfig = new MftpConfig(HOST, PORT, SOURCE, OUTBOUND);
		mftpConfig.setUser(USER);
		mftpConfig.setPass(PWD);
	}
	
	static void loadMftpClient() {
		mftpClient = new MftpClient(mftpConfig, FILE_NAME);
	}
	
	static void loadSampleFile() throws IOException {
		String input = FileUtil.fixSlashes( FileUtil.concat(mftpConfig.getSource(), FILE_NAME) );
		Files.write(Paths.get(input), FILE_CONTENT.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
	}
	
	
	
	@Test
	@Order(1)
	void shouldConnectToFtpServer() {
		assertTrue( mftpClient.open() );
	}
	
	
	
	@Test
	@Order(2)
	void localFileShouldExist() {
		assertTrue( mftpClient.localFileExists() );
	}
	
	@Test
	@Order(3)
	void localFileShouldNotExists() {
		mftpClient.setFileName("archivo.txt");
		assertFalse( mftpClient.localFileExists() );
		mftpClient.setFileName(FILE_NAME);
	}
	
	
	
	@Test
	@Order(4)
	void shouldUploadFile() {
		assertTrue( mftpClient.uploadFile() );
	}
	
	@Test
	@Order(5)
	void shouldNotUploadFile() {
		mftpClient.setFileName("archivo.txt");
		assertFalse( mftpClient.uploadFile() );
		mftpClient.setFileName(FILE_NAME);
	}
	
	
	
	@Test
	@Order(6)
	void shouldDisconnectFromFtpServer() {
		assertTrue( mftpClient.close() );
	}
	
	@Test
	@Order(7)
	void shouldNotConnectToFtpServer() {
		fakeFtpServer.stop();
		assertFalse( mftpClient.open() );
	}
	
	@Test
	@Order(8)
	void shouldNotDisconnectFromFtpServer() {
		assertFalse( mftpClient.close() );
	}

}
