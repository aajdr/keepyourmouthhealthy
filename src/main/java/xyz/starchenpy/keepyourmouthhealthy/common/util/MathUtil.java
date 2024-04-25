package xyz.starchenpy.keepyourmouthhealthy.common.util;

public class MathUtil {
    public static float easeOutQuint(float x, int y) {
        return 1 - (float)Math.pow(1 - x, y);
    }
}
