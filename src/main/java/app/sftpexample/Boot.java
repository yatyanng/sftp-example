package app.sftpexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;
import app.sftpexample.service.SftpService;

@SpringBootApplication
public class Boot {

  private static final Logger log = LoggerFactory.getLogger(Boot.class);

  @Autowired
  private BuildProperties buildProperties;
  
  @Autowired
  private SftpService sftpService;

  public static void main(String[] args) throws Exception {
    String configDirectory = "conf";
    if (args.length > 0) {
      configDirectory = args[0];
    }
    log.info("config directory: {}", configDirectory);
    System.setProperty("spring.config.location", configDirectory + "/springboot.yml");
    System.setProperty("logging.config", configDirectory + "/logback.xml");
    System.setProperty("javamail.config", configDirectory + "/javamail.properties");

    ApplicationContext ac = SpringApplication.run(Boot.class, args);
    int rc = ac.getBean(Boot.class).run(configDirectory);
    System.exit(rc);
  }

  public int run(String configDirectory) throws Exception {
    log.info("{} {} is started with configDirectory: {}", buildProperties.getArtifact(),
        buildProperties.getVersion(), configDirectory);
    int rc = sftpService.run();
    log.info("{} {} has ended, rc={}", buildProperties.getArtifact(), buildProperties.getVersion(),
        rc);
    return rc;
  }

}
