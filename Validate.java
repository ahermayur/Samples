package lnc.paym.Util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LNC on 24-10-2017.
 */

public class Validate {
    public static final String PINCODE_PATTERN = "\\d{6})";
    public static final String CONTACT_PATTERN = "^[789]\\d{9}$";
    //  public static final String OTP_REGEX = "[0-9]{1,4}";
    public static final String OTP_REGEX = "\\b\\d{4}\\b";
    public static final String IFSC_REGX="^[A-Za-z]{4}\\d{7}$";
    public static String EMAIL_PATTERN = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+";
    public static String Date_REGS="^((((0?[1-9]|[12]\\d|3[01])[\\.\\-\\/](0?[13578]|1[02])[\\.\\-\\/]((1[6-9]|[2-9]\\d)?\\d{2}))|((0?[1-9]|[12]\\d|30)[\\.\\-\\/](0?[13456789]|1[012])[\\.\\-\\/]((1[6-9]|[2-9]\\d)?\\d{2}))|((0?[1-9]|1\\d|2[0-8])[\\.\\-\\/]0?2[\\.\\-\\/]((1[6-9]|[2-9]\\d)?\\d{2}))|(29[\\.\\-\\/]0?2[\\.\\-\\/]((1[6-9]|[2-9]\\d)?(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00)|00)))|(((0[1-9]|[12]\\d|3[01])(0[13578]|1[02])((1[6-9]|[2-9]\\d)?\\d{2}))|((0[1-9]|[12]\\d|30)(0[13456789]|1[012])((1[6-9]|[2-9]\\d)?\\d{2}))|((0[1-9]|1\\d|2[0-8])02((1[6-9]|[2-9]\\d)?\\d{2}))|(2902((1[6-9]|[2-9]\\d)?(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00)|00))))$ ";
    public static boolean isEmptyorNull(String input)
    {
        if(input==null || input.isEmpty())
            return true;
        else
            return false;
    }

    public static boolean isValidMobileNumber(String number)
    {
        Pattern pattern=null;
        Matcher matcher;
        pattern=pattern.compile(CONTACT_PATTERN);
        matcher=pattern.matcher(number);
        if(matcher.matches())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isValidIFSC(String ifsc)
    {
        Pattern pattern=null;
        Matcher matcher;
        pattern=pattern.compile(IFSC_REGX);
        matcher=pattern.matcher(ifsc);
        if(matcher.matches())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean isValidDate(String date)
    {
        Pattern pattern=null;
        Matcher matcher;
        pattern=pattern.compile(Date_REGS);
        matcher=pattern.matcher(date);
        if(matcher.matches())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
