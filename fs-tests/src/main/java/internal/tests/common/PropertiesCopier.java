package internal.tests.common;

import cn.hutool.core.bean.BeanUtil;
import org.apache.commons.beanutils.BeanUtils;
import space.sunqian.common.Fs;

public interface PropertiesCopier {

    static PropertiesCopier createCopier(String copierType) {
        return switch (copierType) {
            case "fs" -> Fs::copyProperties;
            case "apache" -> (source, target) -> BeanUtils.copyProperties(target, source);
            case "hutool" -> (source, target) -> BeanUtil.copyProperties(target, source);
            default -> throw new IllegalArgumentException("proxyType is not support");
        };
    }

    void copyProperties(Object source, Object target) throws Exception;
}
