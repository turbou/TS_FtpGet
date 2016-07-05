package jp.co.tabocom.tsplugin.ftpget;

import java.util.List;

import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.plugin.TeratermStationPlugin;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationAction;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationContextMenu;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.widgets.Shell;

public class FtpGetPlugin implements TeratermStationPlugin {

    @Override
    public PreferencePage getPreferencePage() {
        return null;
    }

    @Override
    public void initialize() throws Exception {
    }

    @Override
    public void teminate(PreferenceStore preferenceStore) throws Exception {
    }

    @Override
    public List<TeratermStationContextMenu> getActions(TargetNode[] nodes, Shell shell) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public List<TeratermStationAction> getBulkActions(TargetNode[] nodes, Shell shell) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public List<TeratermStationContextMenu> getDnDActions(TargetNode[] nodes, Object value, Shell shell) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }
}
