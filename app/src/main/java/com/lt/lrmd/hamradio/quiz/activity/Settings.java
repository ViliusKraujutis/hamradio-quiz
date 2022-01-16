package com.lt.lrmd.hamradio.quiz.activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.lt.lrmd.hamradio.quiz.R;

public class Settings extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings); 
	}
 


}
