package jp.co.tabocom.tsplugin.ftpget;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import jp.co.tabocom.teratermstation.Main;
import jp.co.tabocom.teratermstation.model.Tab;
import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.ui.EnvTabItem;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationAction;
import jp.co.tabocom.tsplugin.ftpget.preference.PreferenceConstants;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;

public class FtpGetBulkAction extends TeratermStationAction {

    public FtpGetBulkAction(TargetNode[] nodes, Object value, Shell shell) {
        super("FTP一括取得...", null, nodes, value, shell);
    }

    @Override
    public void run() {
        FtpGetDialog dialog = new FtpGetDialog(shell);
        int result = dialog.open();
        if (IDialogConstants.OK_ID != result) {
            return;
        }
        bulkFtpGet(dialog.getValue(), dialog.isDivide(), dialog.isAddHostname());
    }

    private void bulkFtpGet(String targetPath, boolean isDivide, boolean isAddHostname) {
        Main main = (Main) shell.getData("main");
        EnvTabItem tabItem = main.getCurrentTabItem();
        Tab tab = tabItem.getTab();
        // 念のため確認ダイアログを出す。
        // if (!MessageDialog.openConfirm(getParent().getShell(), "一括取得",
        // "一括でFTP取得します。よろしいですか？")) {
        // return;
        // }
        // this.progressShow(nodes.length);
        IPreferenceStore ps = main.getPreferenceStore();
        // int interval = ps.getInt(PreferenceConstants.BULK_INTERVAL);
        int interval = 200;
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
            String timestamp = fmt.format(Calendar.getInstance().getTime());
            // チェックされているノードすべてで実行します。もちろん親ノード（サーバ種別を表すノード）は対象外です。
            for (TargetNode target : nodes) {
                FtpGet ftpGet = new FtpGet();
                if (tab.getAuth() != null) {
                    // getConnectの中のIPアドレスを取り出す。
                    ftpGet.setAuthAddress(tab.getConnect());
                }
                // あとは共通部分の設定
                ftpGet.setAuthUsr(tabItem.getAuthId());
                ftpGet.setAuthPwd(tabItem.getAuthPwd());
                ftpGet.setTargetAddress(target.getIpAddr());
                ftpGet.setLoginUsr(target.getLoginUsr());
                ftpGet.setLoginPwd(target.getLoginPwd());
                ftpGet.setHostName(target.getHostName());
                ftpGet.setTargetFileListStr(targetPath);
                ftpGet.setAddHostname(isAddHostname);
                StringBuilder saveDirPath = new StringBuilder(ps.getString(PreferenceConstants.FTPGET_DIR));
                saveDirPath.append("\\");
                saveDirPath.append(timestamp);
                if (isDivide) {
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

                final FtpFileGetWorker worker = new FtpFileGetWorker(ftpGet);
                worker.addPropertyChangeListener(new SWPropertyChangeListener(shell) {
                    @Override
                    public void started() throws Exception {
                        // これはworkerがexecuteされた時に呼び出されます。
                    }

                    @Override
                    public void done() throws Exception {
                        // これはworkerの処理が完了した時に呼び出されます。
                        // if (worker.get()) { //
                        // worker.get()で結果を確認することができます。（doInBackgroundの戻り値）
                        progressCountUp();
                    }
                });
                // リスナーを登録してから実行
                worker.execute();

                Thread.sleep(interval); // スリープしなくても問題はないけど、あまりにも連続でターミナルが開くのもあれなので。
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void progressCountUp() {
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

    @Override
    public ToolTip getToolTip() {
        return null;
    }

}
