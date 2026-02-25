package org.jeecg.config.vo;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2022年01月21日 14:23
 */
public class Shiro {
    private String excludeUrls = "";
    /**
     * Base64-encoded rememberMe cipher key (16/24/32 bytes after decode)
     */
    private String rememberMeCipherKey = "";

    public String getExcludeUrls() {
        return excludeUrls;
    }

    public void setExcludeUrls(String excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    public String getRememberMeCipherKey() {
        return rememberMeCipherKey;
    }

    public void setRememberMeCipherKey(String rememberMeCipherKey) {
        this.rememberMeCipherKey = rememberMeCipherKey;
    }
}
