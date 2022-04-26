package com.truking.wms.tool.utils;

/**
 * created by jiangpanming
 * on 2020-06-24
 * desc 防止按钮重复点击的工具类
 */
public class ButtonUtils {

    private static long lastClickTime = 0;
    private static long DIFF = 2000;//2 秒内不让点击
    private static int lastButtonId = -1;

    /**
     * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        return isFastDoubleClick(-1, DIFF);
    }

    /**
     * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
     *
     * @return
     */
    public static boolean isFastDoubleClick(int buttonId) {
        return isFastDoubleClick(buttonId, DIFF);
    }

    /**
     * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
     *
     * @param diff
     * @return
     */
    public static boolean isFastDoubleClick(int buttonId, long diff) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (lastButtonId == buttonId && lastClickTime > 0 && timeD < diff) {
            return true;
        }
        lastClickTime = time;
        lastButtonId = buttonId;
        return false;
    }
}
