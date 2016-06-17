package his.app;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DisplayConfigActivity extends Activity {
	
	private EditText configEditText;
	private Button confirmBtn;
    private Button testConnect;
	
	View main;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.openconfig);
        
        configEditText=(EditText)findViewById(R.id.et_configText);
        confirmBtn=(Button)findViewById(R.id.btn_confirm);
        testConnect = (Button)findViewById(R.id.btn_testConnect);
        
        SharedPreferences sp = getSharedPreferences("userInfo",
				Context.MODE_WORLD_READABLE);
        configEditText.setText(sp.getString("DISPLAY_WEBSERVICE", ""));
        
        confirmBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setConfig();
			}
		});
        
        testConnect.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(configEditText.length()==0){
					disPlayToast("�������ַ");
				}else{
					ConnectHanlder ch = new ConnectHanlder();
					if(ch.Ping(configEditText.getText().toString())){
						disPlayToast("������ͨ�ɹ�");
					}else{
						disPlayToast("������ͨʧ��");
					}
				}
			}
		});
    }
    
    private void setConfig(){
    	String textContext=configEditText.getText().toString();
    	if(textContext.equals("")) return;
    	
    	SharedPreferences sp = getSharedPreferences("userInfo",
				Context.MODE_WORLD_READABLE);
		sp.edit().putString("DISPLAY_WEBSERVICE", textContext).commit();

		
    	Intent intent=new Intent();
    	intent.setClass(DisplayConfigActivity.this, HISQueueDisplayActivity.class);
    	startActivity(intent);
    	DisplayConfigActivity.this.finish();
    }
    
    private void exitActivity(){
    	//DisplayConfigActivity.this.finish();
		//System.exit(0);
    	Intent intent = new Intent();
		intent.setClass(DisplayConfigActivity.this, HISQueueDisplayActivity.class);
		startActivity(intent);
		DisplayConfigActivity.this.finish();
	}
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitActivity();		
		}
		return true;

	}

    public void disPlayToast(String s){
    	Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
		// ����toast��ʾ��λ��
		toast.setGravity(Gravity.TOP, 0, 80);
		// ��ʾ��Toast
		toast.show();
    }
    
    /*
    private void ToTestActivity(View view){
    	Intent intent = new Intent();
    	intent.setClass(DisplayConfigActivity.this,TestActivity.class);
    	startActivity(intent);
    	DisplayConfigActivity.this.finish();
    }*/
}