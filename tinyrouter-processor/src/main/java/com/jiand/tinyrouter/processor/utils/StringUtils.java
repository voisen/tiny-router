package com.jiand.tinyrouter.processor.utils;


/**
 * @author jiand
 */
public class StringUtils {
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz";

    public static String randomClassSuffix(int len){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int randomInt = (int) (Math.random() * 1000);
            int idx = randomInt % CHARS.length();
            if (stringBuilder.length() == 0){
                String str = String.valueOf(CHARS.charAt(idx));
                stringBuilder.append(str.toUpperCase());
            }else{
                stringBuilder.append(CHARS.charAt(idx));
            }
        }
        return stringBuilder.toString();
    }

}
