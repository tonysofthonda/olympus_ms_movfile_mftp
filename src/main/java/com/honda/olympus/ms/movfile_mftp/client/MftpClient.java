package com.honda.olympus.ms.movfile_mftp.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.honda.olympus.ms.movfile_mftp.util.FileUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MftpClient 
{ 
	
	private FTPClient ftp;
	private MftpConfig config;
	
	private String fileName;
	private String input;  
	private String output;
	
	
	public MftpClient(MftpConfig config, String fileName) {
		this.config = config;
		this.fileName = fileName;
		this.input = FileUtil.fixSlashes( FileUtil.concat(config.getSource(), fileName) );
		this.output = FileUtil.fixSlashes( FileUtil.concat(config.getOutbound(), fileName) );
	}
	
	
	// setter method added for testing purposes only
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
		this.input = FileUtil.fixSlashes( FileUtil.concat(config.getSource(), fileName) );
		this.output = FileUtil.fixSlashes( FileUtil.concat(config.getOutbound(), fileName) );
	}
	
	
	public boolean open() {
		try {
			ftp = new FTPClient();
	        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
	        
	        ftp.connect(config.getHost(), config.getPort());
	        ftp.login(config.getUser(), config.getPass());
	        
	        ftp.enterLocalPassiveMode();   // controls local passive mode 
	        
	        int reply = ftp.getReplyCode();
	        if (!FTPReply.isPositiveCompletion(reply)) 
	        {
	        	ftp.disconnect();
	        	log.error("### Can't connect to the ftp server");
	        	return false;
	        }
	        return true;
		}
		catch (IOException ioe) {
			log.error("### Error found while connecting to ftp server", ioe);
			return false;
		}
    }
	
	
	public boolean localFileExists() {
		try {
			Path path = Paths.get(input);
			if (!path.toFile().exists()) {
				log.error("### Can't find local file '{}'", fileName);
				return false;
			}
			return true;
		}
		catch (SecurityException ioe) {
			log.error("### Error found while searching local file '{}'", fileName, ioe);
			return false;
		}
	}
	
	
	public boolean uploadFile() {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(input);
			ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			
		    if (!ftp.storeFile(output, fis)) 
		    {
		    	log.error("### Can't upload file '{}'", fileName);
		    	fis.close();
				return false;
		    }
			fis.close();
		    return true;
		}
		catch (IOException ioe) {
			log.error("### Error found while uploading file '{}'", fileName, ioe);
			if (fis != null) {
				try { fis.close(); } catch (IOException e) { }
			}
			return false;
		}
	}
	
	
	public boolean remoteFileExists() {
		try {
			FTPFile[] list = ftp.listFiles(output);
			
			if (list.length == 0) {
				log.error("### Can't find remote file '{}'", fileName);
				return false;
			}
			return true;
		}
		catch (IOException ioe) {
			log.error("### Error found while searching remote file '{}'", fileName, ioe);
			return false;
		}
	}
	
	
	public boolean close() {
		try {
			ftp.logout();
			ftp.disconnect();
			return true;
		}
		catch (IOException ioe) {
			log.error("### Error found while closing connection to ftp server", ioe);
			return false;
		}
    }
	
}
