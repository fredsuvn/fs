package internal.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode
public class TestPropsData {
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

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String fmt1;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String fmt2;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String fmt3;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String fmt4;
}
