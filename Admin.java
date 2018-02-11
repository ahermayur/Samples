package mayur.lnc.gymmgtapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.desai.vatsal.mydynamictoast.MyDynamicToast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mayur.lnc.gymmgtapp.Database.DBManager;
import mayur.lnc.gymmgtapp.Database.DatabaseHelper;

public class Admin extends AppCompatActivity {
    EditText et_gym_name,et_name,et_mob,et_username,et_password,et_address;
    String gym_name,name,mobile_no,username,password,address;
    int id;
    Button btn_save,btn_cancel;
    DBManager dbManager;
    String CONTACT_PATTERN = "^[789]\\d{9}$";
    Boolean exist=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        dbManager=new DBManager(Admin.this);
        et_gym_name=(EditText)findViewById(R.id.et_gym_name);
        et_name=(EditText)findViewById(R.id.et_name);
        et_mob=(EditText)findViewById(R.id.et_mob);
        et_username=(EditText)findViewById(R.id.et_username);
        et_password=(EditText)findViewById(R.id.et_password);
        et_address=(EditText)findViewById(R.id.et_address);
        btn_save=(Button) findViewById(R.id.btn_save);
        btn_cancel=(Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Admin.this,Login.class));
                finish();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exist==false)
                {
                    if(validate())
                    {
                        dbManager.open();
                        dbManager.addAdmin(name,gym_name,username,password,mobile_no,address);
                        dbManager.close();
                        MyDynamicToast.successMessage(Admin.this,"Admin Details Saved");
                    }
                }
                else
                {
                    //update
                    if(validate()) {
                        dbManager.open();
                        dbManager.updateAdmin(id, name, gym_name, username, password, mobile_no, address);
                        dbManager.close();
                        MyDynamicToast.successMessage(Admin.this, "Admin Details Updated");
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbManager.open();
        Cursor cursor=dbManager.fetch();
        cursor.moveToFirst();
        if (cursor == null || cursor.getCount()==0) {
            exist=false;
        }
        else {
            exist=true;
            for(int i=0;i<cursor.getCount();i++)
            {
                id=cursor.getInt(cursor.getColumnIndex(DatabaseHelper.Ad_ID));
                name=cursor.getString(cursor.getColumnIndex(DatabaseHelper.Ad_name));
                username=cursor.getString(cursor.getColumnIndex(DatabaseHelper.Ad_username));
                password=cursor.getString(cursor.getColumnIndex(DatabaseHelper.Ad_password));
                gym_name=cursor.getString(cursor.getColumnIndex(DatabaseHelper.Ad_email));
                mobile_no=cursor.getString(cursor.getColumnIndex(DatabaseHelper.Ad_mobile));
                address=cursor.getString(cursor.getColumnIndex(DatabaseHelper.Ad_address));
                cursor.moveToNext();
            }

            et_gym_name.setText(gym_name);
            et_address.setText(address);
            et_mob.setText(mobile_no);
            et_name.setText(name);
            et_username.setText(username);
            et_password.setText(password);
        }
        dbManager.close();
    }

    private boolean validate() {
        gym_name=et_gym_name.getText().toString();
        address=et_address.getText().toString();
        mobile_no=et_mob.getText().toString();
        name=et_name.getText().toString();
        username=et_username.getText().toString();
        password=et_password.getText().toString();

        if(gym_name.length()>0)
        {
            if(name.length()>0)
            {
                Pattern pattern = null;
                Matcher matcher;
                pattern=pattern.compile(CONTACT_PATTERN);
                matcher=pattern.matcher(mobile_no);
                if (mobile_no.length()==0 || matcher.matches()) {
                    if (username.length() > 0) {
                        if (password.length() > 0) {
                            return true;
                        } else {
                            MyDynamicToast.errorMessage(Admin.this, "Please Enter Password");
                            et_password.requestFocus();
                            return false;
                        }
                    } else {
                        MyDynamicToast.errorMessage(Admin.this, "Please Enter User Name");
                        et_username.requestFocus();
                        return false;
                    }
                }
                else
                {
                    MyDynamicToast.errorMessage(Admin.this,"Please Enter Enter Valid Mobile No");
                    et_mob.requestFocus();
                    return false;
                }
            }
            else
            {
                MyDynamicToast.errorMessage(Admin.this,"Please Enter Owner Name");
                et_name.requestFocus();
                return false;
            }
        }
        else
        {
            MyDynamicToast.errorMessage(Admin.this,"Please Enter Gym Name");
            et_gym_name.requestFocus();
            return false;
        }
    }
}