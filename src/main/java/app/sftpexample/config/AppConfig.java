package app.sftpexample.config;

import org.apache.sshd.client.SshClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  public SshClient sshClient() {
    return SshClient.setUpDefaultClient();
  }
}
