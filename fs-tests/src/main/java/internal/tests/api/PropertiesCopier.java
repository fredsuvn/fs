package internal.tests.api;

import cn.hutool.core.bean.BeanUtil;
import internal.tests.common.TestPropsData;
import org.apache.commons.beanutils.BeanUtils;
import space.sunqian.common.Fs;

public interface PropertiesCopier {

    static PropertiesCopier createCopier(String copierType) {
        return switch (copierType) {
            case "fs" -> Fs::copyProperties;
            case "apache" -> (source, target) -> BeanUtils.copyProperties(target, source);
            case "hutool" -> (source, target) -> BeanUtil.copyProperties(target, source);
            case "direct" -> (source, target) -> {
                TestPropsData src = (TestPropsData) source;
                TestPropsData dst = (TestPropsData) target;
                dst.setI1(src.getI1());
                dst.setL1(src.getL1());
                dst.setStr1(src.getStr1());
                dst.setIi1(src.getIi1());
                dst.setLl1(src.getLl1());
                dst.setBb1(src.getBb1());
                dst.setI2(src.getI2());
                dst.setL2(src.getL2());
                dst.setStr2(src.getStr2());
                dst.setIi2(src.getIi2());
                dst.setLl2(src.getLl2());
                dst.setBb2(src.getBb2());
            };
            default -> throw new IllegalArgumentException();
        };
    }

    void copyProperties(Object source, Object target) throws Exception;
}
