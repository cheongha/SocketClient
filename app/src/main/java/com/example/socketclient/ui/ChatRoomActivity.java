package com.example.socketclient.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.socketclient.R;
import com.example.socketclient.data.ChatData;
import com.song2.chatting.adapter.ChatAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatRoomActivity extends AppCompatActivity {

    /** 소켓 관련 변수**/
    private Socket socket;              // 클라이언트의 소켓
    private DataInputStream dis;        // Input 통로
    private DataOutputStream dos;       // Output 통로
    private String ip = "ip address";   // Socket 연결할 서버 IP - AWS 고정 IP 사용
    private  Integer port = 3003;       // Socket 연결할 서버 Port

    /** 안드로이드 관련 변수**/
    Button btn_connect;         // 서버와 Socket 연결 Button
    Button btn_disconnect;      // 서버와 Socket 연결 종료 Button
    private EditText edit_msg;          // 서버로 전송할 메세지를 작성하는 EditText
    private String receiveMessage = "";      // 전달받은 메시지 데이터
    private String sendMessage = "";         // 전송할 메시지 데이터
    private String nickName = "";       // 앱 사용자 클라이언트 닉네임
    private String yourName = "";       // 상대 클라이언트 닉네임
    private String yourMessage = "";    // 상대 클라이언트가 보낸 메시지
    private Boolean isConnected = false;// 내부에서 연결 상태를 체크

    ChatAdapter chatAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        // LoginActivity에서 받은 nickName
        Intent intent = getIntent();
        nickName = intent.getStringExtra("nickName");

        btn_connect = findViewById(R.id.btn_chatact_connect);
        edit_msg = findViewById(R.id.edt_chatact_send_message);
        btn_disconnect = findViewById(R.id.btn_chatact_disconnect);

        // 리싸이클러뷰 셋팅
        recyclerView = findViewById(R.id.rv_chatact_chatlist);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        chatAdapter = new ChatAdapter();
        recyclerView.setAdapter(chatAdapter);
    }

    // CONNECT 버튼 클릭 시
    /** 소켓 작업 쓰레드 */
    public void clientSocket(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /** 소켓 생성 **/
                    socket = null;
                    // 소켓 서버에 접속
                    socket = new Socket(ip, port);

                    isConnected = true;

                    // 데이터를 주고 받을 통로 구축
                    dis = new DataInputStream(socket.getInputStream());
                    dos = new DataOutputStream(socket.getOutputStream());

                    // 서버와 연결되었다는 성공 메시지 띄워주기
                    ChatRoomActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(ChatRoomActivity.this, "Connected With Server", Toast.LENGTH_SHORT).show();
                        }
                    });
                    // 접속시 서버로 nickName 전달
                    dos.writeUTF(nickName);

                    /** read() 반복 실행 **/
                    while (true) {
                        // 받은 메시지 읽어 들이기
                        receiveMessage = dis.readUTF();
                        // 메시지 형식 처리
                        processMessage();
                    }
                } catch (IOException e) {
                    // 에러처리
                    e.printStackTrace();
                }
            }
        }).start(); //Thread 실행
    }

    // 받은 메시지 처리 함수
    public void processMessage(){
        // 받은 메시지 형식을 앱에 맞게 처리
        if (receiveMessage.split(":", 2).length == 2) {
            yourName = receiveMessage.split(":", 2)[0].trim();
            yourMessage = receiveMessage.split(":", 2)[1].trim();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 내가 보낸 메시지인지, 다른 클라이언트들이 보낸 메시지인지 판별
                    // 상대방이 보낸 메시지인 경우 메시지 출력
                    if (!nickName.equals(yourName)) {
                        // 배열에 아이템 추가
                        chatAdapter.addItem(new ChatData("you", yourName, yourMessage));
                        // Chatdata 변경 사항 적용
                        chatAdapter.notifyDataSetChanged();
                        // 메시지 오면 리싸이클러뷰의 가장 아래로 스크롤 포지션 변경
                        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                    }
                }
            });
        }
        // 전달받은 데이터 중 메시지가 아닌 경우("접속하셨습니다.", "나갔습니다") 처리
        else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatAdapter.addItem(new ChatData("other", yourName, receiveMessage.trim()));
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                }
            });
        }
    }

    // 보내기 버튼 클릭 시
    /** write() 메시지 전송 **/
    public void sendMessage(View view) {
        // 서버와 연결되어 있지 않다면 전송 불가
        if (dos == null || isConnected == false) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 입력한 메시지 Edittext에서 얻어오기
                    sendMessage = edit_msg.getText().toString();
                    // 입력한 메시지 AWT 형식에 맞도록 전송
                    dos.writeUTF(nickName + ":" + sendMessage + "\n");
                    // 다음 메시지 전송을 위해 연결통로 버퍼 지워주는 메소드
                    dos.flush();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatAdapter.addItem(new ChatData("me", nickName, sendMessage));
                            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                            edit_msg.setText("");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Send창의 입력 Edittext를 클릭 시 키보드 위로 메시지가 오도록 하는 함수
    public void OnFocusChange(View view){
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
            }
        }, 300);

    }

    // DISCONNECT 버튼 클릭시
    public void DisconnectSocket(View view){
        closeSocket();
    }

    /** 소켓 종료 함수 **/
    public void closeSocket(){
        if(isConnected == true){
            try{
                isConnected = false;
                socket.close();
                ChatRoomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ChatRoomActivity.this, "Disonnected With Server", Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 앱이 멈출 시 소켓 close() 처리
    @Override
    protected void onDestroy (){
        super.onDestroy();
        closeSocket();
    }
}
