package internal.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import space.sunqian.fs.object.annotation.DatePattern;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode
public class TestJsonData {
    private int i1;
    private long l1;
    private String str1;
    private Integer ii1;
    private Long ll1;
    private BigDecimal bb1;
    private long[] la1;
    private BigDecimal[] ba1;
    private List<String> sa1;
    private int i2;
    private long l2;
    private String str2;
    private Integer ii2;
    private Long ll2;
    private BigDecimal bb2;
    private long[] la2;
    private BigDecimal[] ba2;
    private List<String> sa2;
    private int i3;
    private long l3;
    private String str3;
    private Integer ii3;
    private Long ll3;
    private BigDecimal bb3;
    private long[] la3;
    private BigDecimal[] ba3;
    private List<String> sa3;

    @DatePattern(value = "yyyy-MM", zoneId = "GMT+8")
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    private Date d1;

    @DatePattern(value = "yyyy-MM-dd", zoneId = "GMT+8")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date d2;

    @DatePattern(value = "yyyy-MM-dd HH:mm:ss", zoneId = "GMT+8")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date d3;
}
