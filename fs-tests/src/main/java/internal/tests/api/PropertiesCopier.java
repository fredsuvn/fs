package internal.tests.api;

import cn.hutool.core.bean.BeanUtil;
import internal.tests.common.TestPropsData;
import internal.tests.common.TestPropsTarget;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import space.sunqian.fs.Fs;

import java.util.Date;

public interface PropertiesCopier {

    static void init() {
        DateConverter converter = new DateConverter();
        converter.setPattern("yyyy-MM-dd HH:mm:ss");
        ConvertUtils.register(converter, Date.class);
    }

    static PropertiesCopier createCopier(String copierType) {
        return switch (copierType) {
            case "fs" -> Fs::copyProperties;
            case "spring" -> org.springframework.beans.BeanUtils::copyProperties;
            case "apache" -> (source, target) -> BeanUtils.copyProperties(target, source);
            case "hutool" -> BeanUtil::copyProperties;
            case "direct" -> (source, target) -> {
                TestPropsData src = (TestPropsData) source;
                TestPropsTarget dst = (TestPropsTarget) target;
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
