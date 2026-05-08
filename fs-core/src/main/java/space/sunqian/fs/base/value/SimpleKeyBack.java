package space.sunqian.fs.base.value;

import space.sunqian.annotation.Nonnull;

import java.util.Objects;

final class SimpleKeyBack {

    static boolean equals(@Nonnull SimpleKey k1, @Nonnull SimpleKey k2) {
        int s1 = k1.size();
        int s2 = k2.size();
        if (s1 != s2) {
            return false;
        }
        for (int i = 0; i < s1; i++) {
            Object v1 = k1.get(i);
            Object v2 = k2.get(i);
            if (!Objects.equals(v1, v2)) {
                return false;
            }
        }
        return true;
    }

    // static int hashCode(@Nonnull SimpleKey k) {
    //     int result = 0;
    //     for (Object e : k.elements()) {
    //         result = 31 * result + Objects.hashCode(e);
    //     }
    //     return result;
    // }
    //
    // static @Nonnull String toString(@Nonnull SimpleKey k) {
    //     StringBuilder sb = new StringBuilder();
    //     sb.append("k:[");
    //     for (Object e : k.elements()) {
    //         sb.append(e);
    //         sb.append(",");
    //     }
    //     if (sb.length() != 3) {
    //         sb.delete(sb.length() - 1, sb.length());
    //     }
    //     sb.append("]");
    //     return sb.toString();
    // }

    private SimpleKeyBack() {
    }
}
