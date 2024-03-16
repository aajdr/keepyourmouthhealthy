package xyz.starchenpy.keepyourmouthhealthy.mixin;

public interface ItemStackAccessor {
    default void keepyourmouthhealthy$setDuration(int duration) {}

    default int keepyourmouthhealthy$getDuration() {
        return 0;
    }
}
