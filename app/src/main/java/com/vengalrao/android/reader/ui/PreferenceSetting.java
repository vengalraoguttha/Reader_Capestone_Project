package com.vengalrao.android.reader.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.vengalrao.android.reader.R;

/**
 * Created by vengalrao on 14-04-2017.
 */

public class PreferenceSetting extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_book_setting);

        SharedPreferences sharedPreferences=getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen=getPreferenceScreen();
        int count=preferenceScreen.getPreferenceCount();
        int i;
        for(i=0;i<count-2;i++){
            Preference preference=preferenceScreen.getPreference(i);
            String value=sharedPreferences.getString(preference.getKey(),"");
            setPreferenceSummary(preference,value);
        }
    }

    public void setPreferenceSummary(Preference preference ,String value){
        if(preference instanceof ListPreference){
            ListPreference listPreference=(ListPreference)preference;
            int preInd=listPreference.findIndexOfValue(value);
            if(preInd>=0){
                listPreference.setSummary(listPreference.getEntries()[preInd]);
            }
        }else if(preference instanceof EditTextPreference){
            EditTextPreference editTextPreference=(EditTextPreference)preference;
            editTextPreference.setSummary(value);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference=findPreference(key);
        if(preference!=null){
            if(preference instanceof CheckBoxPreference){

            }else{
                String value=sharedPreferences.getString(preference.getKey(),"");
                setPreferenceSummary(preference,value);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
