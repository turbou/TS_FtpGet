package jp.co.tabocom.tsplugin.ftpget;

import java.util.Arrays;
import java.util.List;

import jp.co.tabocom.teratermstation.model.TargetNode;

public class FtpGet {

    private String authAddress;
    private String authUsr;
    private String authPwd;
    private String targetFileListStr;
    private List<String> targetFileList;
    private String localSaveDir;
    private boolean isAddHostname;
    private TargetNode node;

    public String getAuthAddress() {
        return authAddress;
    }

    public void setAuthAddress(String authAddress) {
        this.authAddress = authAddress;
    }

    public String getAuthUsr() {
        return authUsr;
    }

    public void setAuthUsr(String authUsr) {
        this.authUsr = authUsr;
    }

    public String getAuthPwd() {
        return authPwd;
    }

    public void setAuthPwd(String authPwd) {
        this.authPwd = authPwd;
    }

    public void setTargetFileListStr(String targetFileListStr) {
        this.targetFileListStr = targetFileListStr;
        this.targetFileList = Arrays.asList(this.targetFileListStr.split("\r\n"));
    }

    public List<String> getTargetFileList() {
        return targetFileList;
    }

    public String getLocalSaveDir() {
        return localSaveDir;
    }

    public void setLocalSaveDir(String localSaveDir) {
        this.localSaveDir = localSaveDir;
    }

    public boolean isAddHostname() {
        return isAddHostname;
    }

    public void setAddHostname(boolean isAddHostname) {
        this.isAddHostname = isAddHostname;
    }

    public TargetNode getNode() {
        return node;
    }

    public void setNode(TargetNode node) {
        this.node = node;
    }

    public boolean isError() {
        boolean flg = false;
        if (authAddress.isEmpty())
            flg |= true;
        if (node.getIpAddr().isEmpty())
            flg |= true;
        if (authUsr.isEmpty())
            flg |= true;
        if (authPwd.isEmpty())
            flg |= true;
        if (node.getLoginUsr().isEmpty())
            flg |= true;
        if (node.getLoginPwd().isEmpty())
            flg |= true;
        return flg;
    }

}
