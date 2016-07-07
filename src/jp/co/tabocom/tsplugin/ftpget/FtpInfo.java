package jp.co.tabocom.tsplugin.ftpget;

public class FtpInfo {
    private String ftpGetDir;
    private boolean isAuth;
    private String connect;

    private String targetPath;
    private boolean isDivide;
    private boolean isAddHostname;

    private String authId;
    private String authPwd;

    public String getFtpGetDir() {
        return ftpGetDir;
    }

    public void setFtpGetDir(String ftpGetDir) {
        this.ftpGetDir = ftpGetDir;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean isAuth) {
        this.isAuth = isAuth;
    }

    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public boolean isDivide() {
        return isDivide;
    }

    public void setDivide(boolean isDivide) {
        this.isDivide = isDivide;
    }

    public boolean isAddHostname() {
        return isAddHostname;
    }

    public void setAddHostname(boolean isAddHostname) {
        this.isAddHostname = isAddHostname;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public String getAuthPwd() {
        return authPwd;
    }

    public void setAuthPwd(String authPwd) {
        this.authPwd = authPwd;
    }

}
