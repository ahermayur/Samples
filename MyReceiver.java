package lnc.paym.OTP_Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lnc.paym.CustomView.CustomAlert.CustomDialog_OTP;
import lnc.paym.Util.ErrorCode;

import static lnc.paym.Util.Util.OTP_REGEX;

public class MyReceiver extends BroadcastReceiver {
    String otp;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String sender = smsMessage.getDisplayOriginatingAddress();
            //Check the sender to filter messages which we require to read

         //   if (sender.endsWith("PAYMRC"))
            if (sender.endsWith(ErrorCode.SMS_From_Name))
            {
                String messageBody = smsMessage.getMessageBody();

                Pattern pattern = Pattern.compile(OTP_REGEX);
                Matcher matcher = pattern.matcher(messageBody);
                while (matcher.find())
                {
                    otp = matcher.group();
                }

                //Pass the otp to dialog
                if(CustomDialog_OTP.isOpen) {
                    CustomDialog_OTP.setOtp(otp);
                }
            }
        }
    }
}
