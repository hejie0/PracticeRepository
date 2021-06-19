package netty.demo.filesync.kafka;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * @Author: hejie
 * @Date: 2021/5/31 14:16
 * @Version: 1.0
 */
public class FieldExclusionStrategy implements ExclusionStrategy {

    private Class classToExclude;

    public FieldExclusionStrategy(Class classToExclude) {
        this.classToExclude = classToExclude;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return clazz.equals(classToExclude);
    }
}
