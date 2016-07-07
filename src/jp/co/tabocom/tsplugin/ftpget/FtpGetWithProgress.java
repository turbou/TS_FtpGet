package jp.co.tabocom.tsplugin.ftpget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.tabocom.teratermstation.model.TargetNode;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

public class FtpGetWithProgress implements IRunnableWithProgress {

    public static final String PROXY_PORT = "21";
    // TIMEOUT定義
    private static final int CONNECT_TIMEOUT = 30000;
    private static final int LOGIN_TIMEOUT = 30000;
    private static final int FILE_GET_TIMEOUT = 30000;

    // 文字列定数定義
    private static final String WIN_SEP = "\\";
    private static final String ATMARK = "@";
    private static final String ERROR_FILE = "ftp_error.log";

    private Shell shell;
    private TargetNode[] nodes;
    private FtpInfo ftpInfo;

    public FtpGetWithProgress(Shell shell, TargetNode[] nodes, FtpInfo ftpInfo) {
        this.shell = shell;
        this.nodes = nodes;
        this.ftpInfo = ftpInfo;
    }

    @Override
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
            String timestamp = fmt.format(Calendar.getInstance().getTime());
            // チェックされているノードすべてで実行します。もちろん親ノード（サーバ種別を表すノード）は対象外です。
            monitor.beginTask("FTP一括取得中...", nodes.length);
            for (TargetNode target : nodes) {
                String svrType = target.getParent().getName();
                if (svrType == null) {
                    svrType = target.getCategory().getName();
                }
                String targetSvr = target.getName();
                monitor.subTask(svrType + " " + targetSvr);
                monitor.worked(1);
                FtpGet ftpGet = new FtpGet();
                if (ftpInfo.isAuth()) {
                    // getConnectの中のIPアドレスを取り出す。
                    Pattern p = Pattern.compile("^connect '(\\S+) .+$", Pattern.MULTILINE);
                    Matcher m = p.matcher(ftpInfo.getConnect());
                    if (m.find()) {
                        ftpGet.setAuthAddress(m.group(1));
                    } else {
                        MessageDialog.openError(shell, "一括取得", "ゲートウェイの接続先が取得できませんでした。");
                        return;
                    }
                }
                // あとは共通部分の設定
                ftpGet.setAuthUsr(ftpInfo.getAuthId());
                ftpGet.setAuthPwd(ftpInfo.getAuthPwd());
                ftpGet.setTargetAddress(target.getIpAddr());
                ftpGet.setLoginUsr(target.getLoginUsr());
                ftpGet.setLoginPwd(target.getLoginPwd());
                ftpGet.setHostName(target.getHostName());
                ftpGet.setTargetFileListStr(ftpInfo.getTargetPath());
                ftpGet.setAddHostname(ftpInfo.isAddHostname());
                StringBuilder saveDirPath = new StringBuilder(ftpInfo.getFtpGetDir());
                saveDirPath.append("\\");
                saveDirPath.append(timestamp);
                if (ftpInfo.isDivide()) {
                    saveDirPath.append("\\");
                    saveDirPath.append(target.getParent().getName());
                    saveDirPath.append("\\");
                    saveDirPath.append(target.getName());
                }
                ftpGet.setLocalSaveDir(saveDirPath.toString());

                // とりあえず各設定項目の内容チェック
                if (ftpGet.isError()) {
                    MessageDialog.openError(shell, "一括取得", "基本設定に不備があるため、処理を続行できません。");
                    return;
                }

                makeFtpDirectory(saveDirPath.toString());
                ftpGet(ftpGet);
                Thread.sleep(200);
            }
            monitor.done();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean makeFtpDirectory(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            return true;
        }
        if (!dir.mkdirs()) {
            MessageDialog.openError(shell, "エラー", "FTP保存ディレクトリを作成できませんでした。");
            return false;
        }
        return true;
    }

    private void ftpGet(FtpGet ftpGet) {
        FileOutputStream ostream = null;

        // FTPClientの生成
        FTPClient client = new FTPClient();

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
                String correctPath = getCorrectPath(filePath, ftpGet);
                String fileName = getCorrectFileName(correctPath, ftpGet);
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
                try {
                    ostream = new FileOutputStream(ftpGet.getLocalSaveDir() + WIN_SEP + ERROR_FILE);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace(new PrintStream(ostream));
        } finally {
            if (client.isConnected()) {
                try {
                    client.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ostream != null) {
                try {
                    ostream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getCorrectPath(String path, FtpGet ftpGet) {
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

    private String getCorrectFileName(String correctPath, FtpGet ftpGet) {
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
}
