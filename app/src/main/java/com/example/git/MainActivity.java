package com.example.git;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.onesignal.OneSignal;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "OneSignalPushTest";


    private static final String ONESIGNAL_APP_ID = "aa02316f-3732-458f-87d4-d3abf9b5e0ac";
    // TODO: 여기에 본인 OneSignal 대시보드에서 발급받은 REST API Key 넣어야 함!
    private static final String ONESIGNAL_REST_API_KEY = "os_v2_app_vibdc3zxgjcy7b6u2ov7tnpavr2na7kab5felnno6qtke5auzcyoqvypg3nrlnqqvhz53dio3phs6ehbwluvwklaorswchde3la74ly";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // OneSignal 초기화
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        // 푸시 전송 버튼
        Button btnSendPush = findViewById(R.id.btnSendPush);
        btnSendPush.setOnClickListener(v -> sendPushNotification());
    }

    private void sendPushNotification() {
        OkHttpClient client = new OkHttpClient();

        String jsonBody = "{"
                + "\"app_id\": \"" + ONESIGNAL_APP_ID + "\","
                + "\"included_segments\": [\"All\"],"
                + "\"headings\": {\"en\": \"Test Title from App\"},"
                + "\"contents\": {\"en\": \"변경됨\"}"
                + "}";

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url("https://onesignal.com/api/v1/notifications")
                .addHeader("Authorization", "Basic " + ONESIGNAL_REST_API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to send push", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Push sent successfully!");
                } else {
                    Log.e(TAG, "Push send error: " + response.body().string());
                }
            }
        });
    }
}
