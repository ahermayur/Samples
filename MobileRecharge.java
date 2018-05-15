package lnc.paym.Retailer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lnc.paym.Adapter.SpinnerAdapter;
import lnc.paym.Common.AsyncTask.Async_Service_Charge;
import lnc.paym.CustomView.Button.Button1;
import lnc.paym.CustomView.CustomAlert.AwesomeErrorDialog;
import lnc.paym.CustomView.CustomAlert.AwesomeSuccessDialog;
import lnc.paym.CustomView.EditText.EditText;
import lnc.paym.Data.Op;
import lnc.paym.R;
import lnc.paym.RestAPI.RestAPI;
import lnc.paym.Util.Encryption.Enc_Dec;
import lnc.paym.Util.ErrorCode;
import lnc.paym.Util.SessionManager;
import lnc.paym.Util.Util;
import lnc.paym.Util.Validate;

import static lnc.paym.Util.Util.CONTACT_PATTERN;

public class MobileRecharge extends Fragment implements TextWatcher, View.OnClickListener {
    Spinner sp_operator, sp_sub_operator;
    SpinnerAdapter spinnerAdapter, circleAdapter;
    EditText et_mob, et_conf_mobile, et_amount, et_conf_amount;
    RadioGroup rg_ts;
    RadioButton rb_prepaid, rb_postpaid, rb_topup, rb_special;
    String mob, mob1, type, operator, sub_operator, param1 = "";
    String btn_string;
    int amount, amount1;
    int tempSelectedPos;
    //   public String[] mob_op_logo;
    public ArrayList<String> mob_op_name;
    Button1 btn_next;
    public boolean isSet;
    SessionManager session;
    Context context;
    Matcher matcher;
    Pattern pattern;
    ArrayList<Util.operator> op_list;
    ArrayList<Op> opwithLogo, op_circle;
    boolean isOpSelected = false;
    PermissionListener permissionListener;
    boolean isFocused=false;
    boolean isFocusedConf=false;
    Async_Service_Charge async_service_charge;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fr_mobile_recharge, container, false);

        //Title
        getActivity().setTitle(getResources().getString(R.string.menu_rmobrc));
        //get operator logo and name from string array

        opwithLogo = new ArrayList<>();
        //     mob_op_logo=getResources().getStringArray(R.array.celluler_logo);
        //      mob_op_name=getResources().getStringArray(R.array.celluler_name);


        //init view
        op_list = new ArrayList<Util.operator>();
        op_circle = new ArrayList<Op>();
        loadData();
        getPrepaid();
        getCircle();
        context = getContext();
        session = new SessionManager(context);
        sp_operator = (Spinner) view.findViewById(R.id.Spinner);
        rg_ts = (RadioGroup) view.findViewById(R.id.rg_ts);
        rb_special = (RadioButton) view.findViewById(R.id.rb_special);
        rb_topup = (RadioButton) view.findViewById(R.id.rb_topup);
        rb_prepaid = (RadioButton) view.findViewById(R.id.rb_prepaid);
        rb_postpaid = (RadioButton) view.findViewById(R.id.rb_postpaid);
        sp_sub_operator = (Spinner) view.findViewById(R.id.Sp_circle);
        et_mob = (EditText) view.findViewById(R.id.et_mobile);
        et_conf_mobile = (EditText) view.findViewById(R.id.et_conf_mobile);
        et_amount = (EditText) view.findViewById(R.id.et_amount);
        et_conf_amount = (EditText) view.findViewById(R.id.et_conf_amount);
        btn_next = (Button1) view.findViewById(R.id.btn_next);
        et_conf_amount.isSet = true;
        et_mob.setLength(10);
        et_conf_mobile.setLength(10);

        //Events on View
        rb_prepaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getPrepaid();
                    btn_string=context.getResources().getString(R.string.mobile_recharge_button);
                } else {
                    getPostpaid();
                    try {

                        String tempName = opwithLogo.get(sp_operator.getSelectedItemPosition()).getName();
                        String Operator = "";
                        String tempType = "";
                        if (rb_prepaid.isChecked()) {
                            tempType = ErrorCode.Prepaid;
                            if (!rb_topup.isChecked()) {
                                Operator = Operator + "-Special";
                            }
                        } else {
                            tempType = ErrorCode.Postpaid;
                        }

                        Operator = tempName + Operator;
                        if(sp_operator.getSelectedItemPosition()>0) {
                            async_service_charge = (Async_Service_Charge) new Async_Service_Charge(Operator, tempType).execute();
                        }
                        btn_string = context.getResources().getString(R.string.bill_pay_button);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                btn_next.setText(btn_string);
                spinnerAdapter.notifyDataSetChanged();
            }
        });
        et_mob.addTextChangedListener(this);
        et_conf_mobile.addTextChangedListener(this);
        et_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // if amount is greater than 500 enable confirm edit text
                int rc_amount = et_amount.getInt();
                if (rc_amount < 500) {
                    et_conf_amount.setVisibility(View.GONE);
                    et_conf_amount.setText(rc_amount + "");
                } else {
                    et_conf_amount.setVisibility(View.VISIBLE);
                    et_conf_amount.setText("");
                }
            }
        });
        et_conf_amount.addTextChangedListener(this);

        btn_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (validate()) {
                /*      // Recharge Confirm Page
                        Bundle bundle = new Bundle();
                        bundle.putString(getResources().getString(R.string.key_no), mob);
                        bundle.putString(getResources().getString(R.string.key_operator), operator);
                        bundle.putString(getResources().getString(R.string.key_suboperator), sub_operator);
                        bundle.putString(getResources().getString(R.string.key_type), type);
                        bundle.putInt(getResources().getString(R.string.key_amount), amount);
                      //  bundle.putString("logo_url",mob_op_logo[tempSelectedPos]);
                        Fragment mFragment;
                        mFragment=new ConfirmRecharge();
                        mFragment.setArguments(bundle);
                        NewRetailer.openFragment(mFragment);*/

                   showAlert();
                }
            }
        });
        sp_operator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tempName = opwithLogo.get(position).getName();
                if (rb_prepaid.isChecked()) {
                    if (tempName.equalsIgnoreCase("BSNL") || tempName.equalsIgnoreCase("JIO") || tempName.equalsIgnoreCase("Reliance GSM") || tempName.equalsIgnoreCase("Uninor") || tempName.equalsIgnoreCase("Tata Docomo GSM") || tempName.equalsIgnoreCase("Videocon") || tempName.equalsIgnoreCase("MTNL")) {
                        rg_ts.setVisibility(View.VISIBLE);
                    } else {
                        rb_topup.setChecked(true);
                        rg_ts.setVisibility(View.GONE);
                    }
                } else {
                    rb_topup.setChecked(true);
                    rg_ts.setVisibility(View.GONE);
                }

                if(position>0) {
                    String Operator = "";
                    String tempType = "";
                    if (rb_prepaid.isChecked()) {
                        tempType = ErrorCode.Prepaid;
                        if (!rb_topup.isChecked()) {
                            Operator = Operator + "-Special";
                        }
                    } else {
                        tempType = ErrorCode.Postpaid;
                    }

                    Operator = tempName + Operator;

                    async_service_charge = (Async_Service_Charge) new Async_Service_Charge(Operator, tempType).execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Applying Adapter on operator Spinner
        spinnerAdapter = new SpinnerAdapter(getContext(), opwithLogo);
        sp_operator.setAdapter(spinnerAdapter);

        // Applying Circles on Spinnner
        circleAdapter = new SpinnerAdapter(getContext(), op_circle);
        sp_sub_operator.setAdapter(circleAdapter);        //Open Contact Permission
        permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openContact();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getActivity(), "Can not read Phone Contact without Permission", Toast.LENGTH_SHORT).show();
            }
        };

        // Drawable OnTouch of Contact

        et_mob.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (et_mob.getRight() - et_mob.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        // your action here

                        TedPermission.with(getActivity())
                                .setPermissionListener(permissionListener)
                                .setPermissions(Manifest.permission.READ_CONTACTS)
                                .check();

                        return true;
                    }
                }

                return false;
            }
        });

        if(this.getArguments()!=null) {
            try {
                mob = this.getArguments().getString(getResources().getString(R.string.key_mob)).toString();
                if(!Validate.isEmptyorNull(mob)) {
                    et_mob.setText(mob);
                    et_conf_mobile.setText(mob);
                    isOpSelected=false;
                    keyDown();
                    et_amount.requestFocus();
                }
            }
            catch (Exception e)
            {

            }
        }

        return view;
    }


    /**
     * this method is used to show dialog
     * for Confirmation of recharge
     */
    private void showAlert() {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setIcon(R.mipmap.ic_app_logo);

        btn_string=btn_next.getText().toString();
        double surcharge=0;
        if(async_service_charge.isGetCharge())
        {
            surcharge=async_service_charge.Calculate_Charge(amount);
        }
        builder.setMessage(ErrorCode.getRechargeAlertMsg(btn_string,operator,mob,amount,surcharge));

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new AsyncMakeRecharge().execute();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.setIcon(R.mipmap.ic_app_logo);
        alertDialog.setTitle(btn_string);
        alertDialog.show();
    }

    private void getCircle() {
        op_circle.clear();
        op_circle.add(new Op("Choose Circle"));
        op_circle.add(new Op("Andhra Pradesh"));
        op_circle.add(new Op("Assam"));
        op_circle.add(new Op("Bihar"));
        op_circle.add(new Op("Chennai"));
        op_circle.add(new Op("Delhi"));
        op_circle.add(new Op("Gujarat"));
        op_circle.add(new Op("Haryana"));
        op_circle.add(new Op("Himachal Pradesh"));
        op_circle.add(new Op("Jammu & Kashmir"));
        op_circle.add(new Op("Karnataka"));
        op_circle.add(new Op("Kerala"));
        op_circle.add(new Op("Kolkata"));
        op_circle.add(new Op("Maharashtra & Goa"));
        op_circle.add(new Op("Madhya Pradesh & Chhattisgarh"));
        op_circle.add(new Op("Mumbai"));
        op_circle.add(new Op("North East"));
        op_circle.add(new Op("Orissa"));
        op_circle.add(new Op("Punjab"));
        op_circle.add(new Op("Rajasthan"));
        op_circle.add(new Op("Tamil Nadu"));
        op_circle.add(new Op("Uttar Pradesh - East"));
        op_circle.add(new Op("Uttar Pradesh - West"));
        op_circle.add(new Op("West Bengal"));
        op_circle.add(new Op("Jharkhand"));

    }

    private void getPrepaid() {
        opwithLogo.clear();
        opwithLogo.add(new Op("Choose Operator"));
        opwithLogo.add(new Op("Aircel", R.drawable.aircel));
        opwithLogo.add(new Op("Airtel", R.drawable.airtel));
        opwithLogo.add(new Op("BSNL", R.drawable.bsnl));
        opwithLogo.add(new Op("Idea", R.drawable.idea));
        opwithLogo.add(new Op("Vodafone", R.drawable.vodafone));
        opwithLogo.add(new Op("JIO", R.drawable.jio));
        opwithLogo.add(new Op("MTS", R.drawable.mts));
        opwithLogo.add(new Op("Tata Docomo GSM", R.drawable.docomo));
        opwithLogo.add(new Op("Tata Docomo CDMA", R.drawable.docomo));
        opwithLogo.add(new Op("MTNL", R.drawable.mtnl));
        opwithLogo.add(new Op("Reliance GSM", R.drawable.reliance));
        opwithLogo.add(new Op("Reliance CDMA", R.drawable.reliance));
        opwithLogo.add(new Op("Tata Indicom", R.drawable.indicom));
        opwithLogo.add(new Op("Uninor", R.drawable.telenor));
        opwithLogo.add(new Op("Virgin GSM", R.drawable.virgin));
        opwithLogo.add(new Op("Virgin CDMA", R.drawable.virgin));
        opwithLogo.add(new Op("T24", R.drawable.t24));
        opwithLogo.add(new Op("Videocon", R.drawable.videocon));
    }

    private void getPostpaid() {
        opwithLogo.clear();
        opwithLogo.add(new Op("Choose Operator"));
        opwithLogo.add(new Op("Aircel", R.drawable.aircel));
        opwithLogo.add(new Op("Airtel", R.drawable.airtel));
        opwithLogo.add(new Op("BSNL", R.drawable.bsnl));
        opwithLogo.add(new Op("Idea", R.drawable.idea));
        opwithLogo.add(new Op("Vodafone", R.drawable.vodafone));
        opwithLogo.add(new Op("JIO", R.drawable.jio));
        opwithLogo.add(new Op("MTS", R.drawable.mts));
        opwithLogo.add(new Op("Tata Docomo", R.drawable.docomo));
        opwithLogo.add(new Op("Reliance", R.drawable.reliance));
        opwithLogo.add(new Op("Tata Indicom", R.drawable.indicom));


    }



    private void loadData() {

        // Jio Database
        array("7000", "13", "2");
        array("7001", "13", "10");
        array("7002", "13", "16");
        array("7003", "13", "23");
        array("7004", "13", "3");
        array("7005", "13", "19");
        array("7006", "13", "4");
        array("7007", "13", "20");
        array("7008", "13", "12");
        array("7009", "13", "9");
        array("7010", "13", "14");
        array("7011", "13", "7");
        array("7012", "13", "15");
        array("7013", "13", "6");
        array("7014", "13", "11");
        array("7015", "13", "17");
        array("7016", "13", "5");
        array("7017", "13", "21");
        array("7018", "13", "18");
        array("7019", "13", "0");
        array("7020", "13", "1");
        array("7021", "13", "9");
        //
        //        array("7045","","");
        array("9900", "0", "0");
        array("9901", "0", "0");
        array("9902", "0", "0");
        array("9903", "0", "1");
        array("9904", "1", "2");
        array("9905", "2", "3");
        array("9906", "0", "4");
        array("9907", "2", "5");
        array("9908", "0", "6");
        array("9909", "3", "2");
        array("9910", "0", "7");
        array("9911", "1", "7");
        array("9912", "1", "6");
        array("9913", "3", "2");
        array("9914", "1", "8");
        array("9915", "0", "8");
        array("9916", "3", "0");
        array("9917", "1", "21");
        array("9918", "3", "20");
        array("9919", "3", "20");
        array("9920", "3", "9");
        array("9921", "1", "1");
        array("9922", "1", "1");
        array("9923", "3", "1");
        array("9924", "1", "2");
        array("9925", "3", "2");
        array("9926", "1", "5");
        array("9927", "1", "21");
        array("9928", "0", "11");
        array("9929", "0", "11");
        array("9930", "3", "9");
        array("9931", "0", "3");
        array("9932", "0", "10");
        array("9933", "0", "10");
        array("9934", "0", "3");
        array("9935", "0", "20");
        array("9936", "0", "20");
        array("9937", "0", "12");
        array("9938", "0", "12");
        array("9939", "0", "3");
        array("9940", "0", "13");
        array("9941", "4", "13");
        array("9942", "4", "14");
        array("9943", "3", "14");
        array("9944", "0", "14");
        array("9945", "0", "0");
        array("9946", "3", "15");
        array("9947", "1", "15");
        array("9948", "1", "6");
        array("9949", "0", "6");
        array("9950", "0", "11");
        array("9951", "1", "6");
        array("9952", "0", "14");
        array("9953", "3", "7");
        array("9954", "0", "16");
        array("9955", "0", "3");
        array("9956", "0", "20");
        array("9957", "0", "16");
        array("9958", "0", "7");
        array("9959", "0", "6");
        array("9960", "0", "1");
        array("9961", "1", "15");
        array("9962", "3", "13");
        array("9963", "0", "6");
        array("9964", "1", "0");
        array("9965", "4", "14");
        array("9966", "3", "6");
        array("9967", "0", "9");
        array("9968", "5", "7");
        array("9969", "5", "9");
        array("9970", "0", "1");
        array("9971", "0", "7");
        array("9972", "0", "0");
        array("9973", "0", "3");
        array("9974", "0", "2");
        array("9975", "0", "1");
        array("9976", "4", "14");
        array("9977", "1", "5");
        array("9978", "3", "2");
        array("9979", "3", "2");
        array("9980", "0", "0");
        array("9981", "0", "5");
        array("9982", "3", "11");
        array("9983", "3", "11");
        array("9984", "3", "20");
        array("9985", "3", "6");
        array("9986", "3", "0");
        array("9987", "0", "9");
        array("9988", "3", "8");
        array("9989", "0", "6");
        array("9990", "1", "7");
        array("9991", "3", "17");
        array("9992", "1", "17");
        array("9993", "0", "5");
        array("9994", "0", "14");
        array("9995", "0", "15");
        array("9996", "0", "17");
        array("9997", "0", "21");
        array("9998", "0", "2");
        array("9999", "3", "7");

        array("9800", "0", "10");
        array("9801", "0", "3");
        array("9802", "4", "17");
        array("9803", "4", "8");
        array("9804", "4", "1");
        array("9805", "0", "18");
        array("9806", "4", "5");
        array("9807", "4", "20");
        array("9808", "4", "21");
        array("9809", "4", "15");
        array("9810", "0", "7");
        array("9811", "3", "7");
        array("9812", "1", "17");
        array("9813", "3", "17");
        array("9814", "1", "8");
        array("9815", "0", "8");
        array("9816", "0", "18");
        array("9817", "2", "18");
        array("9818", "0", "7");
        array("9819", "3", "9");
        array("9820", "3", "9");
        array("9821", "6", "9");
        array("9822", "1", "1");
        array("9823", "3", "1");
        array("9824", "1", "2");
        array("9825", "3", "2");
        array("9826", "1", "5");
        array("9827", "2", "5");
        array("9828", "3", "11");
        array("9829", "0", "11");
        array("9830", "3", "1");
        array("9831", "0", "1");
        array("9832", "2", "10");
        array("9833", "3", "9");
        array("9835", "2", "3");
        array("9836", "3", "1");
        array("9837", "1", "21");
        array("9838", "3", "20");
        array("9839", "3", "20");
        array("9840", "0", "13");
        array("9841", "4", "13");
        array("9842", "4", "14");
        array("9843", "3", "14");
        array("9844", "1", "0");
        array("9845", "0", "0");
        array("9846", "3", "15");
        array("9847", "1", "15");
        array("9848", "1", "6");
        array("9849", "0", "6");
        array("9850", "1", "1");
        array("9851", "4", "10");
        array("9852", "4", "3");
        array("9853", "4", "12");
        array("9854", "4", "16");
        array("9855", "1", "8");
        array("9856", "4", "20");
        array("9857", "4", "18");
        array("9858", "4", "4");
        array("9859", "4", "16");
        array("9860", "0", "1");
        array("9861", "2", "12");
        array("9862", "0", "20");
        array("9863", "2", "20");
        array("9864", "2", "16");
        array("9865", "4", "14");
        array("9866", "0", "6");
        array("9867", "0", "9");
        array("9868", "5", "7");
        array("9869", "5", "9");
        array("9870", "6", "9");
        array("9871", "0", "7");
        array("9872", "0", "8");
        array("9873", "3", "7");
        array("9874", "3", "1");
        array("9875", "7", "11");
        array("9876", "0", "8");
        array("9878", "0", "8");
        array("9879", "3", "2");
        array("9880", "0", "0");
        array("9881", "1", "1");
        array("9882", "1", "18");
        array("9883", "2", "1");
        array("9884", "3", "13");
        array("9885", "3", "6");
        array("9886", "3", "0");
        array("9887", "1", "11");
        array("9888", "3", "8");
        array("9889", "1", "20");
        array("9890", "0", "1");
        array("9891", "1", "7");
        array("9892", "0", "9");
        array("9893", "0", "5");
        array("9894", "0", "14");
        array("9895", "0", "15");
        array("9896", "0", "17");
        array("9897", "0", "21");
        array("9898", "0", "2");
        array("9899", "3", "7");
        array("9700", "4", "6");
        array("9701", "0", "6");
        array("9702", "1", "9");
        array("9703", "3", "6");
        array("9704", "0", "6");
        array("9705", "1", "6");
        array("9706", "3", "16");
        array("9707", "2", "16");
        array("9708", "1", "3");
        array("9709", "3", "3");
        array("9710", "4", "13");
        array("9711", "3", "7");
        array("9712", "3", "2");
        array("9713", "3", "5");
        array("9714", "1", "2");
        array("9715", "4", "14");
        array("9716", "4", "7");
        array("9717", "0", "7");
        array("9718", "1", "7");
        array("9719", "3", "21");
        array("9720", "3", "21");
        array("9721", "3", "20");
        array("9722", "4", "2");
        array("9723", "1", "2");
        array("9724", "0", "2");
        array("9725", "0", "2");
        array("9726", "3", "2");
        array("9727", "3", "2");
        array("9728", "1", "17");
        array("9729", "0", "17");
        array("9730", "0", "1");
        array("9731", "0", "0");
        array("9732", "3", "10");
        array("9733", "3", "10");
        array("9734", "3", "10");
        array("9735", "3", "10");
        array("9736", "3", "18");
        array("9737", "1", "2");
        array("9738", "4", "0");
        array("9739", "3", "0");
        array("9740", "0", "0");
        array("9741", "0", "0");
        array("9742", "3", "0");
        array("9743", "1", "0");
        array("9744", "1", "15");
        array("9745", "3", "15");
        array("9746", "0", "15");
        array("9747", "1", "15");
        array("9748", "0", "1");
        array("9749", "2", "10");
        array("9750", "4", "14");
        array("9751", "3", "14");
        array("9752", "0", "5");
        array("9753", "1", "5");
        array("9754", "1", "5");
        array("9755", "0", "5");
        array("9756", "1", "21");
        array("9757", "5", "9");
        array("9758", "3", "21");
        array("9759", "3", "21");
        array("9760", "0", "21");
        array("9761", "3", "21");
        array("9762", "4", "1");
        array("9763", "1", "1");
        array("9764", "3", "1");
        array("9765", "3", "1");
        array("9766", "0", "1");
        array("9767", "1", "1");
        array("9768", "4", "9");
        array("9769", "3", "9");
        array("9770", "2", "5");
        array("9771", "0", "3");
        array("9772", "3", "11");
        array("9773", "6", "9");
        array("9774", "3", "20");
        array("9775", "3", "10");
        array("9776", "3", "12");
        array("9777", "0", "12");
        array("9778", "2", "12");
        array("9779", "0", "8");
        array("9780", "3", "8");
        array("9781", "1", "8");
        array("9782", "4", "11");
        array("9783", "3", "11");
        array("9784", "0", "11");
        array("9785", "1", "11");
        array("9786", "3", "14");
        array("9787", "3", "14");
        array("9788", "4", "14");
        array("9789", "0", "14");
        array("9790", "0", "14");
        array("9791", "0", "14");
        array("9792", "3", "20");
        array("9793", "0", "20");
        array("9794", "0", "20");
        array("9795", "1", "20");
        array("9796", "3", "4");
        array("9797", "0", "4");
        array("9798", "2", "3");
        array("9799", "0", "11");

        array("9600", "0", "14");
        array("9601", "0", "2");
        array("9602", "0", "11");
        array("9603", "1", "6");
        array("9604", "1", "1");
        array("9605", "1", "15");
        array("9606", "2", "20");
        array("9607", "2", "16");
        array("9608", "2", "3");
        array("9609", "3", "10");
        array("9610", "3", "11");
        array("9611", "0", "0");
        array("9612", "0", "20");
        array("9613", "4", "16");
        array("9614", "4", "10");
        array("9615", "4", "20");
        array("9616", "1", "20");
        array("9617", "1", "5");
        array("9618", "0", "6");
        array("9619", "3", "9");
        array("9620", "3", "0");
        array("9621", "0", "20");
        array("9622", "0", "4");
        array("9623", "1", "1");
        array("9624", "1", "2");
        array("9625", "2", "18");
        array("9626", "3", "14");
        array("9627", "3", "21");
        array("9628", "3", "20");
        array("9629", "0", "14");
        array("9630", "0", "5");
        array("9631", "0", "3");
        array("9632", "0", "0");
        array("9633", "0", "15");
        array("9634", "0", "21");
        array("9635", "0", "10");
        array("9636", "0", "11");
        array("9637", "3", "1");
        array("9638", "3", "2");
        array("9639", "1", "21");
        array("9640", "1", "6");
        array("9641", "2", "10");
        array("9642", "3", "6");
        array("9645", "3", "15");
        array("9646", "3", "8");
        array("9647", "3", "10");
        array("9648", "3", "20");
        array("9649", "3", "11");
        array("9650", "0", "7");
        array("9651", "0", "20");
        array("9652", "0", "6");
        array("9653", "12", "8");
        array("9654", "3", "7");
        array("9655", "3", "14");
        array("9656", "1", "15");
        array("9657", "1", "1");
        array("9658", "4", "12");
        array("9659", "4", "14");
        array("9660", "0", "11");
        array("9661", "0", "3");
        array("9662", "0", "2");
        array("9663", "0", "0");
        array("9664", "6", "9");
        array("9665", "0", "9");
        array("9666", "1", "6");
        array("9667", "7", "11");
        array("9668", "0", "12");
        array("9669", "1", "5");
        array("9670", "3", "20");
        array("9671", "3", "17");
        array("9672", "3", "11");
        array("9673", "3", "1");
        array("9674", "3", "1");
        array("9675", "3", "21");
        array("9676", "0", "6");
        array("9677", "0", "14");
        array("9678", "0", "16");
        array("9679", "0", "10");
        array("9680", "0", "11");
        array("9681", "2", "1");
        array("9682", "8", "21");
        array("9683", "8", "17");
        array("9684", "8", "1");
        array("9685", "0", "5");
        array("9686", "0", "0");
        array("9687", "3", "2");
        array("9688", "4", "14");
        array("9689", "1", "1");
        array("9690", "1", "21");
        array("9691", "2", "5");
        array("9692", "2", "12");
        array("9693", "2", "3");
        array("9694", "1", "11");
        array("9695", "0", "20");
        array("9696", "2", "20");
        array("9697", "4", "4");
        array("9698", "4", "14");
        array("9699", "2", "9");

        array("9500", "0", "14");
        array("9501", "0", "8");
        array("9502", "0", "6");
        array("9503", "0", "1");
        array("9504", "4", "3");
        array("9505", "1", "6");
        array("9506", "1", "20");
        array("9507", "1", "3");
        array("9508", "2", "16");
        array("9509", "2", "11");
        array("9510", "2", "2");
        array("9524", "4", "14");
        array("9525", "1", "3");
        array("9526", "1", "15");
        array("9527", "1", "1");
        array("9528", "2", "21");
        array("9529", "2", "11");
        array("95305", "8", "8");
        array("95306", "8", "8");
        array("95307", "8", "8");
        array("95308", "8", "8");
        array("95309", "8", "8");
        array("95300", "8", "11");
        array("95301", "8", "11");
        array("95302", "8", "11");
        array("95303", "8", "11");
        array("95304", "8", "11");
        array("95310", "8", "16");
        array("95311", "8", "16");
        array("95312", "8", "16");
        array("95313", "8", "16");
        array("95314", "8", "16");
        array("95315", "8", "10");
        array("95316", "8", "10");
        array("95317", "8", "10");
        array("95318", "8", "10");
        array("95319", "8", "10");
        array("9532", "8", "20");
        array("9533", "2", "6");
        array("9534", "3", "3");
        array("9535", "0", "0");
        array("9536", "3", "21");
        array("9537", "3", "2");
        array("9538", "3", "0");
        array("9539", "3", "15");
        array("9540", "1", "7");
        array("9541", "2", "17");
        array("9542", "1", "6");
        array("9543", "2", "14");
        array("9544", "1", "15");
        array("9545", "3", "1");
        array("9546", "0", "3");
        array("9547", "0", "10");
        array("9548", "2", "21");
        array("9549", "3", "11");
        array("9550", "0", "6");
        array("9551", "4", "13");
        array("9552", "1", "1");
        array("9553", "1", "6");
        array("9554", "3", "20");
        array("9555", "2", "7");
        array("9556", "0", "12");
        array("9557", "0", "21");
        array("9558", "0", "2");
        array("9559", "0", "20");
        array("9560", "0", "7");
        array("9561", "0", "1");
        array("9562", "1", "15");
        array("9563", "4", "10");
        array("9564", "3", "10");
        array("9565", "3", "20");
        array("9566", "0", "14");
        array("9567", "0", "15");
        array("9568", "1", "21");
        array("9569", "2", "8");
        array("9570", "3", "3");
        array("9571", "0", "11");
        array("9572", "0", "3");
        array("9573", "0", "6");
        array("9574", "1", "2");
        array("9575", "1", "5");
        array("9576", "1", "3");
        array("9577", "4", "16");
        array("9578", "4", "14");
        array("9579", "2", "1");
        array("9580", "2", "20");
        array("9581", "3", "6");
        array("9582", "3", "7");
        array("9583", "3", "12");
        array("9584", "3", "5");
        array("9585", "3", "14");
        array("9586", "3", "2");
        array("9587", "3", "11");
        array("9589", "0", "5");
        array("9590", "2", "0");
        array("9591", "0", "0");
        array("9592", "1", "8");
        array("9593", "3", "10");
        array("9594", "1", "9");
        array("9595", "2", "1");
        array("9596", "0", "4");
        array("9597", "0", "14");
        array("9598", "1", "20");

        array("9400", "8", "15");
        array("9401", "8", "16");
        array("9402", "8", "20");
        array("9403", "8", "1");
        array("9404", "8", "1");
        array("9405", "8", "1");
        array("9406", "8", "5");
        array("9407", "8", "5");
        array("9408", "8", "2");
        array("9409", "8", "2");
        array("9410", "8", "21");
        array("9411", "8", "21");
        array("9412", "8", "21");
        array("9413", "8", "11");
        array("9414", "8", "11");
        array("9415", "8", "20");
        array("9416", "8", "17");
        array("9417", "8", "8");
        array("9418", "8", "18");
        array("9419", "8", "4");
        array("9420", "8", "1");
        array("9421", "8", "1");
        array("9422", "8", "1");
        array("9423", "8", "1");
        array("9424", "8", "5");
        array("9425", "8", "5");
        array("9426", "8", "2");
        array("9427", "8", "2");
        array("9428", "8", "2");
        array("9429", "8", "2");
        array("9430", "8", "3");
        array("9431", "8", "3");
        array("9432", "8", "1");
        array("9433", "8", "1");
        array("9434", "8", "10");
        array("9435", "8", "16");
        array("9436", "8", "20");
        array("9437", "8", "12");
        array("9438", "8", "12");
        array("9439", "8", "12");
        array("9440", "8", "6");
        array("9441", "8", "6");
        array("9442", "8", "14");
        array("9443", "8", "14");
        array("9444", "8", "13");
        array("9445", "8", "13");
        array("9446", "8", "15");
        array("9447", "8", "15");
        array("9448", "8", "0");
        array("9449", "8", "0");
        array("9450", "8", "20");
        array("9451", "8", "20");
        array("9452", "8", "20");
        array("9453", "8", "20");
        array("9454", "8", "20");
        array("9455", "8", "20");
        array("9456", "8", "21");
        array("9457", "8", "21");
        array("9458", "8", "21");
        array("9459", "8", "18");
        array("9460", "8", "11");
        array("9461", "8", "11");
        array("9462", "8", "11");
        array("9463", "8", "8");
        array("9464", "8", "8");
        array("9465", "8", "8");
        array("9466", "8", "17");
        array("9467", "8", "17");
        array("94680", "8", "17");
        array("94681", "8", "17");
        array("94682", "8", "17");
        array("94683", "8", "17");
        array("94684", "8", "17");
        array("94685", "8", "11");
        array("94686", "8", "11");
        array("94687", "8", "11");
        array("94688", "8", "11");
        array("94689", "8", "11");
        array("9469", "8", "4");
        array("9470", "8", "3");
        array("9471", "8", "3");
        array("9472", "8", "3");
        array("94730", "8", "3");
        array("94731", "8", "3");
        array("94732", "8", "3");
        array("94733", "8", "3");
        array("94734", "8", "3");
        array("94735", "8", "20");
        array("94736", "8", "20");
        array("94737", "8", "20");
        array("94738", "8", "20");
        array("94739", "8", "20");
        array("9474", "8", "10");
        array("9475", "8", "10");
        array("94765", "8", "16");
        array("94766", "8", "16");
        array("94767", "8", "16");
        array("94768", "8", "16");
        array("94769", "8", "16");
        array("94760", "8", "10");
        array("94761", "8", "10");
        array("94762", "8", "10");
        array("94763", "8", "10");
        array("94764", "8", "10");
        array("9477", "8", "1");
        array("9478", "8", "8");
        array("9479", "8", "5");
        array("9480", "8", "0");
        array("9481", "8", "0");
        array("9482", "8", "0");
        array("9483", "8", "0");
        array("9484", "8", "0");
        array("9485", "8", "20");
        array("9486", "8", "14");
        array("9487", "8", "14");
        array("9488", "8", "14");
        array("9489", "8", "14");
        array("9490", "8", "6");
        array("9491", "8", "6");
        array("9492", "8", "6");
        array("9493", "8", "6");
        array("9494", "8", "6");
        array("9495", "8", "15");
        array("9496", "8", "15");
        array("9497", "8", "15");
        array("9498", "8", "13");
        array("9499", "8", "13");

        array("9300", "2", "5");
        array("9301", "2", "5");
        array("9302", "2", "5");
        array("9303", "2", "5");
        array("9304", "2", "3");
        array("9305", "2", "20");
        array("9306", "2", "4");
        array("9307", "2", "20");
        array("9308", "2", "3");
        array("9309", "2", "11");
        array("9310", "2", "7");
        array("9311", "2", "7");
        array("9312", "2", "7");
        array("9313", "2", "7");
        array("9314", "2", "11");
        array("9315", "2", "17");
        array("9316", "2", "8");
        array("9317", "2", "8");
        array("9318", "2", "18");
        array("9319", "2", "21");
        array("9320", "2", "9");
        array("9321", "2", "9");
        array("9322", "2", "9");
        array("9323", "2", "9");
        array("9324", "2", "9");
        array("9325", "2", "1");
        array("9326", "2", "1");
        array("9327", "2", "2");
        array("9328", "2", "2");
        array("9329", "2", "5");
        array("9330", "2", "1");
        array("9331", "2", "1");
        array("9332", "2", "10");
        array("9333", "2", "10");
        array("9334", "2", "3");
        array("9335", "2", "20");
        array("9336", "2", "20");
        array("9337", "2", "12");
        array("9338", "2", "12");
        array("9339", "2", "1");
        array("9340", "2", "13");
        array("9341", "2", "0");
        array("9342", "2", "0");
        array("9343", "2", "0");
        array("9344", "2", "14");
        array("9345", "2", "14");
        array("9346", "2", "6");
        array("9347", "2", "6");
        array("9348", "2", "6");
        array("9349", "2", "15");
        array("9350", "2", "7");
        array("9351", "2", "11");
        array("9352", "2", "11");
        array("9353", "2", "0");
        array("9354", "2", "17");
        array("9355", "2", "17");
        array("9356", "2", "8");
        array("9357", "2", "8");
        array("9358", "2", "21");
        array("9359", "2", "21");
        array("9360", "2", "14");
        array("9361", "2", "14");
        array("9362", "2", "14");
        array("9363", "2", "14");
        array("9364", "2", "14");
        array("9365", "2", "14");
        array("9366", "2", "14");
        array("9367", "2", "14");
        array("9368", "2", "21");
        array("9369", "2", "20");
        array("9370", "2", "1");
        array("9371", "2", "1");
        array("9372", "2", "1");
        array("9373", "2", "1");
        array("9374", "2", "2");
        array("9375", "2", "2");
        array("9376", "2", "2");
        array("9377", "2", "2");
        array("9378", "2", "10");
        array("9379", "2", "0");
        array("9380", "2", "13");
        array("9381", "2", "13");
        array("9382", "2", "13");
        array("9383", "2", "13");
        array("9384", "2", "13");
        array("9385", "2", "13");
        array("9386", "2", "3");
        array("9387", "2", "15");
        array("9388", "2", "15");
        array("9389", "2", "20");
        array("9390", "2", "6");
        array("9391", "2", "6");
        array("9392", "2", "6");
        array("9393", "2", "6");
        array("9394", "2", "6");
        array("9395", "2", "6");
        array("9396", "2", "6");
        array("9397", "2", "6");
        array("9398", "2", "6");
        array("9399", "2", "6");

        array("9200", "9", "5");
        array("9201", "9", "5");
        array("9202", "9", "5");
        array("9203", "9", "5");
        array("9204", "9", "3");
        array("9205", "9", "4");
        array("9206", "9", "20");
        array("9207", "9", "16");
        array("9208", "9", "20");
        array("9209", "9", "1");
        array("9210", "10", "7");
        array("9211", "10", "7");
        array("9212", "10", "7");
        array("9213", "10", "7");
        array("9214", "9", "11");
        array("9215", "9", "17");
        array("9216", "9", "8");
        array("9217", "9", "8");
        array("9218", "9", "18");
        array("9219", "9", "21");
        array("9220", "9", "9");
        array("9221", "9", "9");
        array("9222", "9", "9");
        array("9223", "9", "9");
        array("9224", "9", "9");
        array("9225", "9", "1");
        array("9226", "9", "1");
        array("9227", "9", "2");
        array("9228", "9", "2");
        array("9229", "9", "5");
        array("9230", "9", "1");
        array("9231", "9", "1");
        array("9232", "9", "10");
        array("9233", "9", "10");
        array("9234", "9", "3");
        array("9235", "9", "20");
        array("9236", "9", "20");
        array("9237", "9", "12");
        array("9238", "9", "12");
        array("9239", "9", "1");
        array("9240", "9", "13");
        array("9241", "9", "0");
        array("9242", "9", "0");
        array("9243", "9", "0");
        array("9244", "9", "14");
        array("9245", "9", "14");
        array("9246", "9", "6");
        array("9247", "9", "6");
        array("9248", "9", "6");
        array("9249", "9", "15");
        array("9250", "10", "7");
        array("9251", "9", "11");
        array("9252", "9", "11");
        array("9253", "9", "17");
        array("9254", "9", "17");
        array("9255", "9", "17");
        array("9256", "9", "8");
        array("9257", "9", "8");
        array("9258", "9", "21");
        array("9259", "9", "21");
        array("9260", "9", "1");
        array("9261", "9", "11");
        array("9262", "9", "14");
        array("9263", "9", "3");
        array("92640", "9", "14");
        array("92641", "9", "14");
        array("92642", "9", "14");
        array("92643", "9", "14");
        array("92644", "9", "14");
        array("92645", "9", "10");
        array("92646", "9", "10");
        array("92647", "9", "10");
        array("92648", "9", "10");
        array("92649", "9", "10");
        array("9265", "9", "1");
        array("9266", "10", "7");
        array("9267", "9", "21");
        array("9268", "10", "7");
        array("9269", "9", "11");
        array("9270", "9", "1");
        array("9271", "9", "1");
        array("9272", "9", "1");
        array("9273", "9", "1");
        array("9274", "9", "2");
        array("9275", "9", "2");
        array("9276", "9", "2");
        array("9277", "9", "2");
        array("9278", "10", "7");
        array("9279", "9", "3");
        array("9280", "9", "13");
        array("9281", "9", "13");
        array("9282", "9", "13");
        array("9283", "9", "13");
        array("9284", "9", "13");
        array("9285", "9", "13");
        array("9286", "9", "21");
        array("9287", "9", "15");
        array("9288", "9", "15");
        array("9289", "10", "7");
        array("9290", "9", "6");
        array("9291", "9", "6");
        array("9292", "9", "6");
        array("9293", "9", "6");
        array("9294", "9", "6");
        array("9295", "9", "6");
        array("9296", "9", "6");
        array("9297", "9", "6");
        array("9298", "9", "6");
        array("9299", "9", "6");
        array("9100", "6", "6");
        array("9101", "6", "16");
        array("9102", "6", "3");
        array("9103", "6", "7");
        array("9104", "6", "2");
        array("9105", "6", "17");
        array("9106", "6", "18");
        array("9107", "6", "4");
        array("9108", "6", "0");
        array("9109", "6", "15");
        array("9110", "6", "1");
        array("9111", "6", "5");
        array("9112", "6", "1");
        array("9113", "6", "20");
        array("9114", "6", "12");
        array("9115", "6", "8");
        array("9116", "6", "11");
        array("9117", "6", "14");
        array("9118", "6", "20");
        array("9119", "6", "21");
        array("9120", "6", "10");
        array("9121", "11", "16");
        array("9122", "11", "3");
        array("9123", "11", "20");
        array("9124", "11", "12");
        array("9125", "11", "20");
        array("9126", "11", "10");
        array("9133", "7", "6");
        array("9134", "7", "16");
        array("9135", "7", "3");
        array("9136", "7", "7");
        array("9137", "7", "2");
        array("9138", "7", "17");
        array("9139", "7", "18");
        array("9140", "7", "4");
        array("9141", "7", "0");
        array("9142", "7", "15");
        array("9143", "7", "1");
        array("9144", "7", "5");
        array("9145", "7", "1");
        array("9146", "7", "9");
        array("9147", "7", "20");
        array("9148", "7", "12");
        array("9149", "7", "8");
        array("9150", "7", "14");
        array("9151", "7", "20");
        array("9152", "7", "21");
        array("9153", "7", "10");
        array("9158", "3", "1");
        array("9159", "3", "14");
        array("9160", "3", "6");
        array("9161", "3", "20");
        array("9162", "0", "3");
        array("9163", "0", "1");
        array("9164", "1", "0");
        array("9165", "1", "5");
        array("9166", "0", "11");
        array("9167", "3", "9");
        array("9171", "11", "14");
        array("9172", "11", "9");
        array("9173", "11", "2");
        array("9174", "11", "5");
        array("9175", "11", "1");
        array("9176", "3", "13");
        array("9177", "0", "6");
        array("9178", "0", "12");
        array("9179", "0", "5");
        array("9180", "8", "6");
        array("9181", "8", "16");
        array("9182", "8", "3");
        array("9183", "8", "13");
        array("9184", "8", "2");
        array("9185", "8", "18");
        array("9186", "8", "4");
        array("9187", "8", "0");
        array("9188", "8", "15");
        array("9189", "8", "1");
        array("9190", "8", "5");
        array("9191", "8", "20");
        array("9192", "8", "12");
        array("9193", "8", "8");
        array("9194", "8", "11");
        array("9195", "8", "14");
        array("9196", "8", "20");
        array("9197", "8", "10");
        array("9198", "0", "20");
        array("9199", "0", "3");
        array("9000", "0", "6");
        array("9001", "0", "11");
        array("9002", "0", "10");
        array("9003", "0", "14");
        array("9004", "0", "9");
        array("9005", "0", "20");
        array("9006", "0", "3");
        array("9007", "0", "1");
        array("9008", "0", "0");
        array("9009", "1", "5");
        array("9010", "1", "6");
        array("9011", "1", "1");
        array("9012", "1", "21");
        array("9013", "5", "7");
        array("9014", "2", "6");
        array("9015", "2", "7");
        array("9016", "2", "2");
        array("9017", "2", "17");
        array("9018", "2", "4");
        array("9019", "2", "0");
        array("9020", "2", "15");
        array("9021", "2", "1");
        array("9022", "2", "9");
        array("9023", "2", "8");
        array("9024", "2", "11");
        array("9025", "2", "14");
        array("9026", "2", "20");
        array("9027", "2", "21");
        array("9028", "9", "1");
        array("9029", "9", "9");
        array("9030", "9", "6");
        array("9031", "9", "3");
        array("9032", "9", "6");
        array("9033", "9", "2");
        array("9034", "9", "17");
        array("9035", "9", "0");
        array("9036", "9", "0");
        array("9037", "9", "15");
        array("9038", "9", "1");
        array("9039", "9", "5");
        array("9040", "9", "12");
        array("9041", "9", "8");
        array("9042", "9", "14");
        array("9043", "9", "14");
        array("9044", "9", "20");
        array("9045", "9", "21");
        array("9046", "9", "10");
        array("9047", "3", "14");
        array("9048", "3", "15");
        array("9049", "3", "1");
        array("9050", "3", "17");
        array("9051", "3", "1");
        array("9052", "3", "6");
        array("9053", "11", "17");
        array("9054", "11", "18");
        array("9055", "11", "4");
        array("9056", "11", "8");
        array("9057", "11", "11");
        array("9058", "11", "21");
        array("9059", "11", "6");
        array("9060", "11", "0");
        array("9061", "11", "15");
        array("9062", "11", "1");
        array("9063", "12", "6");
        array("9064", "12", "16");
        array("9065", "12", "3");
        array("9066", "12", "7");
        array("9067", "12", "2");
        array("9068", "12", "17");
        array("9069", "12", "18");
        array("9070", "12", "4");
        array("9071", "12", "0");
        array("9072", "12", "15");
        array("9073", "12", "1");
        array("9074", "12", "5");
        array("9075", "12", "1");
        array("9076", "12", "9");
        array("9077", "12", "20");
        array("9078", "12", "12");
        array("9079", "12", "11");
        array("9080", "12", "14");
        array("9081", "12", "20");
        array("9082", "12", "21");
        array("9083", "12", "10");
        array("9084", "11", "7");
        array("9085", "1", "16");
        array("9086", "1", "4");
        array("9087", "1", "0");
        array("9088", "1", "1");
        array("9089", "1", "20");
        array("9090", "1", "12");
        array("9091", "1", "8");
        array("9092", "1", "14");
        array("9093", "1", "10");
        array("9094", "4", "13");
        array("9095", "4", "14");
        array("9096", "0", "1");
        array("9097", "4", "3");
        array("9098", "2", "5");
        array("9099", "3", "2");
        array("8900", "8", "10");
        array("8901", "8", "17");
        array("8902", "8", "1");
        array("8903", "8", "14");
        array("8904", "9", "0");
        array("8905", "2", "2");
        array("8906", "4", "10");
        array("8907", "4", "15");
        array("8908", "4", "12");
        array("8909", "4", "21");
        array("89220", "11", "20");
        array("89228", "11", "20");
        array("89229", "11", "20");
        array("8923", "11", "21");
        array("89240", "11", "20");
        array("89248", "11", "20");
        array("89249", "11", "20");
        array("8925", "7", "14");
        array("8926", "7", "10");
        array("8927", "2", "10");
        array("8928", "2", "1");
        array("8929", "2", "17");
        array("8930", "3", "17");
        array("89310", "11", "20");
        array("89318", "11", "20");
        array("89319", "11", "20");
        array("89320", "1", "20");
        array("89328", "1", "20");
        array("89329", "1", "20");
        array("89330", "1", "20");
        array("89338", "1", "20");
        array("89339", "1", "20");
        array("89340", "1", "20");
        array("89348", "1", "20");
        array("89349", "1", "20");
        array("89358", "11", "3");
        array("89359", "11", "3");
        array("89350", "1", "20");
        array("89360", "11", "3");
        array("89368", "11", "3");
        array("89369", "1", "21");
        array("89370", "1", "21");
        array("89378", "1", "21");
        array("89379", "1", "21");
        array("89380", "1", "21");
        array("89388", "1", "21");
        array("89389", "1", "21");
        array("8939", "3", "13");
        array("8940", "3", "14");
        array("89410", "1", "21");
        array("89418", "1", "21");
        array("89419", "1", "21");
        array("89420", "11", "10");
        array("89428", "11", "10");
        array("89429", "11", "10");
        array("8943", "3", "15");
        array("89440", "11", "10");
        array("89448", "11", "10");
        array("89449", "11", "10");
        array("89450", "11", "10");
        array("89458", "11", "10");
        array("89459", "11", "10");
        array("89468", "4", "11");
        array("89469", "4", "11");
        array("89460", "11", "10");
        array("89470", "4", "11");
        array("89478", "4", "11");
        array("89479", "4", "11");
        array("8948", "3", "20");
        array("8950", "9", "17");
        array("8951", "9", "0");
        array("89520", "4", "11");
        array("89528", "4", "11");
        array("89529", "4", "11");
        array("8953", "0", "20");
        array("8954", "3", "21");
        array("8955", "2", "11");
        array("8956", "2", "1");
        array("8957", "2", "20");
        array("8958", "1", "21");
        array("8959", "1", "5");
        array("8960", "9", "20");
        array("8961", "9", "1");
        array("8962", "9", "5");
        array("89639", "1", "5");
        array("89630", "4", "11");
        array("89638", "4", "11");
        array("89640", "1", "5");
        array("89648", "1", "5");
        array("89649", "1", "5");
        array("89650", "1", "5");
        array("89658", "1", "5");
        array("89659", "1", "5");
        array("89660", "1", "5");
        array("89668", "1", "5");
        array("89669", "1", "5");
        array("8967", "0", "10");
        array("8968", "0", "8");
        array("8969", "0", "3");
        array("8970", "1", "0");
        array("8971", "0", "0");
        array("8972", "0", "10");
        array("8973", "4", "14");
        array("8974", "0", "20");
        array("8975", "1", "1");
        array("8976", "9", "1");
        array("8977", "9", "6");
        array("8978", "0", "6");
        array("8979", "0", "21");
        array("8980", "3", "2");
        array("8981", "9", "1");
        array("8982", "9", "5");
        array("8983", "9", "1");
        array("8984", "9", "12");
        array("8985", "8", "6");
        array("8986", "8", "3");
        array("8987", "8", "3");
        array("8988", "8", "18");
        array("8989", "8", "5");
        array("8800", "0", "7");
        array("8801", "4", "6");
        array("8802", "4", "7");
        array("8803", "4", "4");
        array("8804", "4", "3");
        array("8805", "1", "1");
        array("8806", "3", "1");
        array("8807", "9", "14");
        array("8808", "1", "20");
        array("8809", "0", "3");
        array("8810", "2", "21");
        array("88110", "0", "16");
        array("88118", "0", "16");
        array("88119", "0", "16");
        array("88120", "0", "16");
        array("88128", "0", "16");
        array("88129", "0", "16");
        array("88130", "3", "17");
        array("88138", "3", "17");
        array("88139", "3", "17");
        array("88140", "3", "17");
        array("88148", "3", "17");
        array("88149", "3", "17");
        array("8815", "2", "5");
        array("88160", "3", "17");
        array("88168", "3", "17");
        array("88169", "3", "17");
        array("8817", "2", "5");
        array("88180", "3", "17");
        array("88188", "3", "5");
        array("88189", "3", "5");
        array("88190", "3", "5");
        array("88198", "3", "5");
        array("88199", "3", "5");
        array("8820", "2", "1");
        array("88210", "3", "5");
        array("88218", "3", "5");
        array("88219", "3", "5");
        array("8822", "2", "16");
        array("88230", "3", "5");
        array("88238", "3", "5");
        array("88239", "9", "11");
        array("8824", "2", "11");
        array("8825", "12", "14");
        array("8826", "0", "7");
        array("8827", "0", "5");
        array("8828", "12", "9");
        array("88290", "9", "11");
        array("88298", "9", "11");
        array("88299", "9", "11");
        array("88520", "9", "11");
        array("88528", "9", "11");
        array("88529", "9", "11");
        array("8853", "0", "20");
        array("88540", "9", "11");
        array("88548", "9", "11");
        array("88549", "9", "11");
        array("88550", "11", "1");
        array("88558", "11", "1");
        array("88559", "11", "1");
        array("88560", "11", "1");
        array("88568", "11", "1");
        array("88569", "11", "1");
        array("88570", "11", "1");
        array("88578", "11", "1");
        array("88579", "11", "1");
        array("8858", "11", "20");
        array("8859", "3", "21");
        array("8860", "3", "7");
        array("8861", "0", "0");
        array("88628", "11", "3");
        array("88629", "11", "3");
        array("88620", "11", "1");
        array("88630", "11", "3");
        array("88638", "11", "3");
        array("88639", "11", "3");
        array("88640", "11", "3");
        array("88648", "11", "21");
        array("88649", "11", "21");
        array("88650", "11", "21");
        array("88658", "11", "21");
        array("88659", "11", "21");
        array("8866", "9", "2");
        array("8867", "9", "0");
        array("88680", "11", "21");
        array("88688", "11", "21");
        array("88689", "11", "21");
        array("88699", "11", "20");
        array("88690", "11", "21");
        array("88698", "11", "21");
        array("8870", "0", "14");
        array("8871", "9", "5");
        array("8872", "1", "8");
        array("8873", "1", "3");
        array("8874", "3", "20");
        array("8875", "3", "11");
        array("8876", "3", "16");
        array("8877", "3", "3");
        array("8878", "3", "5");
        array("8879", "3", "9");
        array("8880", "2", "0");
        array("8881", "2", "21");
        array("8882", "2", "7");
        array("8883", "4", "14");
        array("8884", "3", "0");
        array("8885", "9", "6");
        array("8886", "3", "6");
        array("8887", "8", "20");
        array("8888", "1", "1");
        array("8889", "1", "5");
        array("8890", "0", "11");
        array("8891", "9", "15");
        array("8892", "4", "0");
        array("8893", "2", "15");
        array("8894", "0", "18");
        array("8895", "8", "12");
        array("8896", "4", "20");
        array("8897", "0", "6");
        array("8898", "4", "9");
        array("8899", "2", "21");
        array("87100", "7", "0");
        array("87108", "7", "0");
        array("87109", "7", "0");
        array("87110", "7", "0");
        array("87118", "7", "0");
        array("87119", "7", "0");
        array("8712", "9", "6");
        array("87130", "3", "4");
        array("87138", "3", "4");
        array("87139", "3", "4");
        array("8714", "9", "15");
        array("87150", "3", "4");
        array("87158", "3", "4");
        array("87159", "3", "4");
        array("87160", "3", "4");
        array("87168", "3", "4");
        array("87169", "3", "4");
        array("87170", "3", "4");
        array("87178", "1", "5");
        array("87179", "1", "5");
        array("87180", "1", "5");
        array("87188", "1", "5");
        array("87189", "1", "5");
        array("87190", "1", "5");
        array("87198", "1", "5");
        array("87199", "1", "5");
        array("87209", "3", "16");
        array("87200", "1", "5");
        array("87208", "1", "5");
        array("87210", "3", "16");
        array("87218", "3", "16");
        array("87219", "3", "16");
        array("8722", "1", "0");
        array("87230", "3", "16");
        array("87238", "3", "16");
        array("87239", "3", "16");
        array("87240", "3", "16");
        array("87248", "3", "16");
        array("87249", "3", "16");
        array("87250", "1", "8");
        array("87258", "1", "8");
        array("87259", "1", "8");
        array("8726", "1", "20");
        array("87270", "1", "8");
        array("87278", "1", "8");
        array("87279", "1", "8");
        array("87280", "1", "8");
        array("87288", "1", "8");
        array("87289", "1", "8");
        array("87298", "0", "20");
        array("87299", "0", "20");
        array("87290", "1", "8");
        array("87300", "0", "20");
        array("87308", "0", "20");
        array("87309", "0", "20");
        array("87310", "0", "20");
        array("87318", "0", "20");
        array("87319", "0", "20");
        array("87329", "11", "2");
        array("87320", "0", "20");
        array("87328", "0", "20");
        array("87330", "11", "2");
        array("87338", "11", "2");
        array("87339", "11", "2");
        array("87340", "11", "2");
        array("87348", "11", "2");
        array("87349", "11", "2");
        array("87350", "11", "2");
        array("87358", "11", "2");
        array("87359", "11", "2");
        array("87360", "11", "20");
        array("87358", "11", "20");
        array("87359", "11", "20");
        array("87370", "11", "20");
        array("87358", "11", "20");
        array("87359", "11", "20");
        array("87380", "11", "20");
        array("87358", "11", "20");
        array("87359", "11", "20");
        array("87398", "1", "11");
        array("87399", "1", "11");
        array("87390", "11", "20");
        array("87400", "1", "11");
        array("87408", "1", "11");
        array("87409", "1", "11");
        array("87410", "1", "11");
        array("87418", "1", "11");
        array("87419", "1", "11");
        array("87429", "1", "7");
        array("87420", "1", "11");
        array("87428", "1", "11");
        array("87430", "1", "7");
        array("87438", "1", "7");
        array("87439", "1", "7");
        array("87440", "1", "7");
        array("87448", "1", "7");
        array("87449", "1", "7");
        array("87450", "1", "7");
        array("87458", "1", "7");
        array("87459", "1", "7");
        array("87460", "1", "0");
        array("87468", "1", "0");
        array("87469", "1", "0");
        array("87470", "1", "0");
        array("87478", "1", "0");
        array("87479", "1", "0");
        array("87480", "1", "0");
        array("87488", "1", "0");
        array("87489", "1", "0");
        array("87498", "4", "16");
        array("87499", "4", "16");
        array("87490", "1", "0");
        array("8750", "1", "7");
        array("87510", "4", "16");
        array("87518", "4", "16");
        array("87519", "4", "16");
        array("87520", "4", "16");
        array("87528", "4", "16");
        array("87529", "4", "16");
        array("87530", "4", "16");
        array("87538", "4", "16");
        array("87539", "0", "16");
        array("8754", "0", "14");
        array("8755", "0", "21");
        array("8756", "0", "20");
        array("8757", "0", "3");
        array("8758", "3", "2");
        array("8759", "4", "10");
        array("8760", "4", "14");
        array("87610", "0", "16");
        array("87618", "0", "16");
        array("87619", "0", "16");
        array("8762", "8", "0");
        array("8763", "8", "12");
        array("8764", "8", "11");
        array("8765", "8", "20");
        array("8766", "2", "11");
        array("8767", "2", "9");
        array("8768", "3", "10");
        array("8769", "0", "11");
        array("8790", "0", "6");
        array("8791", "9", "21");
        array("8792", "9", "0");
        array("8793", "9", "1");
        array("8794", "3", "20");
        array("8795", "3", "20");
        array("8796", "4", "1");
        array("8797", "9", "3");
        array("8798", "2", "10");
        array("8799", "2", "20");
        array("8600", "0", "1");
        array("8601", "3", "20");
        array("8602", "9", "5");
        array("8603", "9", "3");
        array("8604", "9", "20");
        array("8605", "1", "1");
        array("8606", "1", "15");
        array("8607", "1", "17");
        array("8608", "1", "14");
        array("8609", "1", "10");
        array("86200", "11", "1");
        array("86208", "11", "1");
        array("86209", "11", "1");
        array("86210", "11", "1");
        array("86218", "11", "1");
        array("86219", "11", "1");
        array("86220", "11", "1");
        array("86228", "11", "1");
        array("86229", "11", "1");
        array("86230", "11", "1");
        array("86238", "11", "1");
        array("86239", "11", "1");
        array("86240", "11", "1");
        array("86248", "11", "1");
        array("86249", "11", "1");
        array("86250", "11", "1");
        array("86258", "11", "1");
        array("86259", "11", "1");
        array("86268", "0", "18");
        array("86269", "0", "18");
        array("86260", "11", "1");
        array("86270", "0", "18");
        array("86278", "0", "18");
        array("86279", "0", "18");
        array("86280", "0", "18");
        array("86288", "0", "18");
        array("86289", "0", "18");
        array("86290", "0", "18");
        array("86298", "0", "18");
        array("86299", "7", "10");
        array("86400", "7", "10");
        array("86408", "7", "10");
        array("86409", "7", "10");
        array("86410", "7", "10");
        array("86418", "7", "10");
        array("86419", "7", "10");
        array("86420", "7", "10");
        array("86428", "7", "10");
        array("86429", "7", "10");
        array("86430", "7", "14");
        array("86438", "7", "14");
        array("86439", "7", "14");
        array("86440", "7", "14");
        array("86448", "7", "14");
        array("86449", "7", "14");
        array("86450", "7", "14");
        array("86458", "7", "14");
        array("86459", "7", "14");
        array("86468", "7", "1");
        array("86469", "7", "1");
        array("86460", "7", "14");
        array("86470", "7", "1");
        array("86478", "7", "1");
        array("86479", "7", "1");
        array("86480", "7", "1");
        array("86488", "7", "1");
        array("86489", "7", "1");
        array("86490", "7", "1");
        array("86498", "7", "1");
        array("86499", "4", "5");
        array("8650", "3", "21");
        array("8651", "1", "3");
        array("8652", "1", "9");
        array("8653", "9", "10");
        array("86540", "4", "5");
        array("86548", "4", "5");
        array("86549", "4", "5");
        array("8655", "9", "9");
        array("86560", "4", "5");
        array("86568", "4", "5");
        array("86569", "4", "5");
        array("8657", "2", "1");
        array("8658", "0", "12");
        array("86590", "4", "5");
        array("86598", "4", "5");
        array("86599", "4", "5");
        array("8670", "0", "10");
        array("86710", "4", "2");
        array("86718", "4", "2");
        array("86719", "4", "2");
        array("86720", "4", "2");
        array("86728", "4", "2");
        array("86729", "4", "2");
        array("86730", "4", "2");
        array("86738", "4", "2");
        array("86739", "4", "2");
        array("86748", "3", "3");
        array("86749", "3", "3");
        array("86740", "4", "2");
        array("8675", "4", "14");
        array("86760", "3", "3");
        array("86768", "3", "3");
        array("86769", "3", "3");
        array("86770", "3", "3");
        array("86778", "3", "3");
        array("86779", "3", "3");
        array("86780", "3", "3");
        array("86788", "3", "3");
        array("86789", "1", "14");
        array("8679", "4", "18");
        array("86800", "1", "14");
        array("86808", "1", "14");
        array("86809", "1", "14");
        array("86810", "1", "14");
        array("86818", "1", "14");
        array("86819", "1", "14");
        array("86820", "1", "14");
        array("86828", "1", "14");
        array("86829", "1", "14");
        array("86830", "1", "17");
        array("86838", "1", "17");
        array("86839", "1", "17");
        array("86840", "1", "17");
        array("86848", "1", "17");
        array("86849", "1", "17");
        array("86850", "1", "17");
        array("86858", "1", "17");
        array("86859", "1", "17");
        array("8686", "4", "6");
        array("8687", "2", "20");
        array("8688", "2", "6");
        array("86890", "1", "17");
        array("86898", "1", "9");
        array("86899", "1", "9");
        array("8690", "2", "2");
        array("86910", "1", "9");
        array("86918", "1", "9");
        array("86919", "1", "9");
        array("86920", "1", "9");
        array("86928", "1", "9");
        array("86929", "1", "9");
        array("86939", "7", "0");
        array("86930", "1", "9");
        array("86938", "1", "9");
        array("86940", "7", "0");
        array("86948", "7", "0");
        array("86949", "7", "0");
        array("8695", "2", "14");
        array("8696", "3", "11");
        array("8697", "3", "1");
        array("8698", "3", "1");
        array("8699", "9", "8");
        array("8500", "8", "6");
        array("85010", "1", "6");
        array("85018", "1", "6");
        array("85019", "1", "6");
        array("85020", "1", "11");
        array("85028", "1", "11");
        array("85029", "1", "11");
        array("85030", "1", "11");
        array("85038", "1", "11");
        array("85039", "1", "11");
        array("85040", "1", "11");
        array("85048", "1", "11");
        array("85049", "1", "11");
        array("85058", "1", "7");
        array("85059", "1", "7");
        array("85050", "1", "11");
        array("85060", "1", "7");
        array("85068", "1", "7");
        array("85069", "1", "7");
        array("8507", "4", "3");
        array("8508", "4", "14");
        array("8509", "2", "10");
        array("85100", "1", "7");
        array("85108", "1", "7");
        array("85109", "1", "7");
        array("8511", "0", "2");
        array("85120", "1", "7");
        array("85128", "1", "7");
        array("85129", "1", "10");
        array("85130", "1", "10");
        array("85138", "1", "10");
        array("85139", "1", "10");
        array("85140", "1", "10");
        array("85148", "1", "10");
        array("85149", "1", "10");
        array("85150", "1", "10");
        array("85158", "1", "10");
        array("85159", "1", "10");
        array("85160", "3", "5");
        array("85168", "3", "5");
        array("85169", "3", "5");
        array("85170", "3", "5");
        array("85178", "3", "5");
        array("85179", "3", "5");
        array("85180", "3", "5");
        array("85188", "3", "5");
        array("85189", "3", "5");
        array("85198", "11", "6");
        array("85199", "11", "6");
        array("85190", "3", "5");
        array("85200", "11", "6");
        array("85208", "11", "6");
        array("85209", "11", "6");
        array("8521", "0", "3");
        array("85220", "11", "6");
        array("85228", "11", "6");
        array("85229", "11", "6");
        array("85230", "11", "6");
        array("85238", "11", "6");
        array("85239", "11", "14");
        array("85240", "11", "14");
        array("85248", "11", "14");
        array("85249", "11", "14");
        array("85250", "11", "14");
        array("85258", "11", "14");
        array("85259", "11", "14");
        array("8526", "4", "14");
        array("8527", "0", "7");
        array("8528", "12", "8");
        array("8529", "12", "17");
        array("8530", "12", "2");
        array("85310", "11", "14");
        array("85318", "11", "14");
        array("85319", "11", "14");
        array("85320", "11", "21");
        array("85328", "11", "21");
        array("85329", "11", "21");
        array("85330", "11", "21");
        array("85338", "11", "21");
        array("85339", "11", "21");
        array("85340", "11", "21");
        array("85348", "11", "21");
        array("85349", "11", "21");
        array("85350", "11", "21");
        array("85358", "11", "10");
        array("85359", "11", "10");
        array("85360", "11", "10");
        array("85368", "11", "10");
        array("85369", "11", "10");
        array("85370", "11", "10");
        array("85378", "11", "10");
        array("85379", "11", "10");
        array("85380", "11", "10");
        array("85388", "11", "10");
        array("85389", "11", "3");
        array("85390", "11", "3");
        array("85398", "11", "3");
        array("85399", "11", "3");
        array("85400", "11", "3");
        array("85408", "11", "3");
        array("85409", "11", "3");
        array("85410", "11", "3");
        array("85418", "11", "3");
        array("85419", "11", "3");
        array("85420", "11", "20");
        array("85428", "11", "20");
        array("85429", "11", "20");
        array("85430", "11", "20");
        array("85438", "11", "20");
        array("85439", "11", "20");
        array("8544", "8", "3");
        array("85450", "11", "20");
        array("85458", "11", "20");
        array("85459", "11", "20");
        array("85460", "11", "20");
        array("85468", "11", "0");
        array("85469", "11", "0");
        array("8547", "8", "15");
        array("85480", "11", "0");
        array("85488", "11", "0");
        array("85489", "11", "0");
        array("85490", "11", "0");
        array("85498", "11", "0");
        array("85499", "11", "0");
        array("85500", "11", "0");
        array("85508", "11", "0");
        array("85509", "3", "1");
        array("85510", "3", "1");
        array("85518", "3", "1");
        array("85519", "3", "1");
        array("85520", "3", "1");
        array("85528", "3", "1");
        array("85529", "3", "1");
        array("8553", "4", "0");
        array("85540", "3", "1");
        array("85548", "3", "1");
        array("85549", "3", "1");
        array("85560", "3", "8");
        array("85568", "3", "8");
        array("85569", "3", "8");
        array("85570", "3", "8");
        array("85578", "3", "8");
        array("85579", "3", "8");
        array("85580", "3", "8");
        array("85588", "3", "8");
        array("85589", "3", "8");
        array("85590", "3", "8");
        array("85598", "4", "11");
        array("85599", "4", "11");
        array("85600", "4", "11");
        array("85608", "4", "11");
        array("85609", "4", "11");
        array("85610", "4", "11");
        array("85618", "4", "11");
        array("85619", "4", "11");
        array("85620", "4", "11");
        array("85628", "4", "11");
        array("85629", "4", "20");
        array("85630", "4", "20");
        array("85638", "4", "20");
        array("85639", "4", "20");
        array("85640", "4", "20");
        array("85648", "4", "20");
        array("85649", "4", "20");
        array("85650", "4", "20");
        array("85658", "4", "20");
        array("85659", "4", "20");
        array("85660", "4", "8");
        array("85668", "4", "8");
        array("85669", "4", "8");
        array("85670", "4", "8");
        array("85678", "4", "8");
        array("85679", "4", "8");
        array("85680", "4", "8");
        array("85688", "4", "8");
        array("85689", "4", "8");
        array("85690", "4", "8");
        array("85698", "0", "17");
        array("85699", "0", "17");
        array("85700", "0", "17");
        array("85708", "0", "17");
        array("85709", "0", "17");
        array("85710", "0", "17");
        array("85718", "0", "17");
        array("85719", "0", "17");
        array("85720", "0", "17");
        array("85728", "0", "17");
        array("85729", "1", "20");
        array("85730", "1", "20");
        array("85738", "1", "20");
        array("85739", "1", "20");
        array("8574", "4", "20");
        array("8575", "4", "20");
        array("85760", "1", "20");
        array("85768", "1", "20");
        array("85769", "1", "20");
        array("85770", "1", "20");
        array("85778", "1", "20");
        array("85779", "1", "20");
        array("85780", "1", "3");
        array("85788", "1", "3");
        array("85789", "1", "3");
        array("85790", "1", "3");
        array("85798", "1", "3");
        array("85799", "1", "3");
        array("8580", "8", "3");
        array("85810", "1", "3");
        array("85818", "1", "3");
        array("85819", "1", "3");
        array("85820", "1", "3");
        array("85828", "0", "1");
        array("85829", "0", "1");
        array("85830", "0", "1");
        array("85838", "0", "1");
        array("85839", "0", "1");
        array("85840", "0", "1");
        array("85848", "0", "1");
        array("85849", "0", "1");
        array("85859", "3", "7");
        array("85850", "0", "1");
        array("85858", "0", "1");
        array("85860", "3", "7");
        array("85868", "3", "7");
        array("85869", "3", "7");
        array("85870", "3", "7");
        array("85878", "3", "7");
        array("85879", "3", "7");
        array("85880", "3", "7");
        array("85888", "3", "7");
        array("85889", "3", "7");
        array("85890", "3", "15");
        array("85898", "3", "15");
        array("85899", "3", "15");
        array("8590", "2", "15");
        array("8591", "2", "8");
        array("85920", "3", "15");
        array("85928", "3", "15");
        array("85929", "3", "15");
        array("85930", "3", "15");
        array("85938", "3", "15");
        array("85939", "3", "15");
        array("85940", "3", "15");
        array("85948", "3", "12");
        array("85949", "3", "12");
        array("8595", "2", "7");
        array("85960", "3", "12");
        array("85968", "3", "12");
        array("85969", "3", "12");
        array("8597", "2", "3");
        array("85980", "3", "12");
        array("85988", "3", "12");
        array("85989", "3", "12");
        array("85999", "11", "1");
        array("85990", "3", "12");
        array("85998", "3", "12");
        array("8400", "0", "20");
        array("8401", "11", "2");
        array("84020", "3", "16");
        array("84028", "3", "16");
        array("84029", "3", "16");
        array("84030", "3", "16");
        array("84038", "3", "16");
        array("84039", "3", "16");
        array("84040", "3", "16");
        array("84048", "3", "3");
        array("84049", "3", "3");
        array("84050", "3", "3");
        array("84058", "3", "3");
        array("84059", "3", "3");
        array("84060", "3", "3");
        array("84068", "3", "3");
        array("84069", "3", "3");
        array("84070", "3", "3");
        array("84078", "3", "3");
        array("84079", "3", "1");
        array("84080", "3", "1");
        array("84088", "3", "1");
        array("84089", "3", "1");
        array("8409", "11", "3");
        array("8410", "4", "21");
        array("84110", "3", "1");
        array("84118", "3", "1");
        array("84119", "3", "1");
        array("84120", "3", "1");
        array("84128", "3", "1");
        array("84129", "3", "1");
        array("84130", "0", "20");
        array("84138", "0", "20");
        array("84139", "0", "20");
        array("84140", "0", "20");
        array("84148", "0", "20");
        array("84149", "0", "20");
        array("84150", "0", "20");
        array("84158", "0", "20");
        array("84159", "0", "20");
        array("84160", "0", "20");
        array("84168", "1", "20");
        array("84169", "1", "20");
        array("84170", "1", "20");
        array("84178", "1", "20");
        array("84179", "1", "20");
        array("84180", "1", "20");
        array("84188", "1", "20");
        array("84189", "1", "20");
        array("84190", "1", "20");
        array("84198", "1", "20");
        array("84199", "1", "9");
        array("8420", "0", "1");
        array("8421", "11", "1");
        array("84220", "1", "9");
        array("84228", "1", "9");
        array("84229", "1", "9");
        array("8423", "11", "20");
        array("84240", "1", "9");
        array("84248", "1", "9");
        array("84249", "1", "9");
        array("84250", "1", "9");
        array("84258", "1", "9");
        array("84259", "1", "9");
        array("84260", "1", "11");
        array("84268", "1", "11");
        array("84269", "1", "11");
        array("8427", "0", "8");
        array("8428", "12", "14");
        array("8429", "2", "20");
        array("8430", "2", "21");
        array("8431", "2", "0");
        array("8432", "7", "11");
        array("8433", "7", "9");
        array("8434", "7", "3");
        array("8435", "1", "5");
        array("8436", "11", "10");
        array("8437", "1", "8");
        array("8438", "9", "14");
        array("8439", "9", "21");
        array("84400", "1", "11");
        array("84408", "1", "11");
        array("84409", "1", "11");
        array("84410", "1", "11");
        array("84418", "1", "11");
        array("84419", "1", "11");
        array("84420", "1", "11");
        array("84428", "2", "1");
        array("84429", "2", "1");
        array("84430", "2", "1");
        array("84438", "2", "1");
        array("84439", "2", "1");
        array("84440", "2", "1");
        array("84448", "2", "1");
        array("84449", "2", "1");
        array("8445", "11", "21");
        array("8446", "9", "1");
        array("8447", "3", "7");
        array("8448", "2", "9");
        array("8449", "1", "21");
        array("84500", "2", "1");
        array("84508", "2", "1");
        array("84509", "0", "9");
        array("84510", "0", "9");
        array("84518", "0", "9");
        array("84519", "0", "9");
        array("84520", "0", "9");
        array("84528", "0", "9");
        array("84529", "0", "9");
        array("8453", "7", "0");
        array("84540", "0", "9");
        array("84548", "0", "9");
        array("84549", "0", "9");
        array("84550", "0", "12");
        array("84558", "0", "12");
        array("84559", "0", "12");
        array("84560", "0", "12");
        array("84568", "0", "12");
        array("84569", "0", "12");
        array("84570", "0", "12");
        array("84578", "0", "12");
        array("84579", "0", "12");
        array("84588", "1", "5");
        array("84589", "1", "5");
        array("84580", "0", "12");
        array("8459", "7", "7");
        array("8460", "9", "2");
        array("84610", "1", "5");
        array("84618", "1", "5");
        array("84619", "1", "5");
        array("84620", "1", "5");
        array("84628", "1", "5");
        array("84629", "1", "5");
        array("84639", "1", "6");
        array("84630", "1", "5");
        array("84638", "1", "5");
        array("84640", "1", "6");
        array("84648", "1", "6");
        array("84649", "1", "6");
        array("84650", "1", "6");
        array("84658", "1", "6");
        array("84659", "1", "6");
        array("84660", "1", "6");
        array("84668", "1", "6");
        array("84669", "1", "6");
        array("84670", "2", "7");
        array("84678", "2", "7");
        array("84679", "2", "7");
        array("84680", "2", "7");
        array("84688", "2", "7");
        array("84689", "2", "7");
        array("8469", "3", "2");
        array("84700", "2", "7");
        array("84708", "2", "7");
        array("84709", "2", "7");
        array("84710", "2", "7");
        array("84718", "0", "16");
        array("84719", "0", "16");
        array("84720", "0", "16");
        array("84728", "0", "16");
        array("84729", "0", "16");
        array("84730", "0", "16");
        array("84738", "0", "16");
        array("84739", "0", "16");
        array("84740", "0", "16");
        array("84748", "0", "16");
        array("84749", "1", "21");
        array("84750", "1", "21");
        array("84758", "1", "21");
        array("84759", "1", "21");
        array("84760", "1", "21");
        array("84768", "1", "21");
        array("84769", "1", "21");
        array("84770", "1", "21");
        array("84778", "1", "21");
        array("84779", "1", "21");
        array("84780", "1", "1");
        array("84788", "1", "1");
        array("84789", "1", "1");
        array("84790", "1", "1");
        array("84798", "1", "1");
        array("84799", "1", "1");
        array("8480", "8", "20");
        array("84810", "1", "1");
        array("84818", "1", "1");
        array("84819", "1", "1");
        array("84820", "1", "1");
        array("84828", "11", "1");
        array("84829", "11", "1");
        array("84830", "11", "1");
        array("84838", "11", "1");
        array("84839", "11", "1");
        array("84840", "11", "1");
        array("84848", "11", "1");
        array("84849", "11", "1");
        array("84850", "11", "1");
        array("84858", "11", "1");
        array("84859", "11", "2");
        array("8486", "3", "16");
        array("84870", "11", "2");
        array("84878", "11", "2");
        array("84879", "11", "2");
        array("84880", "11", "2");
        array("84888", "11", "2");
        array("84889", "11", "2");
        array("8489", "3", "14");
        array("84900", "11", "2");
        array("84908", "11", "2");
        array("84909", "11", "2");
        array("84910", "0", "4");
        array("84918", "0", "4");
        array("84919", "0", "4");
        array("84920", "0", "4");
        array("84928", "0", "4");
        array("84929", "0", "4");
        array("84930", "0", "4");
        array("84938", "0", "4");
        array("84939", "0", "4");
        array("84940", "0", "4");
        array("84948", "1", "0");
        array("84949", "1", "0");
        array("84950", "1", "0");
        array("84958", "1", "0");
        array("84959", "1", "0");
        array("84960", "1", "0");
        array("84968", "1", "0");
        array("84969", "1", "0");
        array("84979", "1", "6");
        array("84970", "1", "0");
        array("84978", "1", "0");
        array("84980", "1", "6");
        array("84988", "1", "6");
        array("84989", "1", "6");
        array("84990", "1", "6");
        array("84998", "1", "6");
        array("84999", "1", "6");
        array("8300", "8", "14");
        array("83010", "8", "15");
        array("83018", "8", "15");
        array("83019", "8", "15");
        array("8302", "2", "11");
        array("8303", "2", "20");
        array("83040", "8", "15");
        array("83048", "8", "15");
        array("83049", "8", "15");
        array("8305", "2", "5");
        array("8306", "2", "2");
        array("8307", "2", "21");
        array("8308", "1", "1");
        array("83300", "8", "15");
        array("83308", "8", "15");
        array("83309", "8", "6");
        array("83310", "8", "6");
        array("83318", "8", "6");
        array("83319", "8", "6");
        array("83320", "8", "6");
        array("83328", "8", "6");
        array("83329", "8", "6");
        array("83330", "8", "6");
        array("83338", "8", "6");
        array("83339", "8", "6");
        array("83340", "3", "1");
        array("83348", "3", "1");
        array("83349", "3", "1");
        array("83350", "3", "1");
        array("83358", "3", "1");
        array("83359", "3", "1");
        array("83360", "3", "1");
        array("83368", "3", "1");
        array("83369", "3", "1");
        array("83370", "3", "1");
        array("83379", "3", "12");
        array("83380", "3", "12");
        array("83388", "3", "12");
        array("83389", "3", "12");
        array("83390", "3", "12");
        array("83398", "3", "12");
        array("83399", "3", "12");
        array("8341", "11", "6");
        array("83420", "3", "12");
        array("83428", "3", "12");
        array("83429", "3", "12");
        array("83430", "1", "10");
        array("83438", "1", "10");
        array("83439", "1", "10");
        array("8344", "4", "14");
        array("83450", "1", "10");
        array("83458", "1", "10");
        array("83459", "1", "10");
        array("83460", "1", "10");
        array("83468", "1", "10");
        array("83469", "1", "10");
        array("8347", "1", "2");
        array("8348", "3", "10");
        array("8349", "0", "5");
        array("83500", "1", "10");
        array("83508", "1", "18");
        array("83509", "1", "18");
        array("83510", "1", "18");
        array("83518", "1", "18");
        array("83519", "1", "18");
        array("83520", "1", "18");
        array("83528", "1", "18");
        array("83529", "1", "18");
        array("83530", "1", "18");
        array("83538", "1", "18");
        array("83539", "11", "20");
        array("83540", "11", "20");
        array("83548", "11", "20");
        array("83549", "11", "20");
        array("83550", "11", "20");
        array("83558", "11", "9");
        array("83559", "11", "9");
        array("83560", "11", "9");
        array("83568", "11", "9");
        array("83569", "11", "9");
        array("83570", "3", "5");
        array("83578", "3", "5");
        array("83579", "3", "5");
        array("83580", "3", "5");
        array("83588", "3", "5");
        array("83589", "3", "5");
        array("83590", "3", "5");
        array("83598", "3", "5");
        array("83599", "3", "5");
        array("83700", "3", "5");
        array("83708", "3", "10");
        array("83709", "3", "10");
        array("83710", "3", "10");
        array("83718", "3", "10");
        array("83719", "3", "10");
        array("83720", "3", "10");
        array("83728", "3", "10");
        array("83729", "3", "10");
        array("83730", "3", "10");
        array("83738", "3", "10");
        array("83739", "3", "7");
        array("8374", "0", "6");
        array("83750", "3", "7");
        array("83758", "3", "7");
        array("83759", "3", "7");
        array("83760", "3", "7");
        array("83768", "3", "7");
        array("83769", "3", "7");
        array("83770", "3", "7");
        array("83778", "3", "7");
        array("83779", "3", "7");
        array("83780", "1", "1");
        array("83788", "1", "1");
        array("83789", "1", "1");
        array("83790", "1", "1");
        array("83798", "1", "1");
        array("83799", "1", "1");
        array("83800", "1", "1");
        array("83808", "1", "1");
        array("83809", "1", "1");
        array("83810", "1", "1");
        array("83818", "11", "20");
        array("83819", "11", "20");
        array("83820", "11", "20");
        array("83828", "11", "20");
        array("83829", "11", "20");
        array("83830", "11", "9");
        array("83838", "11", "9");
        array("83839", "11", "9");
        array("83840", "11", "9");
        array("83848", "11", "9");
        array("83849", "4", "11");
        array("83850", "4", "11");
        array("83858", "4", "11");
        array("83859", "4", "11");
        array("83860", "4", "11");
        array("83868", "4", "11");
        array("83869", "4", "11");
        array("83870", "4", "11");
        array("83878", "4", "11");
        array("83879", "4", "11");
        array("83880", "2", "10");
        array("83888", "2", "10");
        array("83889", "2", "10");
        array("83890", "2", "10");
        array("83898", "2", "10");
        array("83899", "2", "10");
        array("8390", "3", "1");
        array("83910", "2", "10");
        array("83918", "2", "10");
        array("83919", "2", "10");
        array("83920", "2", "10");
        array("83928", "3", "21");
        array("83929", "3", "21");
        array("83930", "3", "21");
        array("83938", "3", "21");
        array("83939", "3", "21");
        array("83940", "3", "21");
        array("83948", "3", "21");
        array("83949", "3", "21");
        array("83950", "3", "21");
        array("83958", "3", "21");
        array("83959", "3", "17");
        array("83960", "3", "17");
        array("83968", "3", "17");
        array("83969", "3", "17");
        array("83970", "3", "17");
        array("83978", "3", "17");
        array("83979", "3", "17");
        array("83980", "3", "17");
        array("83988", "3", "17");
        array("83989", "3", "17");
        array("83990", "3", "16");
        array("83998", "3", "16");
        array("83999", "3", "16");
        array("8220", "0", "14");
        array("8233", "9", "11");
        array("8235", "9", "3");
        array("8237", "9", "1");
        array("8238", "3", "2");
        array("8239", "3", "11");
        array("8252", "2", "3");
        array("8260", "2", "12");
        array("82610", "2", "18");
        array("82618", "2", "18");
        array("82619", "2", "18");
        array("82620", "2", "18");
        array("82628", "2", "18");
        array("82629", "2", "18");
        array("82630", "2", "18");
        array("82638", "2", "18");
        array("82639", "2", "18");
        array("8264", "12", "2");
        array("82650", "2", "18");
        array("82658", "9", "21");
        array("82659", "9", "21");
        array("82660", "9", "21");
        array("82668", "9", "21");
        array("82669", "9", "21");
        array("82670", "9", "21");
        array("82678", "9", "21");
        array("82679", "9", "21");
        array("8268", "12", "9");
        array("8269", "12", "5");
        array("8270", "11", "12");
        array("8271", "11", "3");
        array("82720", "9", "21");
        array("82728", "9", "21");
        array("82729", "9", "1");
        array("8273", "11", "21");
        array("82740", "9", "1");
        array("82748", "9", "1");
        array("82749", "9", "1");
        array("8275", "8", "1");
        array("82760", "9", "1");
        array("82768", "9", "1");
        array("82769", "9", "1");
        array("8277", "8", "0");
        array("8278", "8", "17");
        array("8279", "8", "11");
        array("8280", "8", "12");
        array("8281", "8", "15");
        array("82820", "9", "1");
        array("82828", "9", "1");
        array("82829", "9", "1");
        array("82830", "0", "8");
        array("82838", "0", "8");
        array("82839", "0", "8");
        array("82840", "0", "8");
        array("82848", "0", "8");
        array("82849", "0", "8");
        array("8285", "4", "7");
        array("8286", "4", "9");
        array("8287", "2", "7");
        array("82880", "0", "8");
        array("82888", "0", "8");
        array("82889", "0", "8");
        array("82890", "0", "8");
        array("82898", "8", "15");
        array("82899", "8", "15");
        array("8290", "0", "11");
        array("8291", "11", "9");
        array("8292", "0", "3");
        array("8293", "9", "10");
        array("8294", "0", "3");
        array("8295", "0", "17");
        array("8296", "11", "1");
        array("8297", "3", "6");
        array("8298", "3", "3");
        array("8100", "2", "1");
        array("8101", "2", "10");
        array("8102", "2", "3");
        array("8103", "2", "5");
        array("8104", "7", "11");
        array("8105", "0", "0");
        array("8106", "0", "6");
        array("8107", "0", "11");
        array("8108", "1", "9");
        array("8109", "9", "5");
        array("8115", "11", "20");
        array("8116", "0", "10");
        array("8120", "1", "5");
        array("8121", "9", "6");
        array("8122", "9", "14");
        array("8123", "9", "0");
        array("8124", "1", "14");
        array("8125", "9", "6");
        array("8126", "0", "21");
        array("8127", "0", "20");
        array("8128", "0", "2");
        array("8129", "0", "15");
        array("8130", "0", "7");
        array("8140", "1", "2");
        array("8141", "3", "2");
        array("8142", "3", "6");
        array("8143", "9", "6");
        array("8144", "2", "14");
        array("8145", "3", "10");
        array("8146", "0", "8");
        array("8147", "9", "0");
        array("8148", "9", "14");
        array("8149", "9", "1");
        array("8171", "0", "21");
        array("8179", "0", "6");
        array("8197", "0", "0");

        array("8000", "2", "2");
        array("8001", "3", "10");
        array("8002", "0", "3");
        array("8003", "0", "11");
        array("8004", "8", "20");
        array("80050", "8", "20");
        array("80051", "8", "20");
        array("80052", "8", "20");
        array("80053", "8", "20");
        array("80054", "8", "20");
        array("8006", "3", "21");
        array("8007", "3", "1");
        array("8008", "0", "6");
        array("8009", "0", "20");
        array("8010", "2", "7");
        array("8011", "0", "16");
        array("8012", "4", "14");
        array("8013", "4", "1");
        array("8014", "4", "20");
        array("8015", "9", "14");
        array("8016", "0", "10");
        array("8017", "3", "1");
        array("8018", "0", "12");
        array("8019", "9", "6");
        array("8050", "9", "0");
        array("8051", "3", "3");
        array("8052", "3", "20");
        array("8053", "3", "17");
        array("8054", "3", "8");
        array("8055", "2", "1");
        array("8056", "0", "14");
        array("8057", "1", "21");
        array("8058", "1", "11");
        array("8059", "1", "17");
        array("8080", "2", "9");
        array("8081", "2", "20");
        array("8082", "6", "9");
        array("8083", "4", "3");
        array("8084", "0", "3");
        array("8085", "0", "5");
        array("8086", "3", "15");
        array("8087", "9", "1");
        array("8088", "2", "0");
        array("8089", "9", "15");
        array("8090", "9", "20");
        array("8091", "9", "18");
        array("8092", "9", "3");
        array("8093", "9", "12");
        array("8094", "3", "11");
        array("8095", "3", "0");
        array("8096", "1", "6");
        array("8097", "9", "9");
        array("8098", "3", "14");
        array("8099", "2", "6");
        array("7800", "3", "20");
        array("7814", "2", "8");
        array("7827", "2", "7");
        array("7828", "2", "5");
        array("7829", "3", "0");
        array("7830", "3", "21");
        array("7837", "3", "8");
        array("7838", "3", "7");
        array("7839", "8", "20");
        array("7842", "9", "6");
        array("7845", "9", "14");
        array("7860", "1", "20");
        array("7869", "0", "5");
        array("7870", "11", "3");
        array("7871", "11", "14");
        array("7872", "3", "10");
        array("7873", "3", "12");
        array("7874", "3", "2");
        array("7875", "3", "1");
        array("7876", "2", "17");
        array("7877", "2", "11");
        array("7878", "2", "2");
        array("7879", "2", "5");
        array("7890", "1", "1");
        array("7891", "1", "11");
        array("7893", "0", "6");
        array("7894", "0", "12");
        array("7895", "0", "21");
        array("7896", "0", "16");
        array("7897", "0", "20");
        array("7898", "0", "5");
        array("7899", "11", "0");
        array("7702", "0", "6");
        array("7708", "0", "14");
        array("7709", "0", "1");
        array("7735", "2", "12");
        array("7736", "9", "15");
        array("7737", "9", "11");
        array("7738", "0", "9");
        array("7739", "0", "3");
        array("7742", "0", "11");
        array("7760", "0", "0");
        array("7795", "9", "0");
        array("7796", "9", "7");
        array("7797", "3", "10");
        array("7798", "3", "1");
        array("7799", "3", "6");
        array("7600", "0", "2");
        array("7602", "0", "10");
        array("7607", "0", "20");
        array("7620", "2", "1");
        array("7631", "3", "3");
        array("7639", "3", "14");
        array("7654", "1", "3");
        array("7665", "3", "11");
        array("7666", "2", "9");
        array("7667", "2", "14");
        array("7668", "2", "20");
        array("7669", "2", "21");
        array("7676", "2", "0");
        array("7677", "2", "3");
        array("7679", "2", "10");
        array("7696", "9", "8");
        array("7697", "1", "5");
        array("7698", "1", "2");
        array("7699", "1", "10");
        array("7500", "1", "21");
        array("7501", "9", "10");
        array("7502", "4", "14");
        array("7503", "4", "7");
        array("7504", "4", "12");
        array("7505", "2", "20");
        array("7506", "3", "9");
        array("7507", "3", "1");
        array("7508", "3", "8");
        array("7509", "3", "5");
        array("7520", "2", "21");
        array("7549", "3", "3");
        array("7566", "3", "5");
        array("7567", "3", "2");
        array("7568", "0", "11");
        array("7569", "2", "6");
        array("7579", "8", "21");
        array("7587", "8", "5");
        array("7588", "8", "1");
        array("7589", "8", "8");
        array("7597", "8", "11");
        array("7598", "8", "14");
        array("7599", "8", "21");
        array("7401", "4", "13");
        array("7402", "4", "14");
        array("7403", "4", "15");
        array("7404", "9", "17");
        array("7405", "9", "2");
        array("7406", "3", "0");
        array("7407", "3", "10");
        array("7408", "3", "20");
        array("7409", "3", "21");
        array("7411", "9", "0");
        array("7415", "9", "5");
        array("7416", "9", "6");
        array("7417", "9", "21");
        array("7418", "9", "14");
        array("7419", "2", "1");
        array("7428", "2", "7");
        array("7429", "2", "15");
        array("7439", "2", "1");
        array("7483", "2", "0");
        array("7488", "2", "3");
        array("7489", "2", "5");
        array("7498", "2", "9");
        array("7499", "2", "20");
        array("7302", "1", "9");
        array("7303", "2", "9");
        array("7304", "2", "1");
        array("7305", "2", "14");
        array("7306", "2", "6");
        array("7307", "2", "8");
        array("7308", "2", "20");
        array("7309", "1", "20");
        array("7350", "1", "1");
        array("7351", "1", "21");
        array("7352", "1", "3");
        array("7353", "1", "0");
        array("7354", "1", "5");
        array("7355", "12", "8");
        array("7356", "12", "15");
        array("7357", "12", "17");
        array("7358", "12", "14");
        array("7359", "1", "2");
        array("7373", "4", "14");
        array("7376", "8", "20");
        array("7377", "1", "12");
        array("7379", "3", "20");
        array("7380", "8", "8");
        array("7381", "3", "12");
        array("7382", "8", "6");
        array("7383", "11", "2");
        array("7384", "11", "10");
        array("7385", "11", "1");
        array("7386", "11", "6");
        array("7387", "0", "1");
        array("7388", "0", "20");
        array("7389", "0", "5");
        array("7396", "11", "6");
        array("7398", "11", "20");
        array("7399", "4", "16");
        array("7200", "9", "14");
        array("7204", "9", "0");
        array("7205", "9", "12");
        array("7206", "9", "17");
        array("7207", "9", "6");
        array("7208", "9", "9");
        array("7209", "9", "3");
        array("7250", "0", "3");
        array("7259", "0", "0");
        array("7275", "9", "20");
        array("7276", "9", "1");
        array("7277", "4", "3");
        array("7278", "4", "1");
        array("7293", "4", "15");
        array("7298", "4", "4");
        array("7299", "4", "13");
        array("7840", "1", "7");
        array("8506", "1", "7");
        array("8510", "1", "7");
        array("8745", "1", "7");
        array("9155", "1", "7");
        array("8467", "2", "7");
        array("8470", "2", "7");
        array("8585", "3", "7");
        array("8586", "3", "7");
        array("8587", "3", "7");
        array("8685", "3", "7");
    }

    public void array(String no, String operator, String cir) {
        op_list.add(new Util.operator(no, operator, cir));
    }

    // Key Down Event

    public void keyDown() {
        if(et_conf_mobile.length()>et_mob.length())
        {
            et_conf_mobile.setText("");
        }
        if(et_mob.length()==10)
        {
            if(!isFocused) {
                et_conf_mobile.requestFocus();
                isFocused = true;
            }
                if(et_conf_mobile.length()==10) {

                    if(!isFocusedConf) {
                        et_amount.requestFocus();
                        isFocusedConf = true;
                    }
                }
        }
        try {
            if (et_mob.isSet && et_conf_mobile.isSet && et_amount.isSet && et_conf_amount.isSet && spinnerAdapter.isSet && sp_operator.getSelectedItemPosition() != 0 && sp_sub_operator.getSelectedItemPosition() != 0) {
                btn_next.setButton(true);
            } else {
                btn_next.setButton(false);
            }
            mob = et_mob.getString();
            mob1 = et_conf_mobile.getString();

            if (mob.contains(mob1) != true) {
                // et_mob.requestFocus();
                // et_conf_mobile.setText("");
                et_conf_mobile.setError(getContext().getResources().getString(R.string.err_mobile_no));
            }
            if (isOpSelected == false && mob.length()>3) {
                String circle_name;
                String number = et_mob.getString();
                for (Util.operator elem : op_list) {
                    if (number.contains(elem.getNo())) {
                        circle_name = elem.getCircle();
                        String operator_name = elem.getOperator();
                        sp_operator.setSelection(spinnerAdapter.getPosition(operator_name));
                        sp_sub_operator.setSelection(circleAdapter.getPosition(circle_name));
                        isOpSelected = true;
                        break;
                    }
                }

            }
            /*if (et_mob.getString().length() < 4) {
                isOpSelected = false;
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validate() {

        mob = et_mob.getString();
        mob1 = et_conf_mobile.getString();
        //
        if (mob1.contains(mob) != true) {
            et_conf_mobile.requestFocus();
            et_conf_mobile.setError("");
        }
        amount = et_amount.getInt();
        amount1 = et_conf_amount.getInt();


        if (rb_prepaid.isChecked()) {
            type = rb_prepaid.getText().toString();
            if (rb_topup.isChecked()) {
                param1 = "";
            } else {
                param1 = "-Special";
            }
        } else {
            param1 = "";
            type = rb_postpaid.getText().toString();
        }
        tempSelectedPos = sp_operator.getSelectedItemPosition();
        try {
            operator = "" + opwithLogo.get(tempSelectedPos).getName();
            tempSelectedPos = sp_sub_operator.getSelectedItemPosition();
            sub_operator = op_circle.get(tempSelectedPos).getName();
        } catch (Exception e) {
            e.printStackTrace();

        }
        tempSelectedPos = sp_sub_operator.getSelectedItemPosition();
        sub_operator = op_circle.get(tempSelectedPos).getName();
        pattern = pattern.compile(CONTACT_PATTERN);
        matcher = pattern.matcher(mob);
        if (matcher.matches() && mob1.equalsIgnoreCase(mob)) {
            if (amount == amount1) {
                if (sp_operator.getSelectedItemPosition() != 0) {
                    return true;
                } else {
                    // Error in SP
                    return false;
                }
            } else {
                et_amount.requestFocus();
                et_amount.setError(getActivity().getResources().getString(R.string.amount));
                et_conf_amount.setText("");
                return false;
            }
        } else {
            et_mob.requestFocus();
            et_mob.setError(getActivity().getResources().getString(R.string.valid_number));
            et_conf_mobile.setText("");
            return false;
        }
    }

    @Override
    public void onResume() {
        //chk key Down Event
        keyDown();

        super.onResume();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        keyDown();
    }

    @Override
    public void onClick(View v) {
        keyDown();
    }

    // MAke Recharge
    private class AsyncMakeRecharge extends AsyncTask<Integer, Void, Void> {
        Boolean result = true;
        JSONObject jsonObject = null;
        RestAPI api = new RestAPI();
        ProgressDialog progress;
        String error = "";
        int Errorcode = 0;
        String txn_id="";
        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(context);
            progress.setIndeterminate(true);
            progress.setMessage("Please Wait...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            try {
                String p1= Enc_Dec.enc(String.valueOf(amount));
                String p2=Enc_Dec.enc("");
                String p3=Enc_Dec.enc(type);
                String p4=Enc_Dec.RN();
                String p5=Enc_Dec.enc("");
                String p6=Enc_Dec.RNEC();
                String p7=Enc_Dec.enc(operator);
                String p8=session.E_getUsr_id();
                String p9=Enc_Dec.enc(sub_operator);
                String p10=Enc_Dec.enc(DateFormat.getDateTimeInstance().format(new Date()));
                String p11=Enc_Dec.enc(mob);
                String p12=Enc_Dec.enc(param1);
                jsonObject=api.MKRC(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12);

           //     jsonObject = api.MKRC("", session.getUsr_id(), mob, amount, type, operator, sub_operator, param1, "");
                result = jsonObject.getBoolean("Successful");
                JSONArray jsonArray = null;
                try {
                    jsonArray = jsonObject.getJSONArray("Value");
                } catch (Exception e) {
                    error = jsonObject.getString("ErrorMessage");
                    result = false;
                }
                JSONObject jsonObj = null;
                if (jsonArray.length() == 0) {
                    result = false;
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObj = jsonArray.getJSONObject(i);
                    try {
                        Errorcode = jsonObj.getInt("Error_Code");
                        result = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // this method call when there is no any error
                    if (result) {
                        Errorcode=jsonObj.getInt("Rc_status");
                        txn_id=jsonObj.getString("Txn_id");
                    }
                    else
                    {
                        Errorcode=jsonObj.getInt("Error_Code");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {

                progress.dismiss();
            }
            catch (Exception e)
            {

            }
            try {
                if (result == false) {
                    // on Failuer
                    if (Errorcode == 0) {
                        //paym.paym.mobilerechargeapp.Begain.Toast.makeText(context, "Serious Error : " + error);
                        new AwesomeErrorDialog(context).setTitle(ErrorCode.getErrorcodeTitle(ErrorCode.Rc_Failed)).setMessage(ErrorCode.getMessageString(ErrorCode.SQL_Error)).show();
                    } else {
                        new AwesomeErrorDialog(context).setTitle(ErrorCode.getErrorcodeTitle(Errorcode)).setMessage(ErrorCode.getMessageString(Errorcode)).show();
                        // paym.paym.mobilerechargeapp.Begain.Toast.makeText(context, "Error Code : " + Errorcode);
                    }
                } else {
                    // on Success
                    //paym.paym.mobilerechargeapp.Begain.Toast.makeText(context, "Success : ");
                    new AwesomeSuccessDialog(context).setTitle(ErrorCode.getErrorcodeTitle(Errorcode)).setMessage(ErrorCode.txn_id_msg+txn_id).show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            NewRetailer.openFragment(new RetailerDashboard());
                        }
                    });
                    //   setClearFields();
                }
            }
            catch (Exception e)
            {

            }

        }

        public void setClearFields() {
            et_conf_mobile.setText("");
            et_mob.setText("");
            et_amount.setText("");
            et_conf_amount.setText("");
            sp_operator.setSelection(0);
            sp_sub_operator.setSelection(0);
        }
    }

    private void openContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                Uri contactData = data.getData();

                Cursor c =  getContext().getContentResolver().query(contactData, null, null, null, null);
                if (c.moveToFirst()) {

                    String phoneNumber="",emailAddress="";
                    String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if ( hasPhone.equalsIgnoreCase("1"))
                        hasPhone = "true";
                    else
                        hasPhone = "false" ;

                    if (Boolean.parseBoolean(hasPhone))
                    {
                        Cursor phones = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
                        while (phones.moveToNext())
                        {
                            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                        phones.close();
                    }

                    phoneNumber=phoneNumber.replace("(","");
                    phoneNumber=phoneNumber.replace(")","");
                    phoneNumber=phoneNumber.replace("-","");
                    phoneNumber=phoneNumber.replace("+91","");
                    phoneNumber=phoneNumber.replace(" ","");
                    et_conf_mobile.clearFocus();
                    et_conf_mobile.setText("");
                    et_mob.setText(phoneNumber);
                    et_conf_mobile.setText(phoneNumber);
                    keyDown();
                }
                c.close();
            }
        }
    }
}