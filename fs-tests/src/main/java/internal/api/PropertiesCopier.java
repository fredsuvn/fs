package internal.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import internal.data.TestPropsData;
import internal.data.TestPropsTarget;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.converters.DateConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import space.sunqian.fs.Fs;
import space.sunqian.fs.object.convert.ConvertOption;

import java.text.SimpleDateFormat;
import java.util.Date;

public interface PropertiesCopier {

    static PropertiesCopier createCopier(String copierType) {
        return switch (copierType) {
            case "fs" -> new FsImpl();
            case "fs-newInstMode" -> new FsNewInstImpl();
            case "spring" -> new SpringImpl();
            case "apache" -> new ApacheImpl();
            case "hutool" -> new HutoolImpl();
            case "direct" -> new DirectImpl();
            default -> throw new IllegalArgumentException();
        };
    }

    void copyProperties(Object source, Object target, boolean format) throws Exception;

    class FsImpl implements PropertiesCopier {

        @Override
        public void copyProperties(Object source, Object target, boolean format) throws Exception {
            Fs.copyProperties(source, target);
        }
    }

    class FsNewInstImpl implements PropertiesCopier {

        @Override
        public void copyProperties(Object source, Object target, boolean format) throws Exception {
            Fs.copyProperties(source, target, ConvertOption.newInstanceMode(true));
        }
    }

    class SpringImpl implements PropertiesCopier {

        private final CustomDateEditor customDateEditor =
            new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true);

        @Override
        public void copyProperties(Object source, Object target, boolean format) throws Exception {
            BeanUtils.copyProperties(source, target);
            if (format) {
                TestPropsData src = (TestPropsData) source;
                BeanWrapper beanWrapper = new BeanWrapperImpl(target);
                beanWrapper.registerCustomEditor(Date.class, customDateEditor);
                beanWrapper.setPropertyValue("fmt1", src.getFmt1());
                beanWrapper.setPropertyValue("fmt2", src.getFmt2());
                beanWrapper.setPropertyValue("fmt3", src.getFmt3());
                beanWrapper.setPropertyValue("fmt4", src.getFmt4());
            }
        }
    }

    class ApacheImpl implements PropertiesCopier {

        private final BeanUtilsBean beanUtils;

        {
            DateConverter converter = new DateConverter();
            converter.setPattern("yyyy-MM-dd HH:mm:ss");
            ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
            convertUtilsBean.register(converter, Date.class);
            beanUtils = new BeanUtilsBean(convertUtilsBean);
        }

        @Override
        public void copyProperties(Object source, Object target, boolean format) throws Exception {
            beanUtils.copyProperties(target, source);
        }
    }

    class HutoolImpl implements PropertiesCopier {

        private final CopyOptions copyOptions = CopyOptions.create().setFormatIfDate("yyyy-MM-dd HH:mm:ss");

        @Override
        public void copyProperties(Object source, Object target, boolean format) throws Exception {
            BeanUtil.copyProperties(source, target, copyOptions);
        }
    }

    class DirectImpl implements PropertiesCopier {

        private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public void copyProperties(Object source, Object target, boolean format) throws Exception {
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
            if (format) {
                dst.setFmt1(simpleDateFormat.parse(src.getFmt1()));
                dst.setFmt2(simpleDateFormat.parse(src.getFmt2()));
                dst.setFmt3(simpleDateFormat.parse(src.getFmt3()));
                dst.setFmt4(simpleDateFormat.parse(src.getFmt4()));
            }
        }
    }
}
