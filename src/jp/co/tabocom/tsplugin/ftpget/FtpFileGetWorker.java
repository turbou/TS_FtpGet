package jp.co.tabocom.tsplugin.ftpget;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FtpFileGetWorker extends SwingWorker<Boolean, String> {

    public static final String PROXY_PORT = "21";

    FTPClient client;

    // TIMEOUT定義
    private static final int CONNECT_TIMEOUT = 30000;
    private static final int LOGIN_TIMEOUT = 30000;
    private static final int FILE_GET_TIMEOUT = 30000;

    // 文字列定数定義
    private static final String WIN_SEP = "\\";
    private static final String ATMARK = "@";
    private static final String ERROR_FILE = "ftp_error.log";

    private FtpGet ftpGet;

    public FtpFileGetWorker(FtpGet ftpGet) {
        this.ftpGet = ftpGet;
    }

    @Override
    protected Boolean doInBackground() throws Exception {

        FileOutputStream ostream = null;

        // FTPClientの生成
        client = new FTPClient();

        try {
            // サーバに接続
            client.setDefaultTimeout(CONNECT_TIMEOUT);
            client.connect(ftpGet.getAuthAddress(), Integer.parseInt(PROXY_PORT));
            int reply = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                throw new Exception("Proxy接続エラー：" + ftpGet.getAuthAddress());
            }

            client.setSoTimeout(LOGIN_TIMEOUT);
            // ログイン
            if (!client.login(ftpGet.getAuthUsr(), ftpGet.getAuthPwd())) {
                throw new Exception("認証エラー：" + ftpGet.getAuthUsr() + "/" + ftpGet.getAuthPwd());
            }

            if (!client.login(ftpGet.getLoginUsr() + ATMARK + ftpGet.getTargetAddress(), ftpGet.getLoginPwd())) {
                throw new Exception("サーバログインエラー：" + ftpGet.getLoginUsr() + ATMARK + ftpGet.getTargetAddress() + "/" + ftpGet.getLoginPwd());
            }

            // Passiveはとりあえずコメント.本番でも開発でも問題がないようなので.
            // if (ftpGet.getAuthType() == AuthTypeEnum.PROXY) {
            // client.enterLocalPassiveMode();
            // }

            // 転送モードの設定
            client.setFileType(FTP.BINARY_FILE_TYPE);

            client.setSoTimeout(FILE_GET_TIMEOUT);

            // ファイル受信
            for (String filePath : ftpGet.getTargetFileList()) {
                String correctPath = getCorrectPath(filePath);
                String fileName = getCorrectFileName(correctPath);
                ostream = new FileOutputStream(ftpGet.getLocalSaveDir() + WIN_SEP + fileName);
                client.retrieveFile(correctPath, ostream);
                ostream.close();
                ostream = null;
                if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
                    throw new Exception(String.format("ファイル取得失敗(errorcode=%d)：%s", client.getReplyCode(), filePath));
                }
            }
        } catch (Exception e) {
            if (ostream == null) {
                ostream = new FileOutputStream(ftpGet.getLocalSaveDir() + WIN_SEP + ERROR_FILE);
            }
            e.printStackTrace(new PrintStream(ostream));
        } finally {
            if (client.isConnected()) {
                client.disconnect();
            }
            if (ostream != null) {
                try {
                    ostream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return Boolean.TRUE;
    }

    private String getCorrectPath(String path) {
        if (!path.contains("$")) {
            return path;
        }
        String repStr = new String(path);
        String user = ftpGet.getLoginUsr(); // ログインユーザーを取得(例：aplusr01)
        String men = user.substring(user.length() - 2); // ユーザー名から面情報を取得(例：01)
        // まずは先頭に指定されるであろう環境変数のリプレイス
        if (repStr.startsWith("$APL_DIR")) {
            repStr = repStr.replaceAll(Pattern.quote("$APL_DIR"), String.format("/APL/group%s/local", men));
        } else if (repStr.startsWith("$APL_PKG01_DIR")) {
            repStr = repStr.replaceAll(Pattern.quote("$APL_PKG01_DIR"), String.format("/APL/group%s/pkg01", men));
        } else if (repStr.startsWith("$WEBADM")) {
            repStr = repStr.replaceAll(Pattern.quote("$WEBADM"), String.format("/webadm%s", men));
        }

        // 次にパスの中のどこかで指定されるであろう環境変数のリプレイス
        if (repStr.contains("$HOSTNAME")) {
            repStr = repStr.replaceAll(Pattern.quote("$HOSTNAME"), ftpGet.getHostName());
        }
        return repStr;
    }

    private String getCorrectFileName(String correctPath) {
        String fileName = correctPath.substring(correctPath.lastIndexOf("/"), correctPath.length());
        if (ftpGet.isAddHostname()) {
            if (fileName.contains(".")) {
                String suffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
                fileName = fileName.replaceAll(suffix, "_" + ftpGet.getHostName() + suffix);
            } else {
                fileName = fileName + "_" + ftpGet.getHostName();
            }
        }
        return fileName;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String str : chunks) {
            firePropertyChange("console", null, str);
        }
    }

}
