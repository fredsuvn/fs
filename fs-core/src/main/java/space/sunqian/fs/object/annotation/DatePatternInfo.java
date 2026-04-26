// package space.sunqian.fs.object.annotation;
//
// import space.sunqian.annotation.Nonnull;
// import space.sunqian.fs.base.date.DateFormatter;
//
// import java.time.ZoneId;
//
// public final class DatePatternInfo implements PropertyAnnotation<DatePattern> {
//
//     private final @Nonnull DatePattern pattern;
//     private final @Nonnull ZoneId zoneId;
//     private final @Nonnull DateFormatter formatter;
//
//     public DatePatternInfo(@Nonnull DatePattern pattern) {
//         this.pattern = pattern;
//         this.zoneId = ZoneId.of(pattern.zoneId());
//         this.formatter = DateFormatter.ofPattern(pattern.value(), zoneId);
//     }
//
//     @Override
//     public @Nonnull DatePattern annotation() {
//         return pattern;
//     }
//
//     public @Nonnull ZoneId zoneId() {
//         return zoneId;
//     }
//
//     public @Nonnull DateFormatter defaultFormatter() {
//         return formatter;
//     }
// }
