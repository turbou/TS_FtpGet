package jp.co.tabocom.tsplugin.ftpget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.co.tabocom.teratermstation.TeratermStationShell;
import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.plugin.TeratermStationPlugin;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationAction;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationContextMenu;
import jp.co.tabocom.tsplugin.ftpget.preference.FtpGetPreferencePage;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;

public class FtpGetPlugin implements TeratermStationPlugin {

    @Override
    public PreferencePage getPreferencePage() {
        return new FtpGetPreferencePage();
    }

    @Override
    public void initialize() throws Exception {
    }

    @Override
    public void teminate(PreferenceStore preferenceStore) throws Exception {
    }

    @Override
    public List<TeratermStationContextMenu> getActions(TargetNode[] nodes, TeratermStationShell shell) {
        return null;
    }

    @Override
    public List<TeratermStationAction> getBulkActions(TargetNode[] nodes, TeratermStationShell shell) {
        return new ArrayList<TeratermStationAction>(Arrays.asList(new FtpGetBulkAction(nodes, null, shell)));
    }

    @Override
    public List<TeratermStationContextMenu> getDnDActions(TargetNode[] nodes, Object value, TeratermStationShell shell) {
        return null;
    }
}
