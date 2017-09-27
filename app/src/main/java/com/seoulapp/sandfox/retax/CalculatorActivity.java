package com.seoulapp.sandfox.retax;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static java.lang.Integer.parseInt;

/**
 * 얼마를 환급받을 수 있는지 계산할 수 있는 화면
 *
 */

public class CalculatorActivity extends AppCompatActivity {
    private final static String LOG_TAG = CalculatorActivity.class.getSimpleName();

    private static int totalRefundAmount = 0;

    /*입력 값을 담을 어레이 리스트*/
    private ArrayList<String> inputList;
    private EditText addPriceEditText;

    private CalculatorListAdapter adapter;

    private TextView totalRefund;
    private String typing ="";

    private DecimalFormat decimalFormat;
    private ListView priceList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputList = new ArrayList<>();
        adapter = new CalculatorListAdapter(getApplicationContext(), inputList);

        priceList = (ListView) findViewById(R.id.priceListView);
        priceList.setAdapter(adapter);
        priceList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        totalRefund = (TextView) findViewById(R.id.totalRefundText);
        totalRefund.setText(totalRefundAmount+" "+getString(R.string.krw_currency));

        decimalFormat = new DecimalFormat("###,###,###");

        /*입력란*/
        addPriceEditText = (EditText) findViewById(R.id.inputEditText);
            addPriceEditText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                    if(keyCode == KeyEvent.KEYCODE_ENTER){
                        String enteredText = addPriceEditText.getText().toString().replaceAll(",","");

                        if(!enteredText.equals("")
                                && !enteredText.isEmpty()){
                            inputList.add(enteredText);

                            int taxResult = (int)(parseInt(enteredText) * 0.05);
                            Log.i(LOG_TAG, taxResult+"");
                            totalRefundAmount += taxResult;
                            Log.i(LOG_TAG, "Total "+ totalRefundAmount);
                            totalRefund.setText(decimalFormat.format(totalRefundAmount)+" "+getString(R.string.krw_currency));
                        }
                        addPriceEditText.setText("");
                        return true;
                    }
                    return false;
                }
            });



        addPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Log.i(LOG_TAG, "onTextChanged? >" + charSequence);
                if(!charSequence.toString().equals(typing) && !charSequence.toString().equals("")){
                    Log.i(LOG_TAG, "onTextChanged? if 문");
                    typing = decimalFormat.format(Integer.parseInt(charSequence.toString().replaceAll(",","")));
                    Log.i(LOG_TAG, typing);
                    addPriceEditText.setText(typing);
                    addPriceEditText.setSelection(typing.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private class CalculatorListAdapter extends ArrayAdapter<String>{
        public CalculatorListAdapter(Context context, ArrayList<String> resource) {
            super(context, 0, resource);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            View listItemView = convertView;
            if(listItemView == null){
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.calculate_list_item, parent, false);
            }

            TextView priceTextView = (TextView) listItemView.findViewById(R.id.input_price);
            priceTextView.setText(decimalFormat.format(Integer.parseInt(inputList.get(position))));

            TextView resultTaxTextView = (TextView) listItemView.findViewById(R.id.result_tax);
            int result = (int)(Integer.parseInt(inputList.get(position)) * 0.05);
            resultTaxTextView.setText(decimalFormat.format(result) + "");

            TextView deleteButton = (TextView) listItemView.findViewById(R.id.icon_delete);
            deleteButton.setTag(position);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int)v.getTag();
                    int result = (int)(parseInt(inputList.remove(index)) * 0.05);
                    Log.i(LOG_TAG, "result : "+result);
                    totalRefundAmount -= result;
                    totalRefund.setText(decimalFormat.format(totalRefundAmount)+" "+getString(R.string.krw_currency));
                    adapter.notifyDataSetInvalidated();
                }
            });
            return listItemView;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calculator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_clear:
                inputList.clear();
                adapter.notifyDataSetChanged();
                totalRefundAmount = 0;
                totalRefund.setText(R.string.defaultTotal);
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
         /*FIREBASE 기록*/
        if (inputList.size() > 0){
            FirebaseDatabase.getInstance().getReference("users/user/" + ReTax.USER_UNIQUE_ID + "/calculator").child(UUID.randomUUID().toString()).setValue(new CalculateHistory(inputList, Calendar.getInstance().getTime()));
            inputList.clear();
            totalRefundAmount = 0;
            totalRefund.setText(R.string.defaultTotal);
        }
    }

    private class CalculateHistory{
        ArrayList<String> history;
        Date calculatedTime;

        public CalculateHistory() {
        }
        public CalculateHistory(ArrayList<String> arr, Date time) {
            this.history = arr;
            this.calculatedTime = time;
        }

        public ArrayList<String> getHistory() {
            return history;
        }

        public Date getCalculatedTime() {
            return calculatedTime;
        }
    }
}
