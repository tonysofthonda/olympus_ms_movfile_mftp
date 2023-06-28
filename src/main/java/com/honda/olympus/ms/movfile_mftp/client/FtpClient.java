package com.honda.olympus.ms.movfile_mftp.client;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Vector;
import java.util.regex.Pattern;

@Slf4j
public class FtpClient {

    private String server;
    private Integer port;
    private String user;
    private String password;
    private String workDir;
    private Channel channel = null;
    private ChannelSftp channelSftp = null;

    public FtpClient(String server, Integer port, String user, String password, String workDir) {
        super();
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
        this.workDir = workDir;
    }

    public void connect() throws Exception {

        try {
            String pass = this.password;
            JSch jsch = new JSch();
            Session session = jsch.getSession(this.user, this.server, this.port);
            session.setConfig("StrictHostKeyChecking", "no");

            session.setPassword(pass);
            session.connect();
            log.debug("Connection established.");
            log.debug("Creating SFTP Channel.");

            channel = session.openChannel("sftp");
            channel.connect();

        } catch (JSchException e4) {
            log.info("Exception ocurred due to: {} ", e4.getLocalizedMessage());

            throw new Exception(e4.getLocalizedMessage());

        }
    }

    public boolean listFiles() throws Exception {
        try {
            ArrayList<LsEntry> files;

            this.channelSftp = (ChannelSftp) channel;
            this.channelSftp.cd(this.workDir);
            Vector<LsEntry> filelist = channelSftp.ls(this.workDir);

            files = new ArrayList<LsEntry>(filelist);

            if (filelist.size() != 0) {

                files.forEach(f -> log.debug("File name: {}, Is file: {}", f.getFilename(), Pattern.matches("^[\\w,\\s-]+\\.[A-Za-z]{3}$", f.getFilename())));
                return Boolean.TRUE;

            } else {
                return Boolean.FALSE;
            }
        } catch (SftpException e4) {
            log.info("Exception ocurred due to: {} ", e4.getLocalizedMessage());
            throw new Exception(e4.getLocalizedMessage());
        }
    }

    public LsEntry listFirstFile(String serviceName) throws Exception {

        try {
            ArrayList<LsEntry> files;

            this.channelSftp = (ChannelSftp) channel;
            this.channelSftp.cd(this.workDir);
            Vector<LsEntry> filelist = channelSftp.ls(this.workDir);

            files = new ArrayList<LsEntry>(filelist);

            Optional<LsEntry> ftpFile = files.stream().filter(f -> Pattern.matches("^[\\w,\\s-]+\\.[A-Za-z]{3}$", f.getFilename())).findFirst();

            if (ftpFile.isPresent()) {
                return ftpFile.get();
            } else {

                return null;
            }

        } catch (SftpException e4) {
            log.info("Exception ocurred due to: {} ", e4.getLocalizedMessage());
            throw new Exception(e4.getLocalizedMessage());

        }

    }

    public void close() throws IOException {
        this.channel.disconnect();
    }
}
