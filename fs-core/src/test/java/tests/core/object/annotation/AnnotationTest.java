package tests.core.object.annotation;

import internal.utils.Asserter;
import internal.utils.TestPrint;
import org.junit.jupiter.api.Test;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.object.annotation.AnnotationDetail;
import space.sunqian.fs.object.annotation.AnnotationSet;
import space.sunqian.fs.object.annotation.DatePattern;
import space.sunqian.fs.object.annotation.DatePatternDetail;
import space.sunqian.fs.object.annotation.NumberPattern;
import space.sunqian.fs.object.annotation.NumberPatternDetail;
import space.sunqian.fs.object.annotation.SimpleAnnotationDetail;
import space.sunqian.fs.object.convert.ConvertOption;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class AnnotationTest implements Asserter, TestPrint {

    @Test
    public void testAnnotationSet() throws Exception {
        Field fieldA = X.class.getDeclaredField("a");
        AnnotationSet annotationSetA = AnnotationSet.from(fieldA);
        testAnnotations(annotationSetA);
        testDetails(annotationSetA);
        Field fieldB = X.class.getDeclaredField("b");
        AnnotationSet annotationSetB = AnnotationSet.from(fieldB);
        testFieldB(annotationSetB);
    }

    private void testAnnotations(AnnotationSet annotationSet) throws Exception {
        List<Annotation> annotations = annotationSet.annotations();
        assertEquals(4, annotations.size());
        assertNotNull(annotations.stream().filter(a -> a instanceof NumberPattern).findFirst().orElse(null));
        assertNotNull(annotations.stream().filter(a -> a instanceof DatePattern).findFirst().orElse(null));
        assertNotNull(annotations.stream().filter(a -> a instanceof Nullable).findFirst().orElse(null));
        assertNotNull(annotations.stream().filter(a -> a instanceof AS).findFirst().orElse(null));
        NumberPattern numberPattern = annotationSet.get(NumberPattern.class);
        assertEquals("#.0000", numberPattern.value());
        DatePattern datePattern = annotationSet.get(DatePattern.class);
        assertEquals("yyyy-MM-dd", datePattern.value());
        assertEquals("", datePattern.zoneId());
        Nullable nullable = annotationSet.get(Nullable.class);
        AS as = annotationSet.get(AS.class);
        assertEquals(3, as.value().length);
        assertEquals("1", as.value()[0].value());
        assertEquals("2", as.value()[1].value());
        assertEquals("3", as.value()[2].value());
        assertNull(annotationSet.get(Nonnull.class));
    }

    private void testDetails(AnnotationSet annotationSet) throws Exception {
        List<AnnotationDetail<?>> details = annotationSet.details();
        assertEquals(4, details.size());
        assertNotNull(details.stream().filter(a -> a instanceof NumberPatternDetail).findFirst().orElse(null));
        assertNotNull(details.stream().filter(a -> a instanceof DatePatternDetail).findFirst().orElse(null));
        assertEquals(2, details.stream().filter(a -> a instanceof SimpleAnnotationDetail<?>).collect(Collectors.toList()).size());
        NumberPatternDetail numberPattern = annotationSet.getDetail(NumberPatternDetail.class);
        assertSame(annotationSet.get(NumberPattern.class), numberPattern.annotation());
        assertEquals("11.1122", numberPattern.formatter().format(11.11223344).toString());
        assertSame(ConvertOption.NUMBER_FORMATTER, numberPattern.option().key());
        assertSame(numberPattern.formatter(), numberPattern.option().value());
        DatePatternDetail datePattern = annotationSet.getDetail(DatePatternDetail.class);
        assertSame(annotationSet.get(DatePattern.class), datePattern.annotation());
        assertEquals(ZoneId.systemDefault(), datePattern.zoneId());
        Date date = new Date();
        assertEquals(
            new SimpleDateFormat("yyyy-MM-dd").format(date),
            datePattern.formatter().format(date)
        );
        assertSame(ConvertOption.DATE_FORMATTER, datePattern.option().key());
        assertSame(datePattern.formatter(), datePattern.option().value());
        SimpleAnnotationDetail<?> nullable = annotationSet.getDetail(SimpleAnnotationDetail.class);
        assertSame(annotationSet.get(Nullable.class), nullable.annotation());
        SimpleAnnotationDetail<AS> as = annotationSet.getDetailByAnnotationType(AS.class);
        assertSame(annotationSet.get(AS.class), as.annotation());
        assertNull(annotationSet.getDetailByAnnotationType(Nonnull.class));
    }

    private void testFieldB(AnnotationSet annotationSet) throws Exception {
        NumberPatternDetail numberPattern = annotationSet.getDetail(NumberPatternDetail.class);
        assertEquals(
            new DecimalFormat(numberPattern.annotation().value()).format(11.11223344),
            numberPattern.formatter().format(11.11223344).toString()
        );
        DatePatternDetail datePattern = annotationSet.getDetail(DatePatternDetail.class);
        assertEquals(ZoneId.of("Asia/Shanghai"), datePattern.zoneId());
    }

    @Test
    public void testMultiAnnotationSet() throws Exception {
        Field fieldA = M.class.getDeclaredField("a");
        AnnotationSet annotationSetA = AnnotationSet.from(fieldA);
        DatePattern pa = annotationSetA.get(DatePattern.class);
        DatePatternDetail da = annotationSetA.getDetail(DatePatternDetail.class);
        Field fieldB = M.class.getDeclaredField("b");
        AnnotationSet annotationSetB = AnnotationSet.from(fieldB);
        DatePattern pb = annotationSetB.get(DatePattern.class);
        DatePatternDetail db = annotationSetB.getDetail(DatePatternDetail.class);
        NumberPattern nb = annotationSetB.get(NumberPattern.class);
        NumberPatternDetail ndb = annotationSetB.getDetail(NumberPatternDetail.class);
        AnnotationSet multiSet = AnnotationSet.multiSet(annotationSetA, annotationSetB);
        assertEquals(
            Arrays.asList(pa, pb, nb),
            multiSet.annotations()
        );
        assertEquals(
            Arrays.asList(da, db, ndb),
            multiSet.details()
        );
        assertSame(pa, multiSet.get(DatePattern.class));
        assertSame(da, multiSet.getDetail(DatePatternDetail.class));
        assertSame(nb, multiSet.get(NumberPattern.class));
        assertSame(ndb, multiSet.getDetail(NumberPatternDetail.class));
        assertSame(ndb, multiSet.getDetailByAnnotationType(NumberPattern.class));
        assertNull(multiSet.get(Nullable.class));
        assertNull(multiSet.getDetailByAnnotationType(Nullable.class));
        class D implements AnnotationDetail<Nullable> {

            @Override
            public Nullable annotation() {
                return null;
            }
        }
        assertNull(multiSet.getDetail(D.class));
    }

    @Test
    public void testEmptySet() {
        AnnotationSet empty = AnnotationSet.emptySet();
        assertEquals(Collections.emptyList(), empty.annotations());
        assertEquals(Collections.emptyList(), empty.details());
        assertNull(empty.get(NumberPattern.class));
        assertNull(empty.getDetail(DatePatternDetail.class));
        assertNull(empty.getDetailByAnnotationType(Nullable.class));
    }

    public static class X {

        @NumberPattern("#.0000")
        @DatePattern("yyyy-MM-dd")
        @Nullable
        @A("1")
        @A("2")
        @A("3")
        private String a;

        @NumberPattern
        @DatePattern(zoneId = "Asia/Shanghai")
        private String b;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface AS {
        A[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(AS.class)
    public @interface A {
        String value();
    }

    public static class M {

        @DatePattern("yyyy-MM-dd")
        private String a;

        @DatePattern(zoneId = "Asia/Shanghai")
        @NumberPattern("#.0000")
        private String b;
    }
}