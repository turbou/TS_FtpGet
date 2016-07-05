package jp.co.tabocom.tsplugin.ftpget.preference;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FtpGetPreferencePage extends PreferencePage {

    private Text ftpGetDirTxt;

    public FtpGetPreferencePage() {
        super("FTP一括取得設定");
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        IPreferenceStore preferenceStore = getPreferenceStore();

        Group dirGrp = new Group(composite, SWT.NONE);
        dirGrp.setLayout(new GridLayout(3, false));
        GridData dirGrpGrDt = new GridData(GridData.FILL_HORIZONTAL);
        dirGrpGrDt.horizontalSpan = 3;
        dirGrp.setLayoutData(dirGrpGrDt);
        dirGrp.setText("作業領域");

        // ========== FTP保存先ディレクトリの場所 ========== //
        new Label(dirGrp, SWT.LEFT).setText("FTP保存ディレクトリ：");
        ftpGetDirTxt = new Text(dirGrp, SWT.BORDER);
        ftpGetDirTxt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ftpGetDirTxt.setText(preferenceStore.getString(PreferenceConstants.FTPGET_DIR));
        Button ftpDirBtn = new Button(dirGrp, SWT.NULL);
        ftpDirBtn.setText("参照");
        ftpDirBtn.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                String dir = dirDialogOpen("FTP取得ファイルの保存先ディレクトリを指定してください。", ftpGetDirTxt.getText());
                if (dir != null) {
                    ftpGetDirTxt.setText(dir);
                }
            }
        });

        noDefaultAndApplyButton();
        return composite;
    }

    @Override
    public boolean performOk() {
        IPreferenceStore preferenceStore = getPreferenceStore();
        if (preferenceStore == null) {
            return true;
        }

        if (this.ftpGetDirTxt != null) {
            preferenceStore.setValue(PreferenceConstants.FTPGET_DIR, this.ftpGetDirTxt.getText());
        }

        return true;
    }

    private String dirDialogOpen(String msg, String currentPath) {
        DirectoryDialog dialog = new DirectoryDialog(getShell());
        dialog.setText(msg);
        dialog.setFilterPath(currentPath.isEmpty() ? "C:\\" : currentPath);
        String dir = dialog.open();
        return dir;
    }

}
