package com.updatetest.firestoretest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.updatetest.firestoretest.Models.WeatherModel;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.Result;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatBot extends AppCompatActivity {
    // 날짜
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // 날씨 날짜
    SimpleDateFormat weatherDateFormatFromString = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    SimpleDateFormat weatherDateFormatToString = new SimpleDateFormat("MM월 dd일 hh시");


    RecyclerView recyclerView;
    EditText editText_inputdata;
    Button button_sendMessage;
    List<MessageModel> messageModelList = new ArrayList<>();

    AIConfiguration configuration
            = new AIConfiguration("57153e17eac14cfa9e5d3d81f6485f7e",
            AIConfiguration.SupportedLanguages.Korean);
    AIDataService aiDataService = new AIDataService(configuration);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        // 상태 창 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 리사이클러 뷰 호출, 레이아웃 매니저 설정, 어댑터 설정
        recyclerView = findViewById(R.id.chatbot_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ChatBotRecyclerViewAdapter());

        // 위젯 호출
        editText_inputdata = findViewById(R.id.chatbot_edittext_message);
        button_sendMessage = findViewById(R.id.chatbot_button_sendmessage);

        // 전송 버튼 리스너
        button_sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(editText_inputdata.getText().toString())) {
                    MessageModel messageModel = new MessageModel();
                    messageModel.myMessage = true;
                    messageModel.message = editText_inputdata.getText().toString();

                    // 메시지 입력 완료, 새로고침, 스크롤 하단 이동
                    messageModelList.add(messageModel);
                    recyclerView.getAdapter().notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageModelList.size() - 1);

                    // DialogFlow와 통신
                    new TalkAsyncTask().execute(editText_inputdata.getText().toString());

                    // 전송 완료 후 메시지 리셋
                    editText_inputdata.setText("");
                }
            }
        });
    }

    // 비동기 클래스. Dialogflow에서 값을 송,수신
    class TalkAsyncTask extends AsyncTask<String, Void, Result> {
        @Override
        protected Result doInBackground(String... strings) {
            AIRequest aiRequest = new AIRequest();
            aiRequest.setQuery(strings[0]);
            try {
                return aiDataService.request(aiRequest).getResult();
            } catch (AIServiceException e) {
                e.printStackTrace();
                // return aiDataService.request(aiRequest).getResult();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);
            if (result != null) {
                System.out.println("확인: " + result.getFulfillment().getSpeech());
                makeMessage(result);
            }
        }

        void makeMessage(Result result) {
            // 시간표 호출
            if (result.getMetadata().getIntentName().equals("Schedules")) {
                System.out.println("확인: result 값이 Schedules와 동일");
                // 맞는지 확인 - null 에러 뜨겠는데?
                try {
                    String date = result.getParameters().get("date").toString();
                    String testDate = date.substring(1, date.length() - 1);

                    Date dateFromString = simpleDateFormat.parse(testDate);
                    System.out.println("확인. dateFromString : " + dateFromString);

                    Calendar calendar = Calendar.getInstance();
                    // 맞는지 확인
                    calendar.setTime(dateFromString);

                    // 요일을 숫자로 받아옵니다. 1 == 일요일, 7 == 토요일
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    System.out.println("확인. 해당하는 요일 인덱스: " + dayOfWeek);
                    scheduleMessage(dayOfWeek);
                }

                // 특정 요일을 지정하지 않았을 경우 오늘 데이터를 불러오도록 합니다.
                catch (NullPointerException e) {
                    int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                    scheduleMessage(dayOfWeek);
                } catch (ParseException e) {
                    System.out.println("확인. parse 에러: " + e.getMessage());
                    e.printStackTrace();
                }
            }


            else if (result.getMetadata().getIntentName().equals("Weather")) {
                try {
                    // 확인 1
                    String city = result.getParameters().get("geo-city").toString();
                    weatherMessage(city);
                } catch (NullPointerException e) {
                    String seoul = "서울특별시";
                    weatherMessage(seoul);
                }
            }


            else {
                String message = result.getFulfillment().getSpeech();
                System.out.println("확인 최종. message값: " + message);
                MessageModel messageModel = new MessageModel();
                messageModel.myMessage = false;
                messageModel.message = message;

                // 실제 입력
                messageModelList.add(messageModel);

                // 출력 및 스크롤 이동
                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.scrollToPosition(messageModelList.size() - 1);
            }
        }

        void weatherMessage(final String city) {
            String weatherUrl = "https://api.openweathermap.org/data/2.5/forecast?id=524901&APPID=432d58a485999a6cae261461bbbf772a&q=" + city + "&units=metric";

            /*
            // Test 1
            Gson gson = new Gson();
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8")
                    , gson.toJson(weatherUrl));
            */
            // post(requestBody)

            // 왜 못 불러올까?
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(weatherUrl).build();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();

                    WeatherModel weatherModel = new Gson().fromJson(result, WeatherModel.class);
                    for (int i = 0; i < weatherModel.list.size(); i++) {
                        try {
                            Long weatherItemUnixTime = weatherDateFormatFromString.parse(weatherModel.list.get(i).dt_txt).getTime();
                            if (weatherItemUnixTime > System.currentTimeMillis()) {
                                // 온도
                                Float temp = weatherModel.list.get(i).main.temp;

                                // 날씨 상태
                                String description = weatherModel.list.get(i).weather.get(0).description;

                                // 시간
                                String time = weatherDateFormatToString.format(weatherItemUnixTime);

                                // 습도
                                Float humidity = weatherModel.list.get(i).main.humidity;

                                final String message = time + " " + city + "의 기온은 " + temp + "도이며, 하늘은 "
                                        + description + "습도는 " + humidity + "% 입니다.";

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MessageModel messageModel = new MessageModel();
                                        messageModel.myMessage = false;
                                        messageModel.message = message;
                                        messageModelList.add(messageModel);
                                        recyclerView.getAdapter().notifyDataSetChanged();
                                        recyclerView.scrollToPosition(messageModelList.size() - 1);
                                    }
                                });
                                break;
                            }





                        } catch (ParseException e) {
                            System.out.println("확인. 날씨 일자 parsing 불가: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        void scheduleMessage(int dayOfWeek) {
            String dayOfWeekString = null;
            switch (dayOfWeek) {
                case 1:
                    dayOfWeekString = "일요일";
                    break;

                case 2:
                    dayOfWeekString = "월요일";
                    break;

                case 3:
                    dayOfWeekString = "화요일";
                    break;

                case 4:
                    dayOfWeekString = "수요일";
                    break;

                case 5:
                    dayOfWeekString = "목요일";
                    break;

                case 6:
                    dayOfWeekString = "금요일";
                    break;

                case 7:
                    dayOfWeekString = "토요일";
                    break;
            }

            // 맞는지 확인
            final String finalDayOfWeekString = dayOfWeekString;

            FirebaseFirestore.getInstance().collection("schedule").whereEqualTo("dayofweek", dayOfWeekString)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        String message = null;
                        if (finalDayOfWeekString.equals("금요일") || finalDayOfWeekString.equals("토요일") || finalDayOfWeekString.equals("일요일")) {
                            message = finalDayOfWeekString + "수업은 없습니다. 이번주도 고생하셨습니다!";
                        } else {
                            message = finalDayOfWeekString + " 수업은 "
                                    + snapshot.get("lectures")
                                    + "입니다";
                        }

                        // 메시지 모델 생성
                        MessageModel messageModel = new MessageModel();
                        messageModel.myMessage = false;
                        messageModel.message = message;

                        // 실제 입력
                        messageModelList.add(messageModel);

                        // 새로고침 및 스크롤 이동
                        recyclerView.getAdapter().notifyDataSetChanged();
                        recyclerView.scrollToPosition(messageModelList.size() - 1);
                        break;
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChatBot.this, "에러 발생: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            /*
            FirebaseFirestore.getInstance().collection("schedule").whereEqualTo("dayofweek", dayOfWeekString)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        // 맞는지 확인
                        for (int i = 0; i < task.getResult().getDocuments().size(); i++) {
                            String message = finalDayOfWeekString + "의 수업은 "
                                    + task.getResult().getDocuments().get(i).getData().get("lecture")
                                    + "입니다";

                            // 메시지 모델 생성
                            MessageModel messageModel = new MessageModel();
                            messageModel.myMessage = false;
                            messageModel.message = message;

                            // 실제 입력
                            messageModelList.add(messageModel);

                            // 새로고침 및 스크롤 이동
                            recyclerView.getAdapter().notifyDataSetChanged();
                            recyclerView.scrollToPosition(messageModelList.size() - 1);
                            break;
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChatBot.this, "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
            */
        }
    }

    class ChatBotRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatbot_recyclerview_item, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (messageModelList.get(position).myMessage) {
                ((CustomViewHolder) holder).textView_userMessage.setVisibility(View.VISIBLE);
                ((CustomViewHolder) holder).textView_userMessage.setText(messageModelList.get(position).message);
                ((CustomViewHolder) holder).textView_chatBotMessage.setVisibility(View.GONE);
                ((CustomViewHolder) holder).imageView.setVisibility(View.GONE);
            }

            if (!messageModelList.get(position).myMessage) {
                ((CustomViewHolder) holder).textView_chatBotMessage.setVisibility(View.VISIBLE);
                ((CustomViewHolder) holder).textView_chatBotMessage.setText(messageModelList.get(position).message);

                ((CustomViewHolder) holder).textView_userMessage.setVisibility(View.GONE);

                ((CustomViewHolder) holder).imageView.setVisibility(View.VISIBLE);
                Glide.with(ChatBot.this).load(android.R.mipmap.sym_def_app_icon).apply(new RequestOptions().centerCrop()).into(((CustomViewHolder) holder).imageView);
            }
        }

        @Override
        public int getItemCount() {
            return messageModelList.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView textView_chatBotMessage;
            TextView textView_userMessage;

            public CustomViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.chatbot_item_imageview);
                textView_chatBotMessage = view.findViewById(R.id.chatbot_item_bottext);
                textView_userMessage = view.findViewById(R.id.chatbot_item_usertext);
            }
        }
    }
}
