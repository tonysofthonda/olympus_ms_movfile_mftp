package com.honda.olympus.ms.movfile_mftp.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.honda.olympus.ms.movfile_mftp.util.FileUtil;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

@Slf4j
public class MftpClient {

	//private FTPClient ftp;
	private MftpConfig config;

	private String fileName;
	private String input;
	private String output;

	private Channel channel = null;
	private ChannelSftp channelSftp = null;
	private Session session = null;

	private boolean remoteDirFound;

	public MftpClient(MftpConfig config, String fileName) {
		this.config = config;
		this.fileName = fileName;
		this.input = FileUtil.fixSlashes(FileUtil.concat(config.getSource(), fileName));
		this.output = FileUtil.fixSlashes(FileUtil.concat(config.getOutbound(), fileName));
	}

	// setter method added for testing purposes only

	public void setFileName(String fileName) {
		this.fileName = fileName;
		this.input = FileUtil.fixSlashes(FileUtil.concat(config.getSource(), fileName));
		this.output = FileUtil.fixSlashes(FileUtil.concat(config.getOutbound(), fileName));
	}

	
	public boolean open() {
	
		try {
			/*
			String pass = config.getPass();
			JSch jsch = new JSch();
			this.session = jsch.getSession(config.getUser(),config.getHost(), config.getPort());
			this.session.setConfig("StrictHostKeyChecking", "no");
			this.session.setPassword(pass);
			this.session.connect();
			log.debug("Connection established.");
			log.debug("Creating SFTP Channel.");

			this.channel = this.session.openChannel("sftp");
			this.channel.connect();*/

			return true;
		} catch (Exception e4) {
			log.error("### Error found while connecting to ftp server", e4);
			return false;

		}
	}
	
	private SSHClient setupSshj() throws IOException {
	    SSHClient client = new SSHClient();
	    client.addHostKeyVerifier(new PromiscuousVerifier());
	    client.connect(config.getHost(),config.getPort());
	    client.authPassword(config.getUser(),config.getPass());
	    return client;
	}

	
	public void whenUploadFileUsingSshj_thenSuccess() throws IOException {
	    SSHClient sshClient = setupSshj();
	    SFTPClient sftpClient = sshClient.newSFTPClient();
	 
	    sftpClient.put(this.input,this.output);
	 
	    sftpClient.close();
	    sshClient.disconnect();
	    log.info("Monitor:: SFTP channel & session disconnected");
	}
	
	public boolean localFileExists() {
		
		try {
			Path path = Paths.get(this.input);
			if (!path.toFile().exists()) {
				log.error("### Can't find local file '{}'", fileName);
				return false;
			}
			return true;
		} catch (SecurityException ioe) {
			log.error("### Error found while searching local file '{}'", fileName, ioe);
			return false;
		}
		
		
	}

	public boolean uploadFile() {
		
		try {
			whenUploadFileUsingSshj_thenSuccess();	
			return true;
		}
		
		/*
		//findRemoteDir();
		Path path = Paths.get(this.input);
		
		try (InputStream inputStream = Files.newInputStream(path)){
			
			this.channelSftp = (ChannelSftp) this.channel;	
			this.channelSftp.put(this.input,this.output,null,1);
			//this.channelSftp.put( inputStream,this.output,0);
			//this.channelSftp.rename(this.input, fileName);
			
			this.channelSftp.exit();
			return true;
		}*/
		catch (IOException ioe) {
			ioe.printStackTrace();
			log.error("### Can't upload file '{}' due to: {}", fileName,ioe.getLocalizedMessage());
			return false;
		}
		
		
	}

	private void findRemoteDir() throws IOException {
	
		try {
			this.channelSftp = (ChannelSftp) this.channel;
			this.channelSftp.get(this.input,this.output);
			
		}
		catch (SftpException ioe) {
			log.error("### Can't find remote file '{}'", fileName);
			
		}
	}

	public boolean remoteDirFound() {
		return this.remoteDirFound;
	}

	public boolean deleteLocalFile() {
		return FileUtil.removeFile(input);
	}

	public boolean close() {
	
		try {
			/*
			this.channelSftp.disconnect();
			this.channel.disconnect();
			this.session.disconnect();
			*/
			return true;
		}
		catch (Exception ioe) {
			log.error("### Error found while closing connection to ftp server", ioe);
			return false;
		}
	}

}
