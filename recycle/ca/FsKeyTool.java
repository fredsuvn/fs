//package xyz.srclab.common.security;
//
//import xyz.srclab.common.base.Fs;
//import xyz.srclab.common.io.FsIO;
//
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
///**
// * Utilities for JDK keytool.
// *
// * @author fredsuvn
// */
//public class FsKeyTool {
//
//    private static final List<String> GEN_KEY_PAIR_PARAMS = Arrays.asList(
//        "alias", "keyalg", "keysize", "keystore",
//        "CN", "OU", "O", "L", "ST", "C"
//    );
//
//    /**
//     * Command:
//     * <pre>
//     *     > keytool -genkeypair -alias ca1 -keyalg RSA -keysize 1024 -keystore ca.jks
//     *     > 输入密钥库口令:
//     *     > 再次输入新口令:
//     *     > 您的名字与姓氏是什么?
//     *     >   [Unknown]:  Suvn
//     *     > 您的组织单位名称是什么?
//     *     >   [Unknown]:  Suvn
//     *     > 您的组织名称是什么?
//     *     >   [Unknown]:  Suvn
//     *     > 您所在的城市或区域名称是什么?
//     *     >   [Unknown]:  Shenzhen
//     *     > 您所在的省/市/自治区名称是什么?
//     *     >   [Unknown]:  Shenzhen
//     *     > 该单位的双字母国家/地区代码是什么?
//     *     >   [Unknown]:  CN
//     *     > CN=Suvn, OU=Suvn, O=Suvn, L=Shenzhen, ST=Shenzhen, C=CN是否正确?
//     *     >   [否]:  y
//     * </pre>
//     *
//     * @param password password
//     * @param params   generate params
//     */
//    public static void genKeyPair(String command, CharSequence password, Map<String, String> params) {
//        try {
//            checkParams(params);
//            Process process = Fs.runProcess(true,
//                command, "-genkeypair",
//                "-alias", params.get("alias"),
//                "-keyalg", params.get("keyalg"),
//                "-keysize", params.get("keysize"),
//                "-keystore", params.get("keystore")
//            );
//            OutputStream outputStream = process.getOutputStream();
//            InputStream in = process.getInputStream();
//            List<String> answers = Arrays.asList(password.toString(), password.toString(),
//                params.get("CN"), params.get("OU"), params.get("O"), params.get("L"), params.get("ST"), params.get("C"), "y");
//            int i = 0;
//            while (true) {
//                byte[] bytes = FsIO.availableBytes(in);
//                if (bytes == null) {
//                    break;
//                }
//                //System.out.println(str);
//                if (i >= answers.size()) {
//                    break;
//                }
//                outputStream.write((answers.get(i) + "\n").getBytes());
//                outputStream.flush();
//                i++;
//            }
//            process.waitFor();
//        } catch (Exception e) {
//            throw new FsSecurityException(e);
//        }
//    }
//
//    private static void checkParams(Map<String, String> params) {
//        for (String param : GEN_KEY_PAIR_PARAMS) {
//            if (!params.containsKey(param)) {
//                throw new FsSecurityException("Miss param: " + param + ".");
//            }
//        }
//    }
//}
