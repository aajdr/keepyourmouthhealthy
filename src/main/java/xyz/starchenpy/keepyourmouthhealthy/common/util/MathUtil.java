package xyz.starchenpy.keepyourmouthhealthy.common.util;

public class MathUtil {
    /**
     * 使动画快入缓出
     */
    public static float easeOutQuint(float x, int y) {
        return 1 - (float)Math.pow(1 - x, y);
    }
}
