package internal.samples;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import space.sunqian.fs.object.convert.ObjectConverter;

public class ObjectSamples {

    public static void main(String[] args) {
        DataFrom dataFrom = new DataFrom("001", 999);
        DataTo dataTo = ObjectConverter.defaultConverter()
            .convert(dataFrom, DataTo.class);
        System.out.println(dataTo);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class DataFrom {
        private String id;
        private int code;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class DataTo {
        private char[] id;
        private Double code;
    }
}
