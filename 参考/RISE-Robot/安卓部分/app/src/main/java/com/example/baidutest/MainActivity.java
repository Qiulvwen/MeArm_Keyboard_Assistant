package com.example.baidutest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.example.baidutest.game.Constants;
import com.example.baidutest.game.Coordinate;
import com.example.baidutest.game.Game;
import com.example.baidutest.game.GameView;
import com.example.baidutest.net.ConnectionService;
import com.example.baidutest.net.MessageContent;
import com.example.baidutest.recognition.InitConfig;
import com.example.baidutest.recognition.SynthesizerListener;
import com.example.baidutest.recognition.SynthesizerService;
import com.example.baidutest.ui.ColorFilterGenerator;
import com.example.baidutest.ui.AnimationView;
import com.example.baidutest.ui.Theme;
import com.baidu.speech.EventListener;
import com.baidu.speech.asr.SpeechConstant;
import com.example.baidutest.util.TextAnalyzerUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    //????????????
    SynthesizerService synthesizerService;

    //chat Views
    private EditText inputText;
    private RecyclerView msgRecyclerView;
    private MessageAdapter messageAdapter;
    private Button send;
    private List<MessageContent> msgList = new ArrayList<>();

    //????????????
    public static int state = Constants.SYSTEM_STATE_INIT;  //????????????0????????????1?????????
    public static int mode = Constants.MODE_END;  //????????????0???1???2???3???4

    //game Views
    private Game game = new Game();
    protected LinearLayout gameLayout;
    // ???????????????
    private ImageView mBlackActive; //??????
    private ImageView mWhiteActive;  //??????
    //????????????
    private MediaPlayer mediaPlayer;
    //????????????View
    protected GameView gameView;

    //??????
    private ConnectionService connectionService = null;
    private Handler messageHandler;

    //web
    protected WebView webView;
    private LinearLayout webLayout;
    //????????????webView
    long lastTime = 0;
    long currentTime = 0;

    //??????????????????
    protected AnimationView animationView;

    private EventManager asr;
    private EventListener asrListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("GameView", "onCreate()");
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //????????????
        initViews();  //?????????Views
        initContext();
        asrStart(); //?????????????????????????????????
        //initMsgList(); //??????????????????
    }

    private void initViews() {
        initGameView();
        initAnimationView();
        initWebView();
        initChatViews();
    }

    protected void initGameView() {
        gameView = findViewById(R.id.game_view);
        Log.e("GameView", "initGameView");
        gameLayout = findViewById(R.id.game_layout);
        mBlackActive = findViewById(R.id.black_active);
        mWhiteActive = findViewById(R.id.white_active);
        mediaPlayer = MediaPlayer.create(this, R.raw.voice);
        gameView.setGame(game);
        //gameView.drawGameView();
        Log.e("GameView", "drawGameView");
        //gameLayout.setVisibility(View.GONE);
    }

    protected void initAnimationView() {
        animationView = findViewById(R.id.animation_view);
        ColorMatrix cm = new ColorMatrix();
        /**
         * @param cm         ????????????????????????
         * @param brightness ????????????????????? [-100, 100]
         * @param contrast   ???????????????????????? [-100, 100]
         * @param saturation ???????????????????????? [-100, 100]
         * @param hue        ????????????????????? [-180, 180]
         */
        ColorFilterGenerator.adjustColor(cm, 0, 0, 0, 0);
        ColorFilter filter = new ColorMatrixColorFilter(cm);
        animationView.setThemeStyle(Theme.THEME_BLUE_LIGHTBG);
        animationView.setHsvFilter(filter);
        animationView.setVisibility(View.VISIBLE);
        //animationView.startRecordingAnimation();
        animationView.startPreparingAnimation();
    }

    protected void initWebView() {
        webLayout = findViewById(R.id.web_layout);
        webView = findViewById(R.id.web_view);
        WebSettings webSetting = webView.getSettings();
        webSetting.setDisplayZoomControls(true);
        webView.loadUrl(Constants.WEBVIEW_URL);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        // webLayout.setVisibility(View.VISIBLE);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);   //????????????webview??????????????????url
                return true;
            }
        });
        webLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastTime = currentTime;
                currentTime = System.currentTimeMillis();
                if (currentTime - lastTime < 1000) {
                    webView.loadUrl(Constants.WEBVIEW_URL);
                }
                //Toast.makeText(MainActivity.this, "webLayout", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initChatViews() {
        inputText = findViewById(R.id.input_text);
        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        send = findViewById(R.id.send_button);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);
        messageAdapter = new MessageAdapter(msgList);
        msgRecyclerView.setAdapter(messageAdapter);
        msgRecyclerView.scrollToPosition(msgList.size() - 1);
        setSendButton();
        /*inputText.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                inputText.setInputType(InputType.TYPE_NULL); //?????????????????????????
                return false;
            }
        });*/
    }

    private void initContext() {
        //???????????????
        initPermission();
        //??????????????????????????????
        setRecognizer();
        //??????Socket??????????????????
        setMessageHandler();
        //?????????????????????????????????
        connectionService = ConnectionService.getInstance(messageHandler, MainActivity.this);
        //???????????????????????????
        initSynthesizer();
    }

    private void initSynthesizer() {
        Map<String, String> params = getParams();

        SpeechSynthesizerListener listener = new SynthesizerListener();

        TtsMode ttsMode = TtsMode.ONLINE;  //????????????

        InitConfig initConfig = new InitConfig(Constants.APP_ID, Constants.APP_KEY, Constants.SECRET_KEY, ttsMode, params, listener);

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };  //???????????????UI?????????????????????...
        synthesizerService = new SynthesizerService(this, initConfig, handler);
    }


    /**
     * @author:long
     * @date:2019/8/19
     * @description: ????????????????????????UI??????
     */
    private void setRecognizer() {
        asrListener = new EventListener() {
            String words = "";

            @Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                if (SpeechConstant.CALLBACK_EVENT_ASR_BEGIN.equals(name)) {
                    animationView.startRecordingAnimation();
                } else if (SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL.equals(name)) {
                    if (params != null && !params.isEmpty()) {
                        try {
                            JSONObject json = new JSONObject(params);
                            words = json.get("best_result").toString();
                            //inputText.setText(words);  //??????????????????????????????
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (SpeechConstant.CALLBACK_EVENT_ASR_FINISH.equals(name)) {
                    animationView.startPreparingAnimation();
                    MessageContent message = TextAnalyzerUtil.getMessage(words);
                    //checkNetwork();
                    handleMsg(message); //???????????????????????????????????????
                    //inputText.setText("");
                } else if (SpeechConstant.CALLBACK_EVENT_ASR_EXIT.equals(name)) {
                    animationView.resetAnimation();
                    animationView.startPreparingAnimation();
                }
            }
        };
    }

    /**
     * @author:long
     * @date:2019/8/16
     * @description: ???????????????????????????UI??????
     */
    private void setMessageHandler() {
        messageHandler = new Handler() {
            /**
             * @description: ??????ConnectionService??????????????????UI??????
             */
            @Override
            public void handleMessage(@NonNull Message msg) {
                MessageContent message = (MessageContent) msg.getData().getSerializable("message");
                handleMsg(message);
            }
        };
    }

    private void exitGameMode() {
        mode = Constants.MODE_END;
        game.clearGameMap();
        visibleWebLayout();
    }

    private void setSendButton() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String words = inputText.getText().toString();
                MessageContent message = TextAnalyzerUtil.getMessageFromInput(words);
                handleMsg(message);
                inputText.setText("");
                inputText.clearFocus();
            }
        });
    }

    /*boolean flag = true;

    private void setSendButton() {
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (flag) {
                    mode = Constants.MODE_CHESS_INT;
                    Coordinate coordinate = new Coordinate(4,4,1);
                    game.addChess(coordinate);
                    visibleGameLayout();

                    flag = false;
                } else {
                    mode = Constants.MODE_POUR_WATER_INT;
                    visibleWebLayout();
                    flag = true;
                }
            }
        });
    }*/


    private void initMsgList() {
        if (!msgList.isEmpty()) {
            return;
        }
        if (mode == Constants.SYSTEM_STATE_INIT) {
            updateChatList(Constants.TYPE_PC_RESPONSE, Constants.SYSTEM_INIT_TEXT);
        }
    }

    private void resetActive() {
        mBlackActive.setVisibility(View.INVISIBLE);
        mWhiteActive.setVisibility(View.INVISIBLE);
    }

    private void updateActive(Game game) {
        if (game.getActive() == Game.BLACK) {
            mBlackActive.setVisibility(View.VISIBLE);
            mWhiteActive.setVisibility(View.INVISIBLE);
        } else {
            mBlackActive.setVisibility(View.INVISIBLE);
            mWhiteActive.setVisibility(View.VISIBLE);
        }
    }

    private void visibleWebLayout() {
        int webVisible = webLayout.getVisibility();
        int gameVisible = gameLayout.getVisibility();
        //game.clearGameMap();
        if (gameVisible == View.VISIBLE) {
            gameView.clearChessBoard();
            game.clearGameMap();
            gameLayout.setVisibility(View.GONE);
        }
        if (webVisible != View.VISIBLE) {
            webLayout.setVisibility(View.VISIBLE);
            webView.loadUrl(Constants.WEBVIEW_URL);
        }
    }

    private void visibleGameLayout() {
        int webVisible = webLayout.getVisibility();
        int gameVisible = gameLayout.getVisibility();
        if (webVisible == View.VISIBLE) {
            webLayout.setVisibility(View.GONE);
        }
        if (gameVisible != View.VISIBLE) {
            gameLayout.setVisibility(View.VISIBLE);
            Log.e("GameView", "setVisibility");
            gameView.drawGameView();
            Log.e("GameView", "drawGameView3");
        }
    }

    /**
     * @author:long
     * @date:2019/8/17
     * @description: ????????????????????????ROBOT??????
     */
    private void updateChatList(MessageContent message) {
        if (message == null || "".equals(message.getText())) {
            return;
        }
        if (message.isRobot()) {
            synthesizerService.stopInThread();
            synthesizerService.speakInThread(message.getText());
        }
        msgList.add(message);
        messageAdapter.notifyItemInserted(msgList.size() - 1);
        msgRecyclerView.scrollToPosition(msgList.size() - 1);
        inputText.setText("");
    }

    private void updateChatList(int type, String text) {
        MessageContent message = new MessageContent(text);
        message.setType(type);
        updateChatList(message);
    }

    /**
     * @author:long
     * @date:2019/8/17
     * @description: ????????????
     */
    private void updateGameView(String data) {
        Coordinate coordinate = new Coordinate(data);
        if (gameView.addChess(coordinate)) {
            updateActive(game);
            mediaPlayer.start();
        }
    }

    /**
     * @author:long
     * @date:2019/8/17
     * @description: ??????????????????
     */
    private void enterGameMode() {
        resetActive();
        gameView.setGame(game);
        gameView.drawGameView();
        visibleGameLayout();
    }

    private void sendCommand(MessageContent message) {
        if (connectionService.isConnected()) {
            if (message == null || "".equals(message.getData())) {
                Log.e("connection", "message == null");
                return;
            }
            Log.e("connection", "+++sendCommand  before");
            connectionService.sendMessage(message);
            Log.e("connection", "+++sendCommand:" + message.toString());
        }
    }

    private void handleMsg(MessageContent message) {
        Log.e("connection", "+++handleMsg:");
        if (!connectionService.isConnected()) {
            updateChatList(message);
            throwsException(Constants.NETWORK_EXCEPTION);
            return;
        }
        if (message == null || message.isEmpty()) {
            Log.e("connection", "+++message.isEmpty():");
            return;
        }
        Log.e("connection", "+++handleMsg:" + message.toString());
        int type = message.getType();   //??????????????????
        switch (type) {
            case Constants.TYPE_CONTROL: //1
                handleControl(message);
                break;
            case Constants.TYPE_INTERACTION: //2
                handleInteraction(message);
                break;
            case Constants.TYPE_CHESS_COORDINATE: //3
                handleChessCoordinate(message);
                break;
            case Constants.TYPE_MODE: //4
                handleMode(message);
                break;
            case Constants.TYPE_WORDS_TEXT: //5
                handleWords(message);
                break;
            case Constants.TYPE_PC_RESPONSE: //6
                handlePC(message);
                break;
            case Constants.TYPE_HEART_BEAT:  //7
                break;
            case Constants.TYPE_CONFIRM:  //8
                break;
            case Constants.TYPE_STATE:
                updateSystemState(message);
                break;
        }
    }

    private void updateSystemState(MessageContent message) {
        String data = message.getData();
        String[] strings = data.split(",");
        state = Integer.valueOf(strings[0]);
        mode = Integer.valueOf(strings[1]);
        saveState();  //??????????????????
        //game.clearGameMap();
        //initMsgList();
        updateChatList();
        gameView.drawGameView();
    }


    /**
     * @author:long
     * @date:2019/8/21
     * @description: ?????????????????????????????????
     */
    private void updateChatList() {
        if (state == Constants.SYSTEM_STATE_INIT) {
            updateChatList(Constants.TYPE_PC_RESPONSE, Constants.SYSTEM_INIT_TEXT);
        } else if (state == Constants.SYSTEM_STATE_PREPARE) {
            if (mode == Constants.MODE_END) {
                updateChatList(Constants.TYPE_PC_RESPONSE, Constants.SYSTEM_PREPARE_TEXT);
            } else if (mode == Constants.MODE_CONTROL_INT) {
                updateChatList(Constants.TYPE_PC_RESPONSE, Constants.MODE_CONTROL_WORKING);
            } else if (mode == Constants.MODE_CHESS_INT) {
                enterGameMode();
                updateChatList(Constants.TYPE_PC_RESPONSE, Constants.MODE_CHESS_WORKING);
            } else if (mode == Constants.MODE_POUR_WATER_INT) {
                updateChatList(Constants.TYPE_PC_RESPONSE, Constants.MODE_WATER_WORKING);
            } else if (mode == Constants.MODE_CRAWL_INT) {
                updateChatList(Constants.TYPE_PC_RESPONSE, Constants.MODE_CRAWL_WORKING);
            }
        }
    }


    /**
     * @author:long
     * @date:2019/8/16
     * @description: ????????????
     */
    private void turnOn(MessageContent message) {
        Log.e("connection", "------------turnOn---------begin");
        updateChatList(message);
        if (state == Constants.SYSTEM_STATE_PREPARE && mode == Constants.MODE_END) {
            updateChatList(Constants.TYPE_PC_RESPONSE, Constants.MODE_WORKING_TEXT);
            return;
        }
        sendCommand(message);
        MessageContent response = new MessageContent(Constants.TYPE_PC_RESPONSE, "", Constants.SYSTEM_PREPARE_TEXT);
        updateChatList(response);
        state = Constants.SYSTEM_STATE_PREPARE;
        Log.e("connection", "------------turnOn--------------end");
    }

    /**
     * @author:long
     * @date:2019/8/16
     * @description: ????????????
     */
    private void turnOff(MessageContent message) {
        Log.e("connection", "------------turnOff");
        updateChatList(message);
        if (state == Constants.SYSTEM_STATE_INIT) {
            updateChatList(Constants.TYPE_PC_RESPONSE, Constants.MODE_END_TEXT);
            return;
        }
        sendCommand(message);
        MessageContent response = new MessageContent(Constants.TYPE_PC_RESPONSE, "", Constants.SYSTEM_INIT_TEXT);
        updateChatList(response);
        visibleWebLayout();
        state = Constants.SYSTEM_STATE_INIT;
        mode = Constants.MODE_END;
    }

    private void handleControl(MessageContent message) {
        String data = message.getData();
        if (Constants.ON_ROBOT.equals(data)) {
            turnOn(message);
        } else if (Constants.OFF_ROBOT.equals(data)) {
            turnOff(message);
        } else if (Constants.CHESS_FRONT_TEXT.equals(data)) {
            if (mode != Constants.MODE_CHESS_INT) {
                return;
            }
            updateChatList(message);
            sendCommand(message);
            game.setActive(Constants.CHESS_FRONT);
            updateActive(game);
        } else if (Constants.CHESS_BACK_TEXT.equals(data)) {
            if (mode != Constants.MODE_CHESS_INT) {
                return;
            }
            updateChatList(message);
            sendCommand(message);
            game.setActive(Constants.CHESS_BACK);
            updateActive(game);
        }
    }

    private void throwsException(String exception) {
        updateChatList(Constants.TYPE_PC_RESPONSE, exception);
    }

    private void handleInteraction(MessageContent message) {
        if (state != Constants.SYSTEM_STATE_INIT) {
            updateChatList(message);
            updateChatList(Constants.TYPE_PC_RESPONSE, Constants.BUSYING);
            return;
        }
        updateChatList(message);
        sendCommand(message);
        if (message.getData().equals(Constants.HELLO)) {
            updateChatList(Constants.TYPE_PC_RESPONSE, Constants.HELLO_WORDS);
        }
    }

    private void handleChessCoordinate(MessageContent message) {
        String data = message.getData();
        updateGameView(data);
        if (data.contains("8,8,")) {
            message.setText("?????????????????????????????????????????????????????????????????????????????????");
            updateChatList(message);
        } else if (data.contains("0,8,")) {
            message.setText("?????????????????????????????????????????????????????????????????????????????????");
            updateChatList(message);
        }
    }

    private void handleMode(MessageContent message) {
        String taskMode = message.getData();
        switch (taskMode) {
            case Constants.MODE_CONTROL:
                mode = Constants.MODE_CONTROL_INT;
                message.setText(Constants.MODE_CONTROL_BEGIN);
                break;
            case Constants.MODE_CHESS:
                mode = Constants.MODE_CHESS_INT;
                enterGameMode();
                message.setText(Constants.MODE_CHESS_TEXT);
                break;
            case Constants.MODE_POUR_WATER:
                mode = Constants.MODE_POUR_WATER_INT;
                message.setText(Constants.MODE_WATER_BEGIN);
                break;
            case Constants.MODE_CRAWL:
                mode = Constants.MODE_CRAWL_INT;
                message.setText(Constants.MODE_CRAWL_BEGIN);
                break;
            case Constants.END_TASK:
                if (mode == Constants.MODE_CONTROL_INT) {
                    message.setText(Constants.MODE_CONTROL_END);
                } else if (mode == Constants.MODE_POUR_WATER_INT) {
                    message.setText(Constants.MODE_WATER_END);
                } else if (mode == Constants.MODE_CRAWL_INT) {
                    message.setText(Constants.MODE_CRAWL_END);
                }
                visibleWebLayout();
                mode = Constants.MODE_END;
                break;
        }
        updateChatList(message);
    }

    private void handlePC(MessageContent message) {
        String data = message.getData();
        if (Constants.GAME_LOSE.equals(data) && mode == Constants.MODE_CHESS_INT) {
            message.setText(Constants.MODE_CHESS_LOSE);
            updateChatList(message);
            exitGameMode();
        } else if (Constants.GAME_WINNER.equals(data) && mode == Constants.MODE_CHESS_INT) {
            message.setText(Constants.MODE_CHESS_WIN);
            updateChatList(message);
            exitGameMode();
        } else if (Constants.GAME_DEUCE.equals(data) && mode == Constants.MODE_CHESS_INT) {
            message.setText(Constants.MODE_CHESS_DEUCE);
            updateChatList(message);
            exitGameMode();
        } else {
            message.setText(message.getData());
            updateChatList(message);
        }
    }

    private void handleWords(MessageContent message) {
        updateChatList(message);
        sendCommand(message);
        if (Constants.ROBOT_NAME.equals(message.getText())) {
            updateChatList(Constants.TYPE_PC_RESPONSE, Constants.CALL_TEXT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asrStop();
        Log.e("GameView", "onDestroy");
        saveState();
        synthesizerService.releaseInThread();
    }

    private void saveState() {
        //?????????SharedPreferences?????????????????????
        SharedPreferences mySharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
        //?????????SharedPreferences.Editor?????????????????????
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        //???putString?????????????????????
        editor.putInt("mode", mode);
        editor.putInt("state", state);
        //??????????????????
        editor.commit();
        //??????toast???????????????????????????????????????
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("GameView", "onStop");
        // connectionService.closeConnection();
        inputText.clearFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        synthesizerService.releaseInThread();
        asrStop();
        saveState();
        Log.e("GameView", "onPause");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("GameView", "onStart");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("GameView", "onResume");
        //??????????????????SharedPreferences??????????????????????????????SharedPreferences??????
        SharedPreferences sharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
        // ??????getString????????????value????????????2????????????value????????????
        state = sharedPreferences.getInt("state", 0);
        mode = sharedPreferences.getInt("mode", 0);
        if (synthesizerService.isRelease()) {
            initSynthesizer();
        }
        asrStart();

        //gameView.setVisibility(View.GONE);

    }

    @Override
    public void finish() {
        /**
         * ???????????????????????? super.finish(); ??????????????????????????????????????????
         * ?????????activity???????????????????????????onDestroy()
         */
        moveTaskToBack(true);
    }

    /**
     * android 6.0 ??????????????????????????????
     */
    private void initPermission() {
        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // ?????????????????????????????????.
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // ?????????android 6.0???????????????????????????????????????????????????
    }

    public void asrStop() {
        //asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
        //asr.send(SpeechConstant.ASR_CANCEL, null, null, 0, 0); // ????????????
        if (asr == null) {
            return;
        }
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
        asr.unregisterListener(asrListener);
        asr = null;
        Log.e("connection", "asrStop");
    }

    public void asrStart() {
        if (asr != null) {
            asrStop();
        }
        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(asrListener); //  EventListener ??? onEvent??????
        Map<String, Object> params = new LinkedHashMap<>();
        // ??????SDK??????2.1 ??????????????????
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0);
        //params.put(SpeechConstant.APP_ID, "");
        //params.put(SpeechConstant.APP_KEY, "");
        //params.put(SpeechConstant.APP_NAME, "");
        String json = new JSONObject(params).toString(); // ???????????????????????????????????????json
        asr.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     *
     * @return
     */
    private Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        // ????????????????????????
        // ???????????????????????????0 ???????????????????????? 1 ???????????? 2 ???????????? 3 ????????????<?????????> 4 ???????????????<?????????>
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "4");
        // ????????????????????????0-9 ????????? 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "9");
        // ????????????????????????0-9 ????????? 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
        // ????????????????????????0-9 ????????? 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "1");
        return params;
    }
}
