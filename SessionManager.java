package lnc.paym.Util;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.DecimalFormat;

import lnc.paym.Data.User;
import lnc.paym.Util.Encryption.Enc_Dec;

/**
 * Created by paym on 2/3/17.
 */

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "PAYM";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    int usr_id,par_user_id;
    String name,mobno,email,password,address,status,type;
    int balance;

    //commonn data
    public static final String LastLoginTime="login_time";
    public static final String User_id = "usr_id";
    public static final String Par_user_id = "par_user_id";
    public static final String NAME = "name";
    public static final String MobileNo="mobileno";
    public static final String Alt_MobileNo="alt_mobile_no";
    public static final String EmailID="email";
    public static final String Address="address";
    public static final String Area="area";
    public static final String City="city";
    public static final String State="state";
    public static final String Pincode="pincode";
    public static final String Status="status";
    public static final String Type = "login_type";
    public static final String Balance = "balance";



    public String E_getUsr_id()
    {
        return pref.getString(User_id,Enc_Dec.enc("0"));
    }
    public int D_getUsr_id()
    {
        return Util.getInt(Enc_Dec.dec(pref.getString(User_id,null)));
    }
    public String E_getPar_user_id()
    {
        return pref.getString(Par_user_id,Enc_Dec.enc("0"));
    }
    public int D_getPar_user_id()
    {
        return Util.getInt(Enc_Dec.dec(pref.getString(Par_user_id,null)));
    }
    public float D_getBalance()
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        Float bal=Util.getFloat(Enc_Dec.dec(pref.getString(Balance,"0.0")));
        return  Float.valueOf(twoDForm.format(bal));
    }

    public String getStatus() {
        return pref.getString(Status, null);
    }

    public String getType() {
        return pref.getString(Type, null);
    }

    public String getNAME() {
        return pref.getString(NAME, "");
    }

    public String E_getEmailID() {
        return pref.getString(EmailID, null);
    }

    public String D_getEmailID() {
        return Enc_Dec.dec(pref.getString(EmailID, null));
    }

    public String getAddress() {
        return pref.getString(Address, null);
    }
    public String getArea() {
        return pref.getString(Area, null);
    }
    public String getCity() {
        return pref.getString(City, null);
    }
    public int getPincode() {
        return pref.getInt(Pincode, 0);
    }
    public String getState() {
        return pref.getString(State, null);
    }

    public String E_getMobileNo() {
        return pref.getString(MobileNo, null);
    }
    public String D_getMobileNo() {
        return Enc_Dec.dec(pref.getString(MobileNo, null));
    }
    public String getAlt_MobileNo() {
        return pref.getString(Alt_MobileNo, null);
    }

    public void setStatus(String status)
    {
        editor.putString(Status,status);
        editor.commit();
    }

    public void setType(String type)
    {
        editor.putString(Type,type);
        editor.commit();
    }

    public void setName(String name)
    {
        editor.putString(NAME,name);
        editor.commit();
    }

    public void setEmail(String email)
    {
        editor.putString(EmailID,email);
        editor.commit();
    }
    public void setAddress(String address)
    {
        editor.putString(Address,address);
        editor.commit();
    }

    public void setUser_Id(int user_id)
    {
        editor.putInt(User_id,user_id);
        editor.commit();
    }
    public void setParent_Id(int parent_id)
    {
        editor.putInt(Par_user_id,parent_id);
        editor.commit();
    }

    public void setArea(String area)
    {
        editor.putString(Area,area);
        editor.commit();
    }
    public void setCity(String city)
    {
        editor.putString(City,city);
        editor.commit();
    }
    public void setPincode(int pincode)
    {
        editor.putInt(Pincode,pincode);
        editor.commit();
    }
    public void setState(String state)
    {
        editor.putString(State,state);
        editor.commit();
    }
    public void setAlt_MobileNo(String alt_mobileNo)
    {
        editor.putString(Alt_MobileNo,alt_mobileNo);
        editor.commit();
    }
    public void setMobno(String mob_no)
    {
        editor.putString(MobileNo,mob_no);
        editor.commit();
    }
    public void setBalance(String balance)
    {
            editor.putString(Balance, balance);
            editor.commit();
    }

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public SessionManager(Context context, User user) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();


        editor.putBoolean(IS_LOGIN, true);
        editor.putString(User_id, user.E_getUsr_id());
        editor.putString(Par_user_id, user.E_getPar_user_id());
        try {
            editor.putString(Balance,user.E_getBalance());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        editor.putString(NAME, user.getName());
        editor.putString(MobileNo, user.E_getMobno());
        editor.putString(Address, user.getAddress());
        editor.putString(EmailID, user.E_getEmail());
        editor.putString(Status,user.getStatus());
        editor.putString(Alt_MobileNo, user.getAlt_mobno());
        editor.putString(Area, user.getArea());
        editor.putString(City, user.getCity());
        editor.putString(State, user.getState());
        try {
            setPincode(Integer.parseInt(user.getPincode()));
        }
        catch (Exception e)
        {

        }
        editor.putString(Type, user.getType());
        editor.putLong(LastLoginTime, System.currentTimeMillis());
        editor.commit();
    }

    public void logoutUser()
    {
        String mob_no="";
        try {
            mob_no = E_getMobileNo();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        editor.clear();
        editor.putString(MobileNo,mob_no);
        editor.commit();
    }

    public boolean isLoggedIn(){
      //  10,800,000
        long lasttime=pref.getLong(LastLoginTime, System.currentTimeMillis());
        lasttime=lasttime+10800000;
        long currenttime= System.currentTimeMillis();
        if(currenttime<=lasttime)
        {
            editor.putLong(LastLoginTime, System.currentTimeMillis());
            editor.commit();
            return pref.getBoolean(IS_LOGIN, false);
        }
        else
        {
            // logout user if he is not active in last 3 hour
            logoutUser();
        }

        return false;
    }
}
