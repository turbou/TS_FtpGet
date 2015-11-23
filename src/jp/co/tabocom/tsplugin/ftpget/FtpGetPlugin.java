package jp.co.tabocom.tsplugin.ftpget;

import java.util.List;

import jp.co.tabocom.teratermstation.model.TargetNode;
import jp.co.tabocom.teratermstation.plugin.TeratermStationPlugin;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationAction;
import jp.co.tabocom.teratermstation.ui.action.TeratermStationBulkAction;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

public class FtpGetPlugin implements TeratermStationPlugin {

    @Override
    public List<TeratermStationAction> getActions(TargetNode node, Shell shell, ISelectionProvider selectionProvider) {
        return null;
    }

    @Override
    public List<TeratermStationBulkAction> getBulkActions(List<TargetNode> nodeList, Shell shell) {
        return null;
    }

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
}
