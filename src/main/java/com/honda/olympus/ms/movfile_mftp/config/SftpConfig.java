package com.honda.olympus.ms.movfile_mftp.config;

import com.jcraft.jsch.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

//@Configuration
public class SftpConfig {

    @Value("${sftp.host}")
    private String sftpHost;

    @Value("${sftp.port}")
    private int sftpPort;

    @Value("${sftp.user}")
    private String sftpUser;

    @Value("${sftp.password}")
    private String sftpPassword;

//    @Bean
    public Session sftpSession() throws JSchException {

        JSch jSch = new JSch();

        Session session = jSch.getSession(sftpUser, sftpHost, sftpPort);
        session.setPassword(sftpPassword);
        // Configuración de la autenticación
//        session.setUserInfo(new SftpUserInfo(sftpPassword));
        Properties config = new Properties();
        config.put("kex","diffie-hellman-group-exchange-sha1,diffie-hellman-group14-sha1,diffie-hellman-group1-sha1");
        config.put("StrictHostKeyChecking", "no");

        session.setConfig(config);
        session.connect();
        return session;
    }

//    @Bean
    public ChannelSftp channelSftp() throws JSchException {
        Session session = sftpSession();
        Channel channelSftp = session.openChannel("sftp");
        channelSftp.connect();
        return (ChannelSftp) channelSftp;
    }
}
