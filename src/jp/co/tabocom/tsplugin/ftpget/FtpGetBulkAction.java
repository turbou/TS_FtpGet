package jp.co.tabocom.tsplugin.ftpget;

import java.lang.reflect.InvocationTargetException;

import jp.co.tabocom.teratermstation.Main;
import jp.co.tabocom.teratermstation.model.Tab;
import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.ui.EnvTabItem;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationAction;
import jp.co.tabocom.tsplugin.ftpget.preference.PreferenceConstants;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
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
        ProgressMonitorDialog progDialog = new ProgressMonitorDialog(shell);
        Main main = (Main) shell.getData("main");
        IPreferenceStore ps = main.getPreferenceStore();
        EnvTabItem tabItem = main.getCurrentTabItem();
        Tab tab = tabItem.getTab();

        FtpInfo ftpInfo = new FtpInfo();
        ftpInfo.setFtpGetDir(ps.getString(PreferenceConstants.FTPGET_DIR));
        ftpInfo.setAuth(tab.getAuth() != null);
        ftpInfo.setConnect(tab.getConnect());
        ftpInfo.setTargetPath(dialog.getValue());
        ftpInfo.setDivide(dialog.isDivide());
        ftpInfo.setAddHostname(dialog.isAddHostname());
        ftpInfo.setAuthId(tabItem.getAuthId());
        ftpInfo.setAuthPwd(tabItem.getAuthPwd());
        FtpGetWithProgress progress = new FtpGetWithProgress(shell, nodes, ftpInfo);
        try {
            progDialog.run(true, true, progress);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("キャンセルされました");
            e.printStackTrace();
        }
    }

    @Override
    public ToolTip getToolTip() {
        return null;
    }

}
