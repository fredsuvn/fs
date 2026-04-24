// package space.sunqian.fs.base.number;
//
// import space.sunqian.annotation.Nonnull;
//
// import java.math.BigDecimal;
// import java.math.BigInteger;
//
// public enum NumServiceImplByJ9 implements NumService {
//     INST;
//
//     @Override
//     public @Nonnull Number toNumber(@Nonnull CharSequence cs) throws NumException {
//         return toNumber0(cs.toString());
//     }
//
//     @Override
//     public @Nonnull Number toNumber(
//         @Nonnull CharSequence cs,
//         int start,
//         int end
//     ) throws IndexOutOfBoundsException, NumException {
//         return toNumber0(cs.subSequence(start, end).toString());
//     }
//
//     private @Nonnull Number toNumber0(@Nonnull CharSequence str,
//                                       int start,
//                                       int end) throws NumberFormatException {
//         if (str.contains(".") || str.contains("e") || str.contains("E")) {
//             return new BigDecimal(str);
//         }
//         int len = str.length();
//         if (str.startsWith("-") || str.startsWith("+")) {
//             len--;
//         }
//         if (len <= 9) {
//             return Integer.parseInt(str);
//         }
//         if (len <= 18) {
//             return Long.parseLong(str);
//         }
//         return new BigInteger(str);
//     }
// }
