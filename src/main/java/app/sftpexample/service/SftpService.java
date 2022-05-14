package app.sftpexample.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.apache.sshd.sftp.client.SftpVersionSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import app.sftpexample.Constants;

@Service
public class SftpService {

  private static final Logger log = LoggerFactory.getLogger(SftpService.class);

  @Autowired
  private SshClient sshClient;

  @Value(Constants.CFG_SOURCE_FOLDER)
  private String sourceFolderName;

  @Value(Constants.CFG_SFTP_USER)
  private String user;

  @Value(Constants.CFG_SFTP_PASSWORD)
  private String password;

  @Value(Constants.CFG_SFTP_HOST)
  private String host;

  @Value(Constants.CFG_SFTP_PORT)
  private int port;

  @Value(Constants.CFG_SFTP_TIMEOUT)
  private int timeout;

  @PostConstruct
  private void init() {
    sshClient.start();
  }

  public int run() {
    MutableInt rc = new MutableInt(0);
    try (ClientSession session = sshClient.connect(user, host, port).verify(timeout).getSession()) {
      session.addPasswordIdentity(password);
      session.auth().verify(timeout);
      
      SftpClientFactory factory = SftpClientFactory.instance();
      try (SftpClient sftp = factory.createSftpClient(session, SftpVersionSelector.MINIMUM)) {
        
        File sourceFolder = new File(sourceFolderName);
        List.of(sourceFolder.listFiles()).stream().filter(File::isFile).forEach(file -> {
          
          try (OutputStream out = sftp.write(file.getName())) {
            byte[] content = FileUtils.readFileToByteArray(file);
            out.write(content);
            out.flush();
          } catch (IOException e) {
            log.error("[run] An error has occurred while writing {}!", file.getName(), e);
            rc.setValue(-1);
          }
          
        });
      } catch (Exception e) {
        log.error("[run] An error has occurred while using sftp client!", e);
        rc.setValue(-1);
      }
    } catch (Exception e) {
      log.error("[run] An error has occurred while using ssh client session!", e);
      rc.setValue(-1);
    }
    return rc.getValue();
  }
}
