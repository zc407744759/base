package com.wutos.base.common.util;

/**
 * @author zc
 * 用于接收启动程序传入的配置文件路径
 * @date 2019-07-15
 */
public class ConfigPath {
    /**
     * 应用运行的环境是否是windows,否则就是linux
     */
    private Boolean isWindows = true;//Windows 10
    /**
     * 默认路径
     */
    private String defaultPath;
    /**
     * 是否启用默认路径
     */
    private Boolean isDefault = true;
    /**
     * 公共配置文件路径
     */
    private String pubPath;
    /**
     * 应用默认配置文件路径
     */
    private String selfPath;

    /**
     * 默认情况会读取公共配置文件的default的数据库配置，如不是默认的，启动时传递对应的值做修改
     */
    private String dataSourceDB = "default";

    public ConfigPath(String defaultPath){
        this.defaultPath = defaultPath;
    }
    public String getPubPath() {
        if(this.getDefault()){
            pubPath = this.getDefaultPath()+"public.properties";
        }
        return pubPath;
    }

    public void setPubPath(String pubPath) {
        this.pubPath = pubPath;
    }

    public String getSelfPath() {
        if(this.getDefault()){
            selfPath = this.getDefaultPath()+"application.properties";
        }
        return selfPath;
    }

    public void setSelfPath(String selfPath) {
        this.selfPath = selfPath;
    }

    public Boolean getWindows() {
        String systemName = System.getProperty("os.name");//Windows 10
        if (systemName.toLowerCase().startsWith("windows")) {
            return true;
        }else {
            return false;
        }
    }

    public String getDefaultPath() {
        if(this.getWindows()){
            //windows环境
            return defaultPath;
        }else {
            //linux环境
            defaultPath = "/etc/wutos/conf/";
            return defaultPath;
        }
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public void setWindows(Boolean windows) {
        isWindows = windows;
    }

    public void setDefaultPath(String defaultPath) {
        this.defaultPath = defaultPath;
    }

    public String getDataSourceDB() {
        return dataSourceDB;
    }

    public void setDataSourceDB(String dataSourceDB) {
        this.dataSourceDB = dataSourceDB;
    }
}
