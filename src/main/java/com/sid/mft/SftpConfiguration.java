package com.sid.mft;

import com.jcraft.jsch.ChannelSftp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.dsl.Sftp;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

import java.io.File;
import java.util.Objects;

@Configuration
@EnableIntegration
public class SftpConfiguration {

    @Autowired
    private Environment env;


    @Bean
    public SessionFactory<ChannelSftp.LsEntry> sftpSessionFactory() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
        factory.setHost("localhost");
        factory.setPort(2222);
        factory.setUser("sid");
        factory.setPassword("sid@123");
        factory.setAllowUnknownKeys(true);
        return new CachingSessionFactory<>(factory);
    }
    @Bean
    public IntegrationFlow sftpInboundFlow() {
        return IntegrationFlows
                .from(Sftp.inboundAdapter(sftpSessionFactory())
                                .preserveTimestamp(true)
                                .remoteDirectory(env.getProperty("spring.integration.sftp.remote.directory"))
                                .localDirectory(new File(System.getProperty("java.io.tmpdir"))), // Use a temporary directory
                        e -> e.id("sftpInboundAdapter"))
                .handle(System.out::println) // Modify the handling logic as per your requirements
                .get();
    }
}

