package com.seoulapp.sandfox.retax;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * 관광 시 연락할 곳 또는 방문해볼만한 웹사이트 제공
 *
 */
public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

    }
    String toContact = "";
    String website = "";
    String call = "";
    Uri selectedUri;
    ArrayList<String> arrayList = new ArrayList<>();
    public void onClickContact(View view){
        arrayList.clear();

        MainActivity.buttonClickEffect(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (view.getId()){
            case R.id.kto_btn:
                toContact = "kto";
                arrayList.add(getResources().getString(R.string.dial)+"1330");
                arrayList.add(getResources().getString(R.string.visit_web));

                builder.setTitle("Korean Tourist Organization");

                website = "http://www.visitkorea.or.kr/intro.html";
                call = "1330";

                break;
            case R.id.police_btn:
                toContact = "police";
                arrayList.add(getResources().getString(R.string.dial)+"02-700-6276 (Myeong-dong)");
                arrayList.add(getResources().getString(R.string.dial)+"02-700-6195 (Dongdaemun)");
                arrayList.add(getResources().getString(R.string.dial)+"02-700-6295 (Itaewon)");
                arrayList.add(getResources().getString(R.string.dial)+"02-700-6278 (Hongdae)");

                builder.setTitle("Tourist Police");

                break;
            case R.id.dasan_btn:
                toContact = "dasan";
                arrayList.add(getResources().getString(R.string.dial)+"120 -then press 9");
                arrayList.add(getResources().getString(R.string.visit_web));
                builder.setTitle("Dasan Call Center");
                website = "http://120dasan.seoul.go.kr/foreign/english.html";
                call = "120";
                break;
            case R.id.sgc_btn:
                toContact = "sgc";
                arrayList.add(getResources().getString(R.string.dial)+"02-2075-4180");
                arrayList.add(getResources().getString(R.string.visit_web));
                builder.setTitle("Seoul Global Center");
                website = "http://global.seoul.go.kr/index.do";
                call = "02-2075-4180";
                break;
        }

        if(arrayList.size() != 0){
            CharSequence[] contacts = arrayList.toArray(new String[arrayList.size()]);

            if(arrayList.size() == 2){
                builder.setSingleChoiceItems(contacts, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){
                            selectedUri = Uri.parse("tel:"+call);
                        }else{
                            selectedUri = Uri.parse(website);
                        }
                    }
                });

            }else{
                builder.setSingleChoiceItems(contacts, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                selectedUri = Uri.parse("tel:02-700-6276");
                                break;
                            case 1:
                                selectedUri = Uri.parse("tel:02-700-6195");
                                break;
                            case 2:
                                selectedUri = Uri.parse("tel:02-700-6295");
                                break;
                            case 3:
                                selectedUri = Uri.parse("tel:02-700-6278");
                                break;
                        }
                    }
                });
            }

            builder.setNegativeButton("BACK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton("GO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(selectedUri.toString().startsWith("http")){
                        startActivity(new Intent(Intent.ACTION_VIEW, selectedUri));
                    }else{
                        startActivity(new Intent(Intent.ACTION_DIAL, selectedUri));
                    }
                    FirebaseDatabase.getInstance().getReference("users/user/" + ReTax.USER_UNIQUE_ID + "/contact_method").child(UUID.randomUUID().toString()).setValue(
                            new ContactAction(
                                    (toContact != null)? toContact : null, selectedUri, Calendar.getInstance().getTime())
                    );
                }
            });
            builder.create().show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        if(id == android.R.id.home){
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private class ContactAction{
        String uri;
        String toContact;
        Date selectedDate;

        public ContactAction() {
        }

        public ContactAction(String toWhom,  Uri uri, Date date) {
            this.toContact = toWhom;
            this.uri = uri.toString();
            this.selectedDate = date;

        }

        public Date getSelectedDate() {
            return selectedDate;
        }

        public String getToContact() {
            return toContact;
        }

        public String getUri() {
            return uri;
        }
    }
}
