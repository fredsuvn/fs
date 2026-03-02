package internal.tests.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import space.sunqian.fs.object.annotation.DatePattern;
import space.sunqian.fs.object.annotation.NumPattern;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode
public class TestPropsTarget {
    private int i1;
    private long l1;
    private String str1;
    private Integer ii1;
    private Long ll1;
    private BigDecimal bb1;
    private int i2;
    private long l2;
    private String str2;
    private Integer ii2;
    private Long ll2;
    private BigDecimal bb2;

    @DatePattern("yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String fmt1;
    @DatePattern("yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date fmt2;
    @NumPattern("#.000")
    @NumberFormat(pattern = "#.000")
    private String fmt3;
    @NumPattern("#.0000")
    @NumberFormat(pattern = "#.0000")
    private String fmt4;
}
