package jp.co.tabocom.tsplugin.ftpget;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class FtpGetDialog extends Dialog {

    private StyledText widget;
    private Button serverDivideCheck;
    private Button hostnameAddCheck;
    private String value;
    private boolean isDivide;
    private boolean isAddHostname;

    public FtpGetDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setLayout(new GridLayout(2, false));
        // ========== テキストエリア ==========
        this.widget = new StyledText(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData widgetGrDt = new GridData(GridData.FILL_BOTH);
        widgetGrDt.horizontalSpan = 2;
        this.widget.setLayoutData(widgetGrDt);
        this.widget.setMargins(5, 5, 10, 5);
        this.widget.setEditable(true);
        this.widget.setToolTipText("FTPで取得する対象のファイルパスを指定してください。\n複数指定する場合は改行して指定してください。");
        this.widget.addTraverseListener(new TraverseListener() {
            @Override
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_TAB_NEXT || e.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
                    e.doit = true;
                }
            }
        });
        // ========== 説明文 ==========
        Label descLabel = new Label(composite, SWT.NONE);
        GridData descLblGrDt = new GridData(GridData.FILL_HORIZONTAL);
        descLblGrDt.verticalSpan = 2;
        descLabel.setLayoutData(descLblGrDt);
        descLabel.setText("- 変数も使用できます（TeratermStationの接続定義のvariableで設定しておく必要があります）"
                + "\n  HOSTNAMEは接続定義のhostnameの値が使用されます。"
                + "\n- ホーム直下のファイルを取得する場合は./(ドットスラ)を付けて相対パスで指定してください。"
                + "\n- ファイル名にホスト名が含まれる場合は右のチェックを外すことで１つのフォルダに取得することができます。");
        // ========== サーバごとフォルダを分けるかチェックボックス ==========
        this.serverDivideCheck = new Button(composite, SWT.CHECK);
        GridData serverDivideChkDt = new GridData();
        serverDivideChkDt.horizontalAlignment = SWT.LEFT;
        this.serverDivideCheck.setLayoutData(serverDivideChkDt);
        this.serverDivideCheck.setText("サーバごとにフォルダを分けて保存");
        this.serverDivideCheck.setToolTipText("同一ファイル名を一括DLする場合にフォルダをサーバごとに分けて取得します。");
        this.serverDivideCheck.setSelection(true);
        // ========== ファイル名にホスト名を自動付加するかチェックボックス ==========
        this.hostnameAddCheck = new Button(composite, SWT.CHECK);
        GridData hostnameAddChkDt = new GridData();
        hostnameAddChkDt.horizontalAlignment = SWT.LEFT;
        this.hostnameAddCheck.setLayoutData(hostnameAddChkDt);
        this.hostnameAddCheck.setText("ファイル名にホスト名を付加");
        this.hostnameAddCheck.setToolTipText("同一ファイル名を一括DLする場合にファイル名にホスト名を付加して識別させます。\n１つのフォルダにまとめて取得したい場合に使えます。");
        this.hostnameAddCheck.setSelection(false);
        return composite;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(720, 420);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("FTPで取得する対象ファイルを指定してください。");
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID, "キャンセル", false);
        createButton(parent, IDialogConstants.OK_ID, "実行", true);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (IDialogConstants.OK_ID == buttonId) {
            // OKボタンが押された場合はここで簡単な入力チェックを行う。
            if (this.widget.getText().isEmpty()) {
                MessageDialog.openError(getShell(), "入力エラー", "取得対象ファイルが指定されていません。");
                return;
            }
            if (!this.widget.getText().contains("/")) {
                MessageDialog.openError(getShell(), "入力エラー", "ファイルパスの指定方法が正しくありません。\nフルパスまたはホーム配下の場合は./を付けて相対パス指定としてください。");
                return;
            }
        }
        this.value = this.widget.getText();
        this.isDivide = this.serverDivideCheck.getSelection();
        this.isAddHostname = this.hostnameAddCheck.getSelection();
        super.buttonPressed(buttonId);
    }

    public String getValue() {
        return value;
    }

    public boolean isDivide() {
        return isDivide;
    }

    public boolean isAddHostname() {
        return isAddHostname;
    }

}
