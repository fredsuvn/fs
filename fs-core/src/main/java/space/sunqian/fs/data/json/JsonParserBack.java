// package space.sunqian.fs.data.json;
//
// import space.sunqian.annotation.Nonnull;
// import space.sunqian.fs.base.chars.CharsKit;
//
// import java.io.InputStream;
// import java.io.Reader;
// import java.nio.channels.ReadableByteChannel;
// import java.util.ArrayList;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;
//
// final class JsonParserBack {
//
//     private static final class JsonParserImpl implements JsonParser {
//
//         @Override
//         public @Nonnull JsonData parse(byte @Nonnull [] bytes) throws JsonDataParsingException {
//             return null;
//         }
//
//         @Override
//         public @Nonnull JsonData parse(@Nonnull InputStream input) throws JsonDataParsingException {
//             return null;
//         }
//
//         @Override
//         public @Nonnull JsonData parse(@Nonnull ReadableByteChannel channel) throws JsonDataParsingException {
//             return null;
//         }
//
//         @Override
//         public @Nonnull JsonData parse(char @Nonnull [] chars) throws JsonDataParsingException {
//             return null;
//         }
//
//         @Override
//         public @Nonnull JsonData parse(@Nonnull CharSequence charSequence) throws JsonDataParsingException {
//             return null;
//         }
//
//         @Override
//         public @Nonnull JsonData parse(@Nonnull Reader reader) throws JsonDataParsingException {
//             return null;
//         }
//     }
//
//     // Implementation functions:
//
//     private static final @Nonnull Object NULL = new Object();
//
//     private static final byte @Nonnull [] EXPECT_NULL = {'u', 'l', 'l'};
//     private static final byte @Nonnull [] EXPECT_TRUE = {'r', 'u', 'e'};
//     private static final byte @Nonnull [] EXPECT_FALSE = {'a', 'l', 's', 'e'};
//
//     private static @Nonnull Object parseJson(byte @Nonnull [] bytes, int start, boolean toEnd) throws Exception {
//         Object result = null;
//
//         for (int i = start; i < bytes.length; i++) {
//             byte b = bytes[i];
//             switch (b) {
//                 case 'n':
//                     expect(bytes, i + 1, EXPECT_NULL);
//                     result = NULL;
//                     break;
//                 case 't':
//                     expect(bytes, i + 1, EXPECT_TRUE);
//                     result = true;
//                     break;
//                 case 'f':
//                     expect(bytes, i + 1, EXPECT_FALSE);
//                     result = false;
//                     break;
//                 case '\"':
//                     result = reader.nextString();
//                     break;
//
//             }
//         }
//
//         int i;
//         if ((i = reader.nextCharSkipWhitespace()) != -1) {
//             char c = (char) i;
//             switch (c) {
//                 case 'n':
//                     reader.expect(EXPECT_NULL);
//                     result = NULL;
//                     break;
//                 case 't':
//                     reader.expect(EXPECT_TRUE);
//                     result = true;
//                     break;
//                 case 'f':
//                     reader.expect(EXPECT_FALSE);
//                     result = false;
//                     break;
//                 case '\"':
//                     result = reader.nextString();
//                     break;
//                 case '{':
//                     Map<String, Object> objBuilder = new LinkedHashMap<>();
//                     parseObject(reader, objBuilder);
//                     result = objBuilder;
//                     break;
//                 case '[':
//                     List<Object> arrBuilder = new ArrayList<>();
//                     parseArray(reader, arrBuilder);
//                     result = arrBuilder;
//                     break;
//                 case '0':
//                 case '1':
//                 case '2':
//                 case '3':
//                 case '4':
//                 case '5':
//                 case '6':
//                 case '7':
//                 case '8':
//                 case '9':
//                 case '-':
//                     result = reader.nextNumber();
//                     break;
//                 default:
//                     throw new JsonDataParsingException(reader.nextIndex() - 1, String.valueOf(c), null);
//             }
//         }
//         if (result == null) {
//             throw new JsonDataParsingException(reader.nextIndex(), null, null);
//         }
//         if (result == NULL) {
//             return null;
//         }
//         if (toEnd) {
//             reader.skipToEof();
//         }
//         return result;
//     }
//
//     private static void expect(byte @Nonnull [] bytes, int start, byte @Nonnull [] shouldBe) throws Exception {
//         int rest = bytes.length - start;
//         if (rest < shouldBe.length) {
//             throw new JsonDataParsingException(bytes.length, null, String.valueOf(shouldBe[rest]));
//         }
//         for (int i = start, j = 0; j < shouldBe.length; i++, j++) {
//             byte b = bytes[i];
//             byte target = shouldBe[j];
//             if (b != target) {
//                 throw new JsonDataParsingException(i, String.valueOf(b), String.valueOf(target));
//             }
//         }
//     }
//
//     private static @Nonnull String nextString(byte @Nonnull [] bytes, int start) throws Exception {
//         for (int i = start; i < bytes.length; i++) {
//             byte b = bytes[i];
//             switch (b) {
//                 case '\"':
//                     return new String(bytes, start, i - start, CharsKit.UTF_8);
//                 case '\\':
//                     StringBuilder builder = new StringBuilder(new String(bytes, start, i - start, CharsKit.UTF_8));
//                     return nextStringWithEscape(bytes, i, builder);
//                 default:
//                     // builder.append(c);
//             }
//         }
//         throw new JsonDataParsingException(bytes.length, null, "\"");
//     }
//
//     private static @Nonnull String nextStringWithEscape(
//         byte @Nonnull [] bytes, int start, @Nonnull StringBuilder builder
//     ) {
//         int s = parseEscape(bytes, start, builder);
//         for (int i = s; i < bytes.length;) {
//             byte b = bytes[i];
//             switch (b) {
//                 case '\"':
//                     builder.append(new String(bytes, s, i - s, CharsKit.UTF_8));
//                     return builder.toString();
//                 case '\\':
//                     builder.append(new String(bytes, s, i - s, CharsKit.UTF_8));
//                     i = parseEscape(bytes, i, builder);
//                     s = i;
//                     continue;
//                 default:
//                     s++;
//                     //builder.append(b);
//             }
//         }
//         throw new JsonDataParsingException(bytes.length, null, "\"");
//     }
//
//     private static int parseEscape(byte @Nonnull [] bytes, int start, @Nonnull StringBuilder builder) {
//         if (start >= bytes.length) {
//             throw new JsonDataParsingException(bytes.length, null, null);
//         }
//         byte b = bytes[start];
//         switch (b) {
//             case '\"':
//             case '\\':
//                 builder.append(b);
//                 return start + 1;
//             case 'r':
//                 builder.append('\r');
//                 return start + 1;
//             case 'n':
//                 builder.append('\n');
//                 return start + 1;
//             case 't':
//                 builder.append('\t');
//                 return start + 1;
//             case 'b':
//                 builder.append('\b');
//                 return start + 1;
//             case 'f':
//                 builder.append('\f');
//                 return start + 1;
//             case 'u':
//                 parseUnicode(bytes, start + 1, builder);
//                 return start + 5;
//             default:
//                 throw new JsonDataParsingException(start, String.valueOf(b), null);
//         }
//     }
//
//     private static void parseUnicode(byte @Nonnull [] bytes, int start, @Nonnull StringBuilder builder) {
//         int rest = bytes.length - start;
//         if (rest < 4) {
//             throw new JsonDataParsingException(bytes.length, null, null);
//         }
//         char c1 = (char) bytes[start];
//         char c2 = (char) bytes[start + 1];
//         char c3 = (char) bytes[start + 2];
//         char c4 = (char) bytes[start + 3];
//         builder.append(CharsKit.unicodeToChar(c1, c2, c3, c4));
//     }
//
//     private static boolean isNumberMember(char c) {
//         return (c >= '0' && c <= '9')
//             || (c == '.')
//             || (c == 'e')
//             || (c == 'E')
//             || (c == '+');
//     }
//
//     private static boolean isWhitespace(int c) {
//         return c == (' ' & 0x0000ffff) || c == ('\t' & 0x0000ffff) || c == ('\r' & 0x0000ffff) || c == ('\n' & 0x0000ffff);
//     }
// }
