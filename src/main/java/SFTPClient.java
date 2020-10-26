import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import org.slf4j.Logger;
import pool.StackSessionPool;
import vo.SFTPResult;
import vo.enums.ActionType;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * sftp client
 *
 * @author John.Mao
 * @version 1.0
 * @link https://epaul.github.io/jsch-documentation/javadoc/com/jcraft/jsch/ChannelSftp.html
 */
@SuppressWarnings("unused")
public class SFTPClient {
    private static final Logger log = getLogger(SFTPClient.class);
    private static SFTPClient INSTANCE;

    public SFTPClient(ChannelSftp channelSftp) {
        try {
            if (channelSftp == null) {
                throw new JSchException("ChannelSftp is required, fail to connect.");
            } else {
                channelSftp.connect();
            }
        } catch (JSchException e) {
            log.error(e.getMessage());
        }
    }

    public SFTPResult isConnected(ChannelSftp channelSftp) {
        SFTPResult result = new SFTPResult(ActionType.isConnected, null, null);
        if (channelSftp == null) {
            result.setErrMsg(new NullPointerException("ChannelSftp is null, it required for connection testing.").getMessage());
        } else {
            result.setSuccess(channelSftp.isConnected());
        }
        return result;
    }

    //TODO disconnect
    //TODO ls
    //TODO get file
    //TODO get dir
    //TODO put file
    //TODO put dir
    //TODO chown
    //TODO chmod
    //TODO mkdir
    //TODO mkdir -p
    //TODO rename
    //TODO rm file
    //TODO rm -r dir
    //TODO setMtime
    //TODO setStat
    //TODO symlink

}
