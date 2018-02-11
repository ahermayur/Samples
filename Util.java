package lnc.paym.Util;

/**
 * Created by LNC on 05-09-2017.
 */

public class Util {
    // Time out Time
    public static final long Time_Out_OTP_Resend=90000;
    public static final long Time_Out2=9000;
    public static final long Time_Out=10800000;
    public static final String PINCODE_PATTERN = "\\d{6})";
    public static final String CONTACT_PATTERN = "^[789]\\d{9}$";
  //  public static final String OTP_REGEX = "[0-9]{1,4}";
    public static final String OTP_REGEX = "\\b\\d{"+ErrorCode.OTP_LENGTH+"}\\b";
    public static String EMAIL_PATTERN = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+";


    //auto op select

    // Operator

    public static final int AIRTEL=0;
    public static final int IDEA=1;
    public static final int RELIANCE=2;
    public static final int VODAFONE=3;
    public static final int AIRCEL=4;
    public static final int MTNL=5;
    public static final int LOOP=6;
    public static final int MTS=7;
    public static final int BSNL=8;
    public static final int DOCOMO=9;
    public static final int INDICOM=10;
    public static final int UNINOR=11;
    public static final int VIDEOCON=12;
    public static final int JIO=13;

    // Circles

    public static final int KARNATAKA=0;
    public static final int MAHARASHTRA=1;
    public static final int GUJARAT=2;
    public static final int BIHAR=3;
    public static final int JAMMU=4;
    public static final int MADHYA_PRADESH=5;
    public static final int ANDHRA_PRADESH=6;
    public static final int DELHI=7;
    public static final int PUNJAB=8;
    public static final int MUMBAI=9;
    public static final int WEST_BENGAL=10;
    public static final int RAJASTHAN=11;
    public static final int ODISHA=12;
    public static final int CHENNAI=13;
    public static final int TAMILNADU=14;
    public static final int KERALA=15;
    public static final int ASSAM=16;
    public static final int HARYANA=17;
    public static final int HIMACHAL_PRADESH=18;
    public static final int NORTH_EAST=19;
    public static final int UTTAR_PRADESH_E=20;
    public static final int UTTAR_PRADESH_W_UTTARAKHAND=21;



    public static class operator
    {
        String no;
        String operator;
        String circle;
        public operator()
        {

        }
        public operator(String no, String operator, String circle) {
            this.no=no;
            this.operator = operator;
            this.circle = circle;
        }

        public String getNo() {
            return no;
        }

        public String getOperator() {
            String op="";
            switch (operator)
            {
                case "0":
                    op="AIRTEL";
                    break;
                case "1":
                    op="IDEA";
                    break;
                case "2":
                    op="RELIANCE";
                    break;
                case "3":
                    op="VODAFONE";
                    break;
                case "4":
                    op="AIRCEL";
                    break;
                case "5":
                    op="MTNL";
                    break;
                case "6":
                    op="LOOP";
                    break;
                case "7":
                    op="MTS";
                    break;
                case "8":
                    op="BSNL";
                    break;
                case "9":
                    op="DOCOMO";
                    break;
                case "10":
                    op="INDICOM";
                    break;
                case "11":
                    op="UNINOR";
                    break;
                case "12":
                    op="VIDEOCON";
                    break;
                case "13":
                    op="JIO";
                    break;
                default:
                    op="";
            }
            return op;
        }

        public String getCircle() {
            String cir="";
            switch (circle)
            {
                case "0":
                    cir="Karnataka";
                    break;
                case "1":
                    cir="Maharashtra & Goa";
                    break;
                case "2":
                    cir="Gujarat";
                    break;
                case "3":
                    cir="Bihar";
                    break;
                case "4":
                    cir="Jammu & Kashmir";
                    break;
                case "5":
                    cir="Madhya Pradesh & Chhattisgarh";
                    break;
                case "6":
                    cir="Andhra Pradesh";
                    break;
                case "7":
                    cir="Delhi";
                    break;
                case "8":
                    cir="Punjab";
                    break;
                case "9":
                    cir="Mumbai";
                    break;
                case "10":
                    cir="West Bengal";
                    break;
                case "11":
                    cir="Rajasthan";
                    break;
                case "12":
                    cir="Orissa";
                    break;
                case "13":
                    cir="Chennai";
                    break;
                case "14":
                    cir="Tamil Nadu";
                    break;
                case "15":
                    cir="Kerala";
                    break;
                case "16":
                    cir="Assam";
                    break;
                case "17":
                    cir="Haryana";
                    break;
                case "18":
                    cir="Himachal Pradesh";
                    break;
                case "19":
                    cir="North East";
                    break;
                case "20":
                    cir="Uttar Pradesh - East";
                    break;
                case "21":
                    cir="Uttar Pradesh - West";
                    break;
                case "22":
                    cir="Jharkhand";
                    break;
                case "23":
                    cir="Kolkata";
                    break;
                case "24":
                    cir="Karnataka";
                    break;
            }
            return cir;
        }
    }

    public static Float getFloat(String s)
    {
        if(!Validate.isEmptyorNull(s)) {
            try {
                return Float.parseFloat(s);
            } catch (Exception e) {
                return 0f;
            }
        }
        else
            return  0f;
    }

    public static int getInt(String s)
    {
        if(!Validate.isEmptyorNull(s)) {
            try {
                return Integer.parseInt(s);
            } catch (Exception e) {
                return 0;
            }
        }
        else
            return  0;
    }

    public static String getFirstLetterCapital(String source)
    {

        try {


            StringBuffer res = new StringBuffer();

            String[] strArr = source.split(" ");
            for (String str : strArr) {
                try {
                    char[] stringArray = str.trim().toCharArray();
                    stringArray[0] = Character.toUpperCase(stringArray[0]);
                    str = new String(stringArray);

                    res.append(str).append(" ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return res.toString().trim();
        }
        catch (Exception e)
        {
            return source;
        }
    }

}
