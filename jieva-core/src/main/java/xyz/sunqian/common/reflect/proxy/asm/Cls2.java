package xyz.sunqian.common.reflect.proxy.asm;

public class Cls2 extends Cls<Integer> implements Inter<Long> {

    @Override
    public Long getInter(Long aLong) throws Throwable {
        return 0L;
    }
    Long ss() throws Throwable {
        return this.getInter(11L);
    }
}
