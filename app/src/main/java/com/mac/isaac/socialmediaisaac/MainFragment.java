package com.mac.isaac.socialmediaisaac;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.SocialNetworkManager.OnInitializationCompleteListener;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.github.gorbin.asne.facebook.FacebookSocialNetwork;
import com.github.gorbin.asne.googleplus.GooglePlusSocialNetwork;
import com.github.gorbin.asne.linkedin.LinkedInSocialNetwork;
import com.github.gorbin.asne.twitter.TwitterSocialNetwork;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainFragment extends Fragment implements OnInitializationCompleteListener, OnLoginCompleteListener {

    private Button btnFacebook;
    private Button btnTwitter;
    private Button btnLinkedin;
    private Button btnGoogleplus;
    public static SocialNetworkManager mSocialNetworkManager;
    String facebook_app_id, facebook_scope,
            twitter_consumer_key,
            twitter_consumer_secret,
            linkedin_client_id,
            linkedin_client_secret,
            linkedin_scope,
            googleplus_client_id,
            callback_url;
    private View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int networkId = 0;
            switch (view.getId()){
                case R.id.btn_facebook:
                    networkId = FacebookSocialNetwork.ID;
                    break;
                case R.id.btn_twitter:
                    networkId = TwitterSocialNetwork.ID;
                    break;
                case R.id.btn_linkedin:
                    networkId = LinkedInSocialNetwork.ID;
                    break;
                case R.id.btn_googleplus:
                    networkId = GooglePlusSocialNetwork.ID;
                    break;
            }
            SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);
            if(!socialNetwork.isConnected()) {
                if(networkId != 0) {
                    socialNetwork.requestLogin();
                    MainActivity.showProgress("Loading social person");
                } else {
                    Toast.makeText(getActivity(), "Wrong networkId", Toast.LENGTH_LONG).show();
                }
            } else {
                startProfile(socialNetwork.getID());
            }
        }
    };

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager()
                .findFragmentByTag(MainActivity.SOCIAL_NETWORK_TAG);
        btnFacebook = (Button) rootView.findViewById(R.id.btn_facebook);
        btnFacebook.setOnClickListener(loginClick);
        btnTwitter = (Button) rootView.findViewById(R.id.btn_twitter);
        btnTwitter.setOnClickListener(loginClick);
        btnLinkedin = (Button) rootView.findViewById(R.id.btn_linkedin);
        btnLinkedin.setOnClickListener(loginClick);
        btnGoogleplus = (Button) rootView.findViewById(R.id.btn_googleplus);
        btnGoogleplus.setOnClickListener(loginClick);
        initSocialNetworks();
        return rootView;
    }

    private void startProfile(int networkId){
        ProfileFragment profile = ProfileFragment.newInstannce(networkId);
        getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack("profile")
                .replace(R.id.container, profile)
                .commit();
    }

    @Override
    public void onSocialNetworkManagerInitialized() {
        //when init SocialNetworks - get and setup login only for initialized SocialNetworks
        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
            initSocialNetwork(socialNetwork);
        }
    }

    private void initSocialNetwork(SocialNetwork socialNetwork){
        if(socialNetwork.isConnected()){
            switch (socialNetwork.getID()){
                case FacebookSocialNetwork.ID:
                    btnFacebook.setText("Connected to Facebook");
                    break;
                case TwitterSocialNetwork.ID:
                    btnTwitter.setText("Connected to Twitter");
                    break;
                case LinkedInSocialNetwork.ID:
                    btnLinkedin.setText("Connected to LinkedIn");
                    break;
                case GooglePlusSocialNetwork.ID:
                    btnGoogleplus.setText("Connected to GooglePlus");
                    break;
            }
        }
    }

    /*@Override
    public void onRequestSocialPersonSuccess(int i, SocialPerson socialPerson) {
        MainActivity.hideProgress();
        name.setText(socialPerson.name);
        id.setText(socialPerson.id);
        String socialPersonString = socialPerson.toString();
        String infoString = socialPersonString.substring(socialPersonString.indexOf("{")+1, socialPersonString.lastIndexOf("}"));
        info.setText(infoString.replace(", ", "\n"));
        Picasso.with(getActivity())
                .load(socialPerson.avatarURL)
                .into(photo);
    }*/

    public void initSocialNetworks() {
        facebook_app_id = getActivity().getResources().getString(R.string.facebook_app_id);
        facebook_scope = getActivity().getResources().getString(R.string.facebook_scope);
        twitter_consumer_key = getActivity().getResources().getString(R.string.twitter_consumer_key);
        twitter_consumer_secret = getActivity().getResources().getString(R.string.twitter_consumer_secret);
        linkedin_client_id = getActivity().getResources().getString(R.string.linkedin_client_id);
        linkedin_client_secret = getActivity().getResources().getString(R.string.linkedin_client_secret);
        linkedin_scope = getActivity().getResources().getString(R.string.linkedin_scope);
        googleplus_client_id = getActivity().getResources().getString(R.string.googleplus_client_id);
        callback_url = getActivity().getResources().getString(R.string.callback_url);
        ArrayList<String> fbScope = new ArrayList<String>();
        fbScope.addAll(Arrays.asList(facebook_scope));

        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = new SocialNetworkManager();

            FacebookSocialNetwork fbNetwork = new FacebookSocialNetwork(this,
                    fbScope);
            TwitterSocialNetwork twNetwork = new TwitterSocialNetwork(this,
                    twitter_consumer_key,
                    twitter_consumer_secret,
                    callback_url);
            GooglePlusSocialNetwork gpNetwork = new GooglePlusSocialNetwork(this);
            LinkedInSocialNetwork inNetwork = new LinkedInSocialNetwork(this,
                    linkedin_client_id,
                    linkedin_client_secret,
                    callback_url,
                    linkedin_scope);

            mSocialNetworkManager.addSocialNetwork(fbNetwork);
            mSocialNetworkManager.addSocialNetwork(twNetwork);
            mSocialNetworkManager.addSocialNetwork(gpNetwork);
            mSocialNetworkManager.addSocialNetwork(inNetwork);

            //Initiate every network from mSocialNetworkManager
            getFragmentManager()
                    .beginTransaction()
                    .add(mSocialNetworkManager, MainActivity.SOCIAL_NETWORK_TAG)
                    .commit();
            mSocialNetworkManager.setOnInitializationCompleteListener(this);
        } else {
            //if manager exist - get and setup login only for initialized SocialNetworks
            if(!mSocialNetworkManager.getInitializedSocialNetworks().isEmpty()) {
                List<SocialNetwork> socialNetworks = mSocialNetworkManager.getInitializedSocialNetworks();
                for (SocialNetwork socialNetwork : socialNetworks) {
                    socialNetwork.setOnLoginCompleteListener(this);
                    initSocialNetwork(socialNetwork);
                }
            }
        }

    }

    @Override
    public void onLoginSuccess(int networkId) {
        MainActivity.hideProgress();
        startProfile(networkId);
    }

    @Override
    public void onError(int networkId, String requestID, String errorMessage, Object data) {
        MainActivity.hideProgress();
        Toast.makeText(getActivity(), "ERROR: " + errorMessage, Toast.LENGTH_LONG).show();
    }
}
