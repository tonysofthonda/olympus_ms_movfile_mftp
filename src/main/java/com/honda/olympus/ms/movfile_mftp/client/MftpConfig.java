package com.honda.olympus.ms.movfile_mftp.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.honda.olympus.ms.movfile_mftp.util.FileUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j
@Component
public class MftpConfig {

    private static final String BASE_DIR = System.getProperty("java.io.tmpdir");

    private String host;
    private int port;
    private String source;
    private String outbound;

    @Value("${sftp.user}")
    private String user;

    @Value("${sftp.password}")
    private String pass;

    public MftpConfig(
            @Value("${sftp.host}") String host,
            @Value("${sftp.port}") int port,
            @Value("${mftp.source}") String source,
            @Value("${mftp.outbound}") String outbound) {

        this.host = host;
        this.port = port;
        this.source = FileUtil.fixSlashes(String.format(source, BASE_DIR));
        this.outbound = FileUtil.fixSlashes(String.format(outbound, BASE_DIR));

        log.info("# mftp host: {}", host);
        log.info("# mftp port: {}", port);
        log.info("# mftp source: {}", this.source);
        log.info("# mftp outbound: {}", this.outbound);

        FileUtil.createDir(this.source);
    }

}
