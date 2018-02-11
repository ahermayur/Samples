package lnc.paym.Data;

/**
 * Created by anupamchugh on 19/10/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
        DatabaseHelper db = new DatabaseHelper(context);
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long addAdmin(int id, String Ad_name, String Ad_email, String Ad_username, String Ad_password, String Ad_mobile, String Ad_address)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.Ad_ID, id);
        contentValue.put(DatabaseHelper.Ad_name, Ad_name);
        contentValue.put(DatabaseHelper.Ad_email, Ad_email);
        contentValue.put(DatabaseHelper.Ad_username, Ad_username);
        contentValue.put(DatabaseHelper.Ad_password, Ad_password);
        contentValue.put(DatabaseHelper.Ad_mobile, Ad_mobile);
        contentValue.put(DatabaseHelper.Ad_address, Ad_address);
        long rowInserted = -1;
        rowInserted=database.insert(DatabaseHelper.Ad_Table, null, contentValue);
        return rowInserted;
    }
    public long addAdmin(String Ad_name, String Ad_email, String Ad_username, String Ad_password, String Ad_mobile, String Ad_address)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.Ad_name, Ad_name);
        contentValue.put(DatabaseHelper.Ad_email, Ad_email);
        contentValue.put(DatabaseHelper.Ad_username, Ad_username);
        contentValue.put(DatabaseHelper.Ad_password, Ad_password);
        contentValue.put(DatabaseHelper.Ad_mobile, Ad_mobile);
        contentValue.put(DatabaseHelper.Ad_address, Ad_address);
        long rowInserted = -1;
        rowInserted=database.insert(DatabaseHelper.Ad_Table, null, contentValue);
        return rowInserted;
    }

    // Add Member

    public long addMember(int id, String M_fname, String M_mname, String M_lname, String M_mobile, String M_email, String M_gender, String M_joiningDate, int M_amount, String M_status, int M_planid, byte[] M_dp)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.M_ID, id);
        contentValue.put(DatabaseHelper.M_fname, M_fname);
        contentValue.put(DatabaseHelper.M_mname, M_mname);
        contentValue.put(DatabaseHelper.M_lname, M_lname);
        contentValue.put(DatabaseHelper.M_mobile, M_mobile);
        contentValue.put(DatabaseHelper.M_email, M_email);
        contentValue.put(DatabaseHelper.M_gender, M_gender);
        contentValue.put(DatabaseHelper.M_joiningDate, M_joiningDate);
        contentValue.put(DatabaseHelper.M_amount, M_amount);
        contentValue.put(DatabaseHelper.M_status, M_status);
        contentValue.put(DatabaseHelper.M_planid, M_planid);
        contentValue.put(DatabaseHelper.M_dp, M_dp);
        long rowInserted = -1;
        rowInserted=database.insert(DatabaseHelper.Member_Table, null, contentValue);
        return rowInserted;
    }


    public long addMember(String M_fname, String M_mname, String M_lname, String M_mobile, String M_email, String M_gender, String M_joiningDate, int M_amount, String M_status, int M_planid, byte[] M_dp)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.M_fname, M_fname);
        contentValue.put(DatabaseHelper.M_mname, M_mname);
        contentValue.put(DatabaseHelper.M_lname, M_lname);
        contentValue.put(DatabaseHelper.M_mobile, M_mobile);
        contentValue.put(DatabaseHelper.M_email, M_email);
        contentValue.put(DatabaseHelper.M_gender, M_gender);
        contentValue.put(DatabaseHelper.M_joiningDate, M_joiningDate);
        contentValue.put(DatabaseHelper.M_amount, M_amount);
        contentValue.put(DatabaseHelper.M_status, M_status);
        contentValue.put(DatabaseHelper.M_planid, M_planid);
        contentValue.put(DatabaseHelper.M_dp, M_dp);
        long rowInserted = -1;
        rowInserted=database.insert(DatabaseHelper.Member_Table, null, contentValue);
        return rowInserted;
    }

    // Add Transaction into db

    public long addTransaction(int id, int P_userid, int P_current_paid_amoount, int P_Month_amount, String P_start_date, String P_end_date, String P_status, int P_total_paid_amount, int P_pkg_amount)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.P_ID, id);
        contentValue.put(DatabaseHelper.P_userid, P_userid);
        contentValue.put(DatabaseHelper.P_current_paid_amoount, P_current_paid_amoount);
        contentValue.put(DatabaseHelper.P_Month_amount, P_Month_amount);
        contentValue.put(DatabaseHelper.P_start_date,P_start_date );
        contentValue.put(DatabaseHelper.P_end_date, P_end_date);
        contentValue.put(DatabaseHelper.P_status, P_status);
        contentValue.put(DatabaseHelper.P_total_paid_amount, P_total_paid_amount);
        contentValue.put(DatabaseHelper.P_pkg_amount, P_pkg_amount);
        long rowInserted = -1;
        rowInserted=database.insert(DatabaseHelper.User_Payment_Table, null, contentValue);
        return rowInserted;
    }

    public long addPackageHistory(int P_H_M_ID, String P_H_Pkg_Name, String P_H_date)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.P_H_M_ID, P_H_M_ID);
        contentValue.put(DatabaseHelper.P_H_Pkg_Name, P_H_Pkg_Name);
        contentValue.put(DatabaseHelper.P_H_date, P_H_date);
        long rowInserted = -1;
        rowInserted=database.insert(DatabaseHelper.User_Pkg_History_Table, null, contentValue);
        return rowInserted;
    }
    public long addTransaction(int P_userid, int P_current_paid_amoount, int P_Month_amount, String P_start_date, String P_end_date, String P_status, int P_total_paid_amount, int P_pkg_amount)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.P_userid, P_userid);
        contentValue.put(DatabaseHelper.P_current_paid_amoount, P_current_paid_amoount);
        contentValue.put(DatabaseHelper.P_Month_amount, P_Month_amount);
        contentValue.put(DatabaseHelper.P_start_date,P_start_date );
        contentValue.put(DatabaseHelper.P_end_date, P_end_date);
        contentValue.put(DatabaseHelper.P_status, P_status);
        contentValue.put(DatabaseHelper.P_total_paid_amount, P_total_paid_amount);
        contentValue.put(DatabaseHelper.P_pkg_amount, P_pkg_amount);
        long rowInserted = -1;
        rowInserted=database.insert(DatabaseHelper.User_Payment_Table, null, contentValue);
        return rowInserted;
    }

    //Add Transaction History

    public long addTransactionHistory(int id, int H_P_ID, String H_mode_txn, int H_amount, String H_amount_date)
    {
        ContentValues contentValue = new ContentValues();
        //contentValue.put(DatabaseHelper.H_Id, H_Id);
        contentValue.put(DatabaseHelper.H_Id,id);
        contentValue.put(DatabaseHelper.H_P_ID,H_P_ID);
        contentValue.put(DatabaseHelper.H_mode_txn,H_mode_txn );
        contentValue.put(DatabaseHelper.H_amount,H_amount);
        contentValue.put(DatabaseHelper.H_amount_date,H_amount_date);
        long rowInserted = -1;
        rowInserted=database.insert(DatabaseHelper.User_Payment_History_Table, null, contentValue);
        return rowInserted;
    }

    public long addTransactionHistory(int H_P_ID, String H_mode_txn, int H_amount, String H_amount_date)
    {
        ContentValues contentValue = new ContentValues();
        //contentValue.put(DatabaseHelper.H_Id, H_Id);
        contentValue.put(DatabaseHelper.H_P_ID,H_P_ID);
        contentValue.put(DatabaseHelper.H_mode_txn,H_mode_txn );
        contentValue.put(DatabaseHelper.H_amount,H_amount);
        contentValue.put(DatabaseHelper.H_amount_date,H_amount_date);
        long rowInserted = -1;
        rowInserted=database.insert(DatabaseHelper.User_Payment_History_Table, null, contentValue);
        return rowInserted;
    }
    /*
      public static final String P_ID = "p_id";
    public static final String P_userid = "m_id";
    public static final String P_current_paid_amoount = "current_amount";
    public static final String P_Month_amount = "amount";
    public static final String P_start_date = "start_date";
    public static final String P_end_date = "end_date";
    public static final String P_total_paid_amount = "total_amount";
    public static final String P_status = "status";
     */

    public  long addTransactionPayment(int pid, int p_uid, int CurrentPaidAmt, int pMonthAmt, String p_startDate, String p_endDate, int p_totalAmt, String status, int P_pkg_amount)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(DatabaseHelper.P_ID,pid);
        contentValues.put(DatabaseHelper.P_userid,p_uid);
        contentValues.put(DatabaseHelper.P_current_paid_amoount,CurrentPaidAmt);
        contentValues.put(DatabaseHelper.P_Month_amount,pMonthAmt);
        contentValues.put(DatabaseHelper.P_start_date,p_startDate);
        contentValues.put(DatabaseHelper.P_end_date,p_endDate);
        contentValues.put(DatabaseHelper.P_total_paid_amount,p_totalAmt);
        contentValues.put(DatabaseHelper.P_status,status);
        contentValues.put(DatabaseHelper.P_pkg_amount,P_pkg_amount);
        long rowInserted = -1;

        rowInserted=database.insert(DatabaseHelper.User_Payment_Table, null, contentValues);
        return rowInserted;


    }


    // Add Package into DB

    public long addPackage(int id, String Pkg_name, int Pkg_duration, int Pkg_amount)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.Pkg_ID, id);
        contentValue.put(DatabaseHelper.Pkg_name, Pkg_name);
        contentValue.put(DatabaseHelper.Pkg_duration, Pkg_duration);
        contentValue.put(DatabaseHelper.Pkg_amount, Pkg_amount);
        //   contentValue.put(DatabaseHelper.Pkg_created_date, Pkg_created_date);
        try {
            long rowInserted = -1;
            rowInserted=database.insertOrThrow(DatabaseHelper.Pkg_Table, null, contentValue);
            return rowInserted;
        }
        catch (SQLiteConstraintException e) {
            // TODO Auto-generated catch block
      //      MyDynamicToast.errorMessage(context,"Same Package Already Available");
            System.out.println(e);
        }
        return -1;
    }

    public long addPackage(String Pkg_name, int Pkg_duration, int Pkg_amount, String Pkg_created_date)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.Pkg_name, Pkg_name);
        contentValue.put(DatabaseHelper.Pkg_duration, Pkg_duration);
        contentValue.put(DatabaseHelper.Pkg_amount, Pkg_amount);
     //   contentValue.put(DatabaseHelper.Pkg_created_date, Pkg_created_date);
        try {
        long rowInserted = -1;
        rowInserted=database.insert(DatabaseHelper.Pkg_Table, null, contentValue);
        return rowInserted;
    }
        catch (SQLiteConstraintException e) {
        // TODO Auto-generated catch block
        System.out.println(e);
    }
        return -1;
    }

    // SMS Add
    public long addSMSHistory(int S_M_ID, String S_Due_Date, int S_Status)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.S_M_ID, S_M_ID);
        contentValue.put(DatabaseHelper.S_Due_Date, S_Due_Date);
        contentValue.put(DatabaseHelper.S_Status, S_Status);
        //   contentValue.put(DatabaseHelper.Pkg_created_date, Pkg_created_date);
        try {
            long rowInserted = -1;
            rowInserted=database.insert(DatabaseHelper.User_SMS_History_Table, null, contentValue);
            return rowInserted;
        }
        catch (SQLiteConstraintException e) {
            // TODO Auto-generated catch block
            System.out.println(e);
        }
        return -1;
    }

    //Read SMS History Details
    public int getSMSHistory(int id,String S_Due_Date)
    {
        Cursor cursor = database.rawQuery("Select * from "+DatabaseHelper.User_SMS_History_Table+" where "+DatabaseHelper.S_id+" = "+id+" and "+DatabaseHelper.S_Due_Date+"='"+S_Due_Date+"'", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor.getCount();
    }



    // Read Admin Details
    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper.Ad_ID, DatabaseHelper.Ad_name, DatabaseHelper.Ad_email,DatabaseHelper.Ad_username,DatabaseHelper.Ad_password,DatabaseHelper.Ad_mobile,DatabaseHelper.Ad_address };
        Cursor cursor = database.query(DatabaseHelper.Ad_Table, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    // Read Member Details
    public Cursor getMembers()
    {
        String[] columns = new String[] { DatabaseHelper.M_ID,DatabaseHelper.M_fname,DatabaseHelper.M_mname,DatabaseHelper.M_lname,DatabaseHelper.M_mobile,DatabaseHelper.M_email,DatabaseHelper.M_gender,DatabaseHelper.M_joiningDate,DatabaseHelper.M_amount,DatabaseHelper.M_status,DatabaseHelper.M_planid,DatabaseHelper.M_dp };
        Cursor cursor = database.query(DatabaseHelper.Member_Table, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    //Read Package History Details
    public Cursor getPkgHistory(int id)
    {
        Cursor cursor = database.rawQuery("Select * from "+DatabaseHelper.User_Pkg_History_Table+" where "+DatabaseHelper.P_H_M_ID+" = "+id+" ", null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    // Read Transaction Details (Package Subscription)

    public Cursor getTransaction()
    {
        String[] columns = new String[] { DatabaseHelper.P_ID,DatabaseHelper.P_userid,DatabaseHelper.P_current_paid_amoount,DatabaseHelper.P_Month_amount,DatabaseHelper.P_start_date,DatabaseHelper.P_end_date,DatabaseHelper.P_total_paid_amount,DatabaseHelper.P_status,DatabaseHelper.P_pkg_amount};
        Cursor cursor = database.query(DatabaseHelper.User_Payment_Table, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getTransaction1(int id)
    {
        Cursor cursor = database.rawQuery("Select * from "+DatabaseHelper.User_Payment_Table+" where "+DatabaseHelper.P_userid+"="+id,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    public Cursor getMember(String mob)
    {
        String[] columns = new String[] { DatabaseHelper.M_ID,DatabaseHelper.M_fname,DatabaseHelper.M_mname,DatabaseHelper.M_lname,DatabaseHelper.M_mobile,DatabaseHelper.M_email,DatabaseHelper.M_gender,DatabaseHelper.M_joiningDate,DatabaseHelper.M_amount,DatabaseHelper.M_status,DatabaseHelper.M_planid,DatabaseHelper.M_dp };
        Cursor cursor=database.rawQuery("select * from "+DatabaseHelper.Member_Table+" where "+DatabaseHelper.M_mobile+"='"+mob+"'",null);
//        Cursor cursor = database.query(DatabaseHelper.Member_Table, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    public Cursor getMemberId(int id)
    {
        String[] columns = new String[] { DatabaseHelper.M_ID,DatabaseHelper.M_fname,DatabaseHelper.M_mname,DatabaseHelper.M_lname,DatabaseHelper.M_mobile,DatabaseHelper.M_email,DatabaseHelper.M_gender,DatabaseHelper.M_joiningDate,DatabaseHelper.M_amount,DatabaseHelper.M_status,DatabaseHelper.M_planid,DatabaseHelper.M_dp };
        Cursor cursor=database.rawQuery("select * from "+DatabaseHelper.Member_Table+" where "+DatabaseHelper.M_ID+"="+id+"",null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;

    }

    public String getMemberPaymentStatus(int id)
    {
        String[] columns = new String[] { DatabaseHelper.P_ID,DatabaseHelper.P_userid,DatabaseHelper.P_current_paid_amoount,DatabaseHelper.P_Month_amount,DatabaseHelper.P_start_date,DatabaseHelper.P_end_date,DatabaseHelper.P_total_paid_amount,DatabaseHelper.P_status};
        Cursor cursor=database.rawQuery("select * from "+DatabaseHelper.User_Payment_Table+" where "+DatabaseHelper.P_userid+"="+id+"",null);
//        Cursor cursor = database.query(DatabaseHelper.Member_Table, columns, null, null, null, null, null);
       String s="pending";
        if(cursor.getCount()==0)
        {
            s="none";
        }
        if (cursor != null) {
            cursor.moveToFirst();
            try {
                s = cursor.getString(cursor.getColumnIndex(DatabaseHelper.P_status));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
             return s;
        }
        return s;
    }

    public String getMemberDueDate(int id)
    {
        String[] columns = new String[] { DatabaseHelper.P_ID,DatabaseHelper.P_userid,DatabaseHelper.P_current_paid_amoount,DatabaseHelper.P_Month_amount,DatabaseHelper.P_start_date,DatabaseHelper.P_end_date,DatabaseHelper.P_total_paid_amount,DatabaseHelper.P_status};
        Cursor cursor=database.rawQuery("select * from "+DatabaseHelper.User_Payment_Table+" where "+DatabaseHelper.P_userid+"="+id+"",null);
//        Cursor cursor = database.query(DatabaseHelper.Member_Table, columns, null, null, null, null, null);
        String s="";
        if(cursor.getCount()==0)
        {
            s="";
        }
        if (cursor != null) {
            cursor.moveToFirst();
            try {
                s = cursor.getString(cursor.getColumnIndex(DatabaseHelper.P_end_date));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return s;
        }
        return s;
    }



    //Read All Packages
    public Cursor getPackages() {
        String[] columns = new String[] { DatabaseHelper.Pkg_ID, DatabaseHelper.Pkg_name, DatabaseHelper.Pkg_duration,DatabaseHelper.Pkg_amount };
  //      String[] columns = new String[] { DatabaseHelper.Pkg_ID, DatabaseHelper.Pkg_name, DatabaseHelper.Pkg_duration,DatabaseHelper.Pkg_amount,DatabaseHelper.Pkg_created_date };
        Cursor cursor = database.query(DatabaseHelper.Pkg_Table, columns, null, null, null, null, null);
      //  Cursor cursor=database.rawQuery("select * from "+DatabaseHelper.Member_Table+" where "+DatabaseHelper.M_ID+"='"+id+"'",null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }


    public Cursor getHistoryId(int id)
    {
        Cursor cursor=database.rawQuery("select * from "+DatabaseHelper.User_Payment_History_Table+" where "+DatabaseHelper.H_P_ID+"="+id+"",null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getPaymentId(int id)
    {
        Cursor cursor=database.rawQuery("select * from "+DatabaseHelper.User_Payment_Table+" where "+DatabaseHelper.P_userid+"="+id+"",null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public Cursor getPackagesId(int id) {
        String[] columns = new String[] { DatabaseHelper.Pkg_ID, DatabaseHelper.Pkg_name, DatabaseHelper.Pkg_duration,DatabaseHelper.Pkg_amount };
        //      String[] columns = new String[] { DatabaseHelper.Pkg_ID, DatabaseHelper.Pkg_name, DatabaseHelper.Pkg_duration,DatabaseHelper.Pkg_amount,DatabaseHelper.Pkg_created_date };
       // Cursor cursor = database.query(DatabaseHelper.Pkg_Table, columns, null, null, null, null, null);
         Cursor cursor=database.rawQuery("select * from "+DatabaseHelper.Pkg_Table+" where "+DatabaseHelper.Pkg_ID+"='"+id+"'",null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    // Update Admin Details
    public int updateAdmin(long id, String Ad_name, String Ad_email, String Ad_username, String Ad_password, String Ad_mobile, String Ad_address) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.Ad_name, Ad_name);
        contentValue.put(DatabaseHelper.Ad_email, Ad_email);
        contentValue.put(DatabaseHelper.Ad_username, Ad_username);
        contentValue.put(DatabaseHelper.Ad_password, Ad_password);
        contentValue.put(DatabaseHelper.Ad_mobile, Ad_mobile);
        contentValue.put(DatabaseHelper.Ad_address, Ad_address);
        int i = database.update(DatabaseHelper.Ad_Table, contentValue, DatabaseHelper.Ad_ID + " = " + id, null);
        return i;
    }

    //update Transaction
    public int updateTransaction(long id, int P_userid, int P_current_paid_amoount, int P_Month_amount, String P_start_date, String P_end_date, int P_total_paid_amount, String P_status ) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.P_userid, P_userid);
        contentValue.put(DatabaseHelper.P_current_paid_amoount, P_current_paid_amoount);
        contentValue.put(DatabaseHelper.P_Month_amount, P_Month_amount);
        contentValue.put(DatabaseHelper.P_start_date, P_start_date);
        contentValue.put(DatabaseHelper.P_end_date, P_end_date);
        contentValue.put(DatabaseHelper.P_total_paid_amount, P_total_paid_amount);
        contentValue.put(DatabaseHelper.P_status, P_status);

        int i = database.update(DatabaseHelper.User_Payment_Table, contentValue, DatabaseHelper.P_ID + " = " + id, null);
        return i;
    }

    /*
        public static final String P_userid = "m_id";
    public static final String P_current_paid_amoount = "current_amount";
    public static final String P_Month_amount = "amount";
    public static final String P_start_date = "start_date";
    public static final String P_end_date = "end_date";
    public static final String P_total_paid_amount = "total_amount";
    public static final String P_status = "status";

     */


    //update Member
    public int updateMember(long id, String M_fname, String M_mname, String M_lname, String M_mobile, String M_email, String M_gender, String M_joiningDate, String M_status, byte[] M_dp) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.M_fname, M_fname);
        contentValue.put(DatabaseHelper.M_mname, M_mname);
        contentValue.put(DatabaseHelper.M_lname, M_lname);
        contentValue.put(DatabaseHelper.M_mobile, M_mobile);
        contentValue.put(DatabaseHelper.M_email, M_email);
        contentValue.put(DatabaseHelper.M_gender, M_gender);
        contentValue.put(DatabaseHelper.M_joiningDate, M_joiningDate);
        contentValue.put(DatabaseHelper.M_status, M_status);
        contentValue.put(DatabaseHelper.M_dp,M_dp);
        int i = database.update(DatabaseHelper.Member_Table, contentValue, DatabaseHelper.M_ID + " = " + id, null);
        return i;
    }

    public int updateMember(long id, String M_fname, String M_mname, String M_lname, String M_mobile, String M_email, String M_gender, String M_joiningDate, String M_status) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.M_fname, M_fname);
        contentValue.put(DatabaseHelper.M_mname, M_mname);
        contentValue.put(DatabaseHelper.M_lname, M_lname);
        contentValue.put(DatabaseHelper.M_mobile, M_mobile);
        contentValue.put(DatabaseHelper.M_email, M_email);
        contentValue.put(DatabaseHelper.M_gender, M_gender);
        contentValue.put(DatabaseHelper.M_joiningDate, M_joiningDate);
        contentValue.put(DatabaseHelper.M_status, M_status);
        int i = database.update(DatabaseHelper.Member_Table, contentValue, DatabaseHelper.M_ID + " = " + id, null);
        return i;
    }

    public int updatePlan(long id, int plan_id) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.M_planid,plan_id);
        int i = database.update(DatabaseHelper.Member_Table, contentValue, DatabaseHelper.M_ID + " = " + id, null);
        return i;
    }

    //update Payment
    public int updatePayment(long id,int currentPaidAmt,int totalPaidAmt,String status)
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(DatabaseHelper.P_current_paid_amoount,currentPaidAmt);
        contentValues.put(DatabaseHelper.P_total_paid_amount,totalPaidAmt);
        contentValues.put(DatabaseHelper.P_status,status);

        int i=database.update(DatabaseHelper.User_Payment_Table,contentValues,DatabaseHelper.P_ID+"=" + id,null);
        return i;
    }

    // Update Package
    public int updatePackage(long id, String Pkg_name, int Pkg_duration, int Pkg_amount, String Pkg_created_date) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.Pkg_name, Pkg_name);
        contentValue.put(DatabaseHelper.Pkg_duration, Pkg_duration);
        contentValue.put(DatabaseHelper.Pkg_amount, Pkg_amount);
    //    contentValue.put(DatabaseHelper.Pkg_created_date, Pkg_created_date);
        int i = database.update(DatabaseHelper.Pkg_Table, contentValue, DatabaseHelper.Pkg_ID + " = " + id, null);
        return i;
    }
    //delete Member
    public void deleteTxn(long id)
    {
        try {
            database.delete(DatabaseHelper.User_Payment_History_Table, DatabaseHelper.H_P_ID + "=" + getTxnId(id), null);
        }
        catch(Exception E)
        {
            E.printStackTrace();
        }
        try {
            database.delete(DatabaseHelper.User_Payment_Table,DatabaseHelper.P_userid + "=" + id,null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public boolean delete(String table, String where) {
        open();
        long index = database.delete(table, where, null);
        close();
        return index > 0;
    }
    public void deleteMember(long id) {
       try {
           database.delete(DatabaseHelper.User_Payment_History_Table, DatabaseHelper.H_P_ID + "=" + getTxnId(id), null);
       }
       catch(Exception E)
        {
            E.printStackTrace();
        }
        try {
            database.delete(DatabaseHelper.User_Payment_Table,DatabaseHelper.P_userid + "=" + id,null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try {
            database.delete(DatabaseHelper.Member_Table, DatabaseHelper.M_ID + "=" + id, null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String getTxnId(long id) {
        Cursor cursor = database.rawQuery("Select * from "+DatabaseHelper.User_Payment_Table+" where "+DatabaseHelper.P_userid+"="+id,null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        cursor.moveToFirst();
        if (cursor == null || cursor.getCount()==0) {
            //    MyDynamicToast.informationMessage(getActivity(), "Member is Not Available");
        }
        if (cursor != null) {
            for(int i=0;i<1;i++)
            {
                try {
                    return ""+cursor.getInt(cursor.getColumnIndex(DatabaseHelper.P_ID));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                cursor.moveToNext();
            }
        }
        return "0";
    }

    // Delete Package
    public void deletePackage(long id) {
        database.delete(DatabaseHelper.Pkg_Table, DatabaseHelper.Pkg_ID + "=" + id, null);
    }
}
