package test;

import org.testng.annotations.Test;
import xyz.fslabo.common.base.ShouldNotHappenException;
import xyz.fslabo.common.bean.BeanException;
import xyz.fslabo.common.bean.BeanResolvingException;
import xyz.fslabo.common.codec.CodecException;
import xyz.fslabo.common.invoke.InvocationException;
import xyz.fslabo.common.io.IORuntimeException;
import xyz.fslabo.common.reflect.JvmException;
import xyz.fslabo.common.reflect.ReflectionException;
import xyz.fslabo.common.reflect.proxy.ProxyException;

import static org.testng.Assert.expectThrows;

public class ExceptionCoverageTest {

    @Test
    public void additionalExceptionCoverage() {

        expectThrows(ShouldNotHappenException.class, () -> {
            throw new ShouldNotHappenException();
        });
        expectThrows(ShouldNotHappenException.class, () -> {
            throw new ShouldNotHappenException("");
        });
        expectThrows(ShouldNotHappenException.class, () -> {
            throw new ShouldNotHappenException("", new RuntimeException());
        });
        expectThrows(ShouldNotHappenException.class, () -> {
            throw new ShouldNotHappenException(new RuntimeException());
        });

        expectThrows(InvocationException.class, () -> {
            throw new InvocationException();
        });
        expectThrows(InvocationException.class, () -> {
            throw new InvocationException("");
        });
        expectThrows(InvocationException.class, () -> {
            throw new InvocationException("", new RuntimeException());
        });
        expectThrows(InvocationException.class, () -> {
            throw new InvocationException(new RuntimeException());
        });

        expectThrows(BeanResolvingException.class, () -> {
            throw new BeanResolvingException();
        });
        expectThrows(BeanResolvingException.class, () -> {
            throw new BeanResolvingException(String.class);
        });
        expectThrows(BeanResolvingException.class, () -> {
            throw new BeanResolvingException(new RuntimeException());
        });
        expectThrows(BeanException.class, () -> {
            throw new BeanException();
        });
        expectThrows(BeanException.class, () -> {
            throw new BeanException(new RuntimeException());
        });

        expectThrows(ProxyException.class, () -> {
            throw new ProxyException();
        });
        expectThrows(ProxyException.class, () -> {
            throw new ProxyException(new RuntimeException());
        });
        expectThrows(ProxyException.class, () -> {
            throw new ProxyException("", new RuntimeException());
        });

        expectThrows(JvmException.class, () -> {
            throw new JvmException();
        });
        expectThrows(JvmException.class, () -> {
            throw new JvmException("");
        });
        expectThrows(JvmException.class, () -> {
            throw new JvmException("", new RuntimeException());
        });
        expectThrows(JvmException.class, () -> {
            throw new JvmException(new RuntimeException());
        });
        expectThrows(ReflectionException.class, () -> {
            throw new ReflectionException();
        });
        expectThrows(ReflectionException.class, () -> {
            throw new ReflectionException("");
        });
        expectThrows(ReflectionException.class, () -> {
            throw new ReflectionException("", new RuntimeException());
        });
        expectThrows(ReflectionException.class, () -> {
            throw new ReflectionException(new RuntimeException());
        });

        expectThrows(IORuntimeException.class, () -> {
            throw new IORuntimeException();
        });
        expectThrows(IORuntimeException.class, () -> {
            throw new IORuntimeException("");
        });
        expectThrows(IORuntimeException.class, () -> {
            throw new IORuntimeException("", new RuntimeException());
        });
        expectThrows(IORuntimeException.class, () -> {
            throw new IORuntimeException(new RuntimeException());
        });

        expectThrows(CodecException.class, () -> {
            throw new CodecException();
        });
        expectThrows(CodecException.class, () -> {
            throw new CodecException("");
        });
        expectThrows(CodecException.class, () -> {
            throw new CodecException("", new RuntimeException());
        });
        expectThrows(CodecException.class, () -> {
            throw new CodecException(new RuntimeException());
        });
    }
}
