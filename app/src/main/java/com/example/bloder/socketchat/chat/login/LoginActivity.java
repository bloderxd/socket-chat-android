package com.example.bloder.socketchat.chat.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;

import com.example.bloder.socketchat.BuildConfig;
import com.example.bloder.socketchat.chat.ChatActivity_;
import com.example.bloder.socketchat.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

    @ViewById protected EditText userName;

    @AfterViews
    protected void afterViews(){
        socket.on("login", onLogin);
        socket.connect();
    }

    @Click(R.id.login)
    protected void loginClick(){
        loginAttempt();
    }

    private Socket socket;{
        try{
            socket = IO.socket(BuildConfig.CHAT_SERVER_ADRESS);
        }catch (URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        socket.off("login", onLogin);
    }

    private void loginAttempt() {
        userName.setError(null);

        String username = userName.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            userName.setError(getString(R.string.error_field_required));
            userName.requestFocus();
            return;
        }

        socket.emit("add user", username);
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            int numberUsers;

            try{
                numberUsers = data.getInt("numUsers");
            } catch (JSONException e) {
                return;
            }

            ChatActivity_.intent(getApplicationContext())
                    .username(userName.getText().toString())
                    .numUsers(numberUsers)
                    .flags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .start();
        }
    };

}
