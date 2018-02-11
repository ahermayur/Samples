package lnc.paym.Data;

/**
 * Created by anupamchugh on 19/10/15.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String Ad_Table="Admin";
    public static final String Pkg_Table="Package";
    public static final String Member_Table="Member";
    public static final String User_Payment_Table="Payment";
    public static final String User_Payment_History_Table="Payment_History";
    public static final String User_Pkg_History_Table="Package_History";
    public static final String User_SMS_History_Table="SMS_History";

    // Admin Table columns
    public static final String Ad_ID = "admin_id";
    public static final String Ad_name = "name";
    public static final String Ad_email = "email";
    public static final String Ad_username = "username";
    public static final String Ad_password = "password";
    public static final String Ad_mobile = "mobile";
    public static final String Ad_address = "address";

    // Package Table Colums

    public static final String Pkg_ID = "pkg_id";
    public static final String Pkg_name = "pkg_name";
    public static final String Pkg_duration = "pkg_duration";
    public static final String Pkg_amount = "pkg_amount";
 //   public static final String Pkg_created_date = "pkg_date";

    // Member Table Colums

    public static final String M_ID = "m_id";
    public static final String M_fname = "fname";
    public static final String M_mname = "mname";
    public static final String M_lname = "lname";
    public static final String M_mobile = "mobile";
    public static final String M_email = "email";
    public static final String M_gender = "gender";
    public static final String M_joiningDate = "joiningdate";
    public static final String M_amount = "amount";
    public static final String M_status = "status";
    public static final String M_planid = "planid";
    public static final String M_dp = "image";

    // User Payment Table Colums

    public static final String P_ID = "p_id";
    public static final String P_userid = "m_id";
    public static final String P_current_paid_amoount = "current_amount";
    public static final String P_Month_amount = "amount";
    public static final String P_start_date = "start_date";
    public static final String P_end_date = "end_date";
    public static final String P_total_paid_amount = "total_amount";
    public static final String P_status = "status";
    public static final String P_pkg_amount = "pkg_amount";

    // User Payment History

    public static final String H_Id="h_id";
    public static final String H_P_ID="p_id";
    public static final String H_mode_txn="mode";
    public static final String H_amount="amount";
    public static final String H_amount_date="h_paid_date";

    // User Package History Table Contents

    public static final String P_H_Id="p_h_id";
    public static final String P_H_M_ID="m_id";
    public static final String P_H_Pkg_Name="mode";
    public static final String P_H_date="amount";

    // SMS History Table Table Contents

    public static final String S_id="s_id";
    public static final String S_M_ID="m_id";
    public static final String S_Due_Date="due_date";
    public static final String S_Status="status";

    // Database Information
    public static final String DB_NAME = "Gym.DB";

    // database version
    public static final int DB_VERSION = 24;

    // Creating table query
    private static final String CREATE_Ad_TABLE = "create table " + Ad_Table + "(" + Ad_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Ad_name + " TEXT NOT NULL, " + Ad_email + " TEXT NOT NULL , " + Ad_username + " TEXT NOT NULL, " + Ad_password + " TEXT NOT NULL," + Ad_mobile + " TEXT NOT NULL," + Ad_address + " TEXT NOT NULL);";

    private static final String CREATE_Pkg_TABLE = "create table " + Pkg_Table + "(" + Pkg_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Pkg_name + " TEXT NOT NULL, " + Pkg_duration + " INTEGER NOT NULL," + Pkg_amount + " INTEGER NOT NULL, UNIQUE("+Pkg_name+","+Pkg_duration+"));";

    private static final String CREATE_Member_TABLE = "create table " + Member_Table + "(" + M_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + M_fname + " TEXT NOT NULL, "+ M_mname+" TEXT NOT NULL, "+ M_lname + " TEXT NOT NULL, " + M_mobile + " TEXT NULL, " + M_email + " TEXT NULL, " + M_gender + " TEXT NOT NULL, " + M_joiningDate + " TEXT NOT NULL, " + M_amount + " INTEGER NOT NULL, " + M_status + " INTEGER NOT NULL," + M_planid + " TEXT NOT NULL, " + M_dp + " BLOB);";

    private static final String CREATE_User_Payment_TABLE = "create table " + User_Payment_Table + "(" + P_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + P_userid + " INTEGER NOT NULL, " + P_current_paid_amoount + " INTEGER NOT NULL, " + P_Month_amount + " INTEGER NOT NULL, " + P_start_date + " DATE NOT NULL, " + P_end_date + " DATE NOT NULL, " + P_status + " TEXT NOT NULL, " + P_total_paid_amount + " INTEGER NOT NULL,"+P_pkg_amount+" INTEGER NOT NULL,UNIQUE("+P_userid+"));";

    private static final String CREATE_User_Payment_History_TABLE = "create table " + User_Payment_History_Table + "(" + H_Id
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + H_P_ID + " INTEGER NOT NULL, " + H_mode_txn + " TEXT NOT NULL, " + H_amount + " INTEGER NOT NULL, " + H_amount_date + " DATE NOT NULL);";

    private static final String CREATE_User_Package_History_TABLE = "create table " + User_Pkg_History_Table + "(" + P_H_Id
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + P_H_M_ID + " INTEGER NOT NULL, " + P_H_Pkg_Name + " TEXT NOT NULL, " + P_H_date + " TEXT NOT NULL,UNIQUE("+P_H_M_ID+","+P_H_Pkg_Name+","+P_H_date+"));";

    private static final String Create_User_SMS_History_Table = "create table " + User_SMS_History_Table + "(" + S_id
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + S_M_ID + " INTEGER NOT NULL, " + S_Due_Date + " TEXT NOT NULL, " + S_Status + " INTEGER NOT NULL,UNIQUE("+S_M_ID+","+S_Due_Date+"));";

/*    private static final String CREATE_TABLE1 = "create table " + CHAIR_TABLE_NAME + "(" + CHAIR_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CHAIR_NAME + " TEXT NOT NULL, " + DESCRIPTION + " TEXT NOT NULL , " + PRICE + " TEXT NOT NULL, " + IMAGE + " BLOB, UNIQUE("+CHAIR_NAME+") ON CONFLICT REPLACE);";
    private static final String CREATE_TABLE2 = "create table " + CHAIR_COLOR_TABLE_NAME + "(" + COLOR_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CHAIR_ID1 + " INTEGER NOT NULL, " + COLOR + " TEXT NOT NULL );";*/

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_Ad_TABLE);
        db.execSQL(CREATE_Pkg_TABLE);
        db.execSQL(CREATE_Member_TABLE);
        db.execSQL(CREATE_User_Payment_TABLE);
        db.execSQL(CREATE_User_Payment_History_TABLE);
        db.execSQL(CREATE_User_Package_History_TABLE);
        db.execSQL(Create_User_SMS_History_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion>23)
        db.execSQL("DROP TABLE IF EXISTS " + Ad_Table);
        db.execSQL("DROP TABLE IF EXISTS " + Pkg_Table);
        db.execSQL("DROP TABLE IF EXISTS " + Member_Table);
        db.execSQL("DROP TABLE IF EXISTS " + User_Payment_Table);
        db.execSQL("DROP TABLE IF EXISTS " + User_Payment_History_Table);
        db.execSQL("DROP TABLE IF EXISTS " + User_Pkg_History_Table);
        db.execSQL("DROP TABLE IF EXISTS " + User_SMS_History_Table);
        onCreate(db);
    }
}
