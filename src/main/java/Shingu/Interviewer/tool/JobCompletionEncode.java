package Shingu.Interviewer.tool;

import java.security.MessageDigest;

public class JobCompletionEncode {
    public static String encode(String email, String password) {
        String hashPassword = null;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            String tempString = password + "slkInter";

            messageDigest.update(tempString.toString().getBytes());

            byte byteData[] = messageDigest.digest();

            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : byteData) {
                stringBuilder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            hashPassword = stringBuilder.toString();
        } catch (Exception e) {
            hashPassword = null;
        }
        return hashPassword;
    }
}
