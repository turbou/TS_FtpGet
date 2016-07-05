package jp.co.tabocom.tsplugin.ftpget;

import java.util.Arrays;
import java.util.List;

public class FtpGet {

    private String authAddress;
    private String targetAddress;
    private String authUsr;
    private String authPwd;
    private String loginUsr;
    private String loginPwd;
    private String hostName;
    private String targetFileListStr;
    private List<String> targetFileList;
    private String localSaveDir;
    private boolean isAddHostname;

    public String getAuthAddress() {
        return authAddress;
    }

    public void setAuthAddress(String authAddress) {
        this.authAddress = authAddress;
    }

    public String getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
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

    public String getLoginUsr() {
        return loginUsr;
    }

    public void setLoginUsr(String loginUsr) {
        this.loginUsr = loginUsr;
    }

    public String getLoginPwd() {
        return loginPwd;
    }

    public void setLoginPwd(String loginPwd) {
        this.loginPwd = loginPwd;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
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

    public boolean isError() {
        boolean flg = false;
        if (authAddress.isEmpty())
            flg |= true;
        if (targetAddress.isEmpty())
            flg |= true;
        if (authUsr.isEmpty())
            flg |= true;
        if (authPwd.isEmpty())
            flg |= true;
        if (loginUsr.isEmpty())
            flg |= true;
        if (loginPwd.isEmpty())
            flg |= true;
        return flg;
    }

}
