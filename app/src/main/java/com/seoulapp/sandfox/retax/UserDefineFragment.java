package com.seoulapp.sandfox.retax;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.seoulapp.sandfox.retax.model.User;

import java.util.Calendar;

/**
 * 사용자에 대한 간단한 정보를 수집 후, Firebase 실시간 데이터베이스에 저장
 * 맨 처음 실행했을 시에만 정보를 수집하고 이후에는 더이상 띄워주지 않는다.
 */

public class UserDefineFragment extends Fragment {
    private final String LOG_TAG = UserDefineFragment.class.getSimpleName();

    View fragment;
    RadioGroup tourist_rg, gender_rg;
    Spinner continent_sp, age_sp;
    RatingBar tour_rate;
    CheckBox not_tour_checkbox;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.user_define_fragment, null);

        final LinearLayout rating_part = (LinearLayout) fragment.findViewById(R.id.rating);

        tourist_rg = (RadioGroup) fragment.findViewById(R.id.rg_tour);
        tourist_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.rb_not_tourist){
                    rating_part.setVisibility(View.GONE);
                }else{
                    rating_part.setVisibility(View.VISIBLE);
                }
            }
        });
        continent_sp = (Spinner) fragment.findViewById(R.id.sp_continent);
        gender_rg = (RadioGroup) fragment.findViewById(R.id.rg_gender);
        age_sp = (Spinner) fragment.findViewById(R.id.sp_age);
        tour_rate = (RatingBar) fragment.findViewById(R.id.rate_tour);
        not_tour_checkbox = (CheckBox)fragment.findViewById(R.id.cb_not_tour);
        not_tour_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    tour_rate.setRating(0.0f);
                    tour_rate.setEnabled(false);
                }else{
                    tour_rate.setEnabled(true);
                }
            }
        });

        Button submit = (Button) fragment.findViewById(R.id.user_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User(
                        ifTourist(tourist_rg), fromWhere(continent_sp), whatGender(gender_rg), getAgeGroup(age_sp), getStarRate(tour_rate), Calendar.getInstance().getTime()
                );
                Log.i(LOG_TAG, user.toString());

                if(rating_part.getVisibility() == View.VISIBLE && tour_rate.getRating() == 0.0 && !not_tour_checkbox.isChecked()){
                    Toast.makeText(getActivity(), "Please rate", Toast.LENGTH_SHORT).show();
                }else{
                    writeToUsers(user);
                    fragment.setVisibility(View.GONE);
                }
            }
        });
        return fragment;
    }

    private void writeToUsers(User user){
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = mFirebaseDatabase.getReference("users");

        String userId = ReTax.sharedPreferences.getString("userId", null);
        Log.i(LOG_TAG, userId);
        ref.child("user").child(userId).child("profile").setValue(user);

    }


    private boolean ifTourist(RadioGroup radioGroup){
        if(radioGroup.getCheckedRadioButtonId() == R.id.rb_tourist){
            return true;
        }else{
            return false;
        }
    }

    private String fromWhere(Spinner spinner){
        return spinner.getSelectedItem().toString();
    }

    private String whatGender(RadioGroup radioGroup){
        switch (radioGroup.getCheckedRadioButtonId()){
            case R.id.rb_male:
                return "male";
            case R.id.rb_female:
                return "female";
            case R.id.rb_other:
                return "other";
            default:
                return null;
        }
    }

    private String getAgeGroup(Spinner spinner){
        return spinner.getSelectedItem().toString();
    }

    private float getStarRate(RatingBar ratingBar){
        return ratingBar.getRating();
    }


}
