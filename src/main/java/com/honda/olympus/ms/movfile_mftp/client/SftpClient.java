package com.honda.olympus.ms.movfile_mftp.client;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Vector;

//@Component
public class SftpClient {

    private final ChannelSftp channelSftp;

//    @Autowired
    public SftpClient(ChannelSftp channelSftp) {
        this.channelSftp = channelSftp;
    }

    public void listFiles(String remoteDirectory) throws SftpException {
        @SuppressWarnings("unchecked")
        Vector<ChannelSftp.LsEntry> files = channelSftp.ls(remoteDirectory);
        for (ChannelSftp.LsEntry file : files) {
            if (!file.getFilename().equals(".") && !file.getFilename().equals("..")) {
                System.out.println(file.getFilename());
            }
        }
    }

    public void uploadFile(String localFilePath, String remoteDirectory) throws FileNotFoundException, SftpException {
        File localFile = new File(localFilePath);
        FileInputStream inputStream = new FileInputStream(localFile);
        channelSftp.put(inputStream, remoteDirectory + "/" + localFile.getName());
    }

}