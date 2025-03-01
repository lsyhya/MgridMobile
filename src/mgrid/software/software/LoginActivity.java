package mgrid.software.software;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import mgrid.software.software.crash.DialogUtils;
import mgrid.software.software.crash.MyApplication;

public class LoginActivity extends Activity {
	/*
	 * author: conowen date: 2012.4.2
	 * 
	 */
	SQLiteDatabase sqldb;
	public String DB_NAME = "db.sqlite";
	public String DB_TABLE = "num";
	public int DB_VERSION = 1;
	final DataHelper helper = new DataHelper(this, DB_NAME, null, DB_VERSION);
	public static String dbName = "db.sqlite";// 数据库的名字
	private static String DATABASE_PATH = "/data/data/mgrid.software.software/databases/";// 数据库在手机里的路径

	AutoCompleteTextView cardNumAuto;
	EditText passwordET;
	Button logBT;
	EditText serverip;
	EditText serverport;
	CheckBox savePasswordCB;
	SharedPreferences sp;
	String cardNumStr;
	String passwordStr;
	ProgressBar progressbar;
	public static Context context = null;
	
	public static ExecutorService xianChengChi = Executors.newCachedThreadPool();
	
	
	public static Handler handlers = new Handler() {

		public void handleMessage(Message msg) {

			Object obj = msg.obj;
			
			switch (msg.what) {
			
			
			case 1:

				
				DialogUtils.getDialog().showDialog(context, "发送异常,程序停止运行,请查看日志", "日志详细："+(String)obj);
				
				
				break;

			}

		};

	};

	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		DataAccess.Init();
		boolean dbExist = checkDataBase();
		
		
		
		if (dbExist) {

		} else {// 不存在就把raw里的数据库写入手机
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
		sqldb = helper.getWritableDatabase();
		DataAccess.clientid = -1;
		SystemSetting.ServerIp = GetSettings("SERVERIP");
		// SystemSetting.ServerIp = "220.231.192.87";

		SystemSetting.ServerPort = GetSettings("SERVERPORT");
		setContentView(R.layout.loginlayout);
		context=this;
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
					.detectNetwork().detectAll().penaltyLog().build());
			StrictMode.setVmPolicy(
					new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
		}

		
		
		progressbar = (ProgressBar) findViewById(R.id.progressBar1);
		progressbar.setVisibility(View.INVISIBLE);
		cardNumAuto = (AutoCompleteTextView) findViewById(R.id.cardNumAuto);
		passwordET = (EditText) findViewById(R.id.passwordET);
		String pwd = GetSettings("PWD");
		if (pwd.length() > 0) {
			passwordET.setText(pwd);

		}
		logBT = (Button) findViewById(R.id.logBT);
		serverip = (EditText) findViewById(R.id.login_serverip);
		if (SystemSetting.ServerIp.length() > 0) {
			serverip.setText(SystemSetting.ServerIp);
		}

		serverport = (EditText) findViewById(R.id.login_serverport);
		if (SystemSetting.ServerPort.length() > 0) {
			serverport.setText(SystemSetting.ServerPort);
		}

		sp = this.getSharedPreferences("passwordFile", MODE_PRIVATE);
		savePasswordCB = (CheckBox) findViewById(R.id.savePasswordCB);
		savePasswordCB.setChecked(true);// 默认为记住密码
		cardNumAuto.setThreshold(1);// 输入1个字母就开始自动提示
		passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		// 隐藏密码为InputType.TYPE_TEXT_VARIATION_PASSWORD，也就是0x81
		// 显示密码为InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD，也就是0x91

		cardNumAuto.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				String[] allUserName = new String[sp.getAll().size()];// sp.getAll().size()返回的是有多少个键值对
				allUserName = sp.getAll().keySet().toArray(new String[0]);
				// sp.getAll()返回一张hash map
				// keySet()得到的是a set of the keys.
				// hash map是由key-value组成的

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginActivity.this,
						android.R.layout.simple_dropdown_item_1line, allUserName);

				cardNumAuto.setAdapter(adapter);
				// 设置数据适配器

			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				passwordET.setText(sp.getString(cardNumAuto.getText().toString(), ""));// 自动输入密码

			}
		});

		// 登陆
		logBT.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
			
				
				progressbar.setVisibility(View.VISIBLE);

				SystemSetting.ServerIp = serverip.getText().toString();
				UpdateKeyValue("SERVERIP", SystemSetting.ServerIp);
				// String Ip=GetSettings("SERVERIP");

				SystemSetting.ServerPort = serverport.getText().toString();
				UpdateKeyValue("SERVERPORT", SystemSetting.ServerPort);
				String port = GetSettings("SERVERPORT");

				if (port.length() <= 0 && SystemSetting.ServerPort.length() > 0) {
					InsertKeyValue("SERVERPORT", SystemSetting.ServerPort);
					port = GetSettings("SERVERPORT");
				}
				cardNumStr = cardNumAuto.getText().toString();
				passwordStr = passwordET.getText().toString();

				// showprogressbar();

				if (savePasswordCB.isChecked()) {// 登陆成功才保存密码
					sp.edit().putString(cardNumStr, passwordStr).commit();
					UpdateKeyValue("PWD", passwordStr);
					String pwd = GetSettings("PWD");
					if (pwd.length() <= 0) {
						InsertKeyValue("PWD", passwordStr);
					}
				}
				new Thread() {
					@Override
					public void run() {
						try {

							int longinstatus = 1;
							int rtn = DataAccess.GetClientId(cardNumStr, passwordStr);

							if (rtn == -2) {
								// 用户名或秘密错误
								int longinstatus3 = 3;
								Message msg3 = Message.obtain();
								msg3.obj = longinstatus3;
								handler.sendMessage(msg3);
								return;
							}
							if (rtn < 0) {
								// 网络原因导致登陆失败
								longinstatus = 0;
								Message msg1 = Message.obtain();
								msg1.obj = longinstatus;
								handler.sendMessage(msg1);
								return;
							}

							longinstatus = 1;
							Message msg = Message.obtain();
							msg.obj = longinstatus;
							handler.sendMessage(msg);

//							Thread.sleep(500);
//							DataAccess.GetStations(rtn);

							// 登陆成功
							int longinstatus1 = 2;
							Message msg1 = Message.obtain();
							msg1.obj = longinstatus1;
							handler.sendMessage(msg1);

						} catch (Exception e) {
							int longinstatus = 0;
							Message msg = Message.obtain();
							msg.obj = longinstatus;
							handler.sendMessage(msg);
						}
						// 执行完毕后给handler发送一个空消息
					}
				}.start();
			}
		});

	}

	private Handler handler = new Handler() {
		@Override
		// 当有消息发送出来的时候就执行Handler的这个方法
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch ((Integer) msg.obj) {

			case 0:
				Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
				progressbar.setVisibility(View.INVISIBLE);

				break;
			case 2:
				progressbar.setVisibility(View.INVISIBLE);

				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, MainTabActivity.class);

				startActivity(intent);
				finish();

				break;
			case 1:
				progressbar.setVisibility(View.VISIBLE);

				Toast.makeText(LoginActivity.this, "正在获取 数据,请稍后…………",

						Toast.LENGTH_SHORT).show();

				break;
			case 3:
				Toast.makeText(LoginActivity.this, "用户名密码错误", Toast.LENGTH_SHORT).show();
				progressbar.setVisibility(View.INVISIBLE);

				break;
			}
		}
	};

	public void InsertKeyValue(String key, String value) {
		try {
			String sqlstr = "INSERT INTO settings(Keys, Value) VALUES('" + key + "','" + value + "')";
			sqldb.execSQL(sqlstr);
		} catch (Exception e) {
			e.getMessage();
		}
	}

	public void UpdateKeyValue(String key, String value) {
		String sqlstr = "UPDATE settings SET Value = " + "'" + value + "'" + "  WHERE Keys='" + key + "'";
		try {
			sqldb.execSQL(sqlstr);
		} catch (Exception e) {
			InsertKeyValue(key, value);
		}
	}

	public String GetSettings(String key) {
		String value = "";
		Cursor cr = null;
		try {
			cr = sqldb.query("settings", new String[] { "Value" }, "Keys=?", new String[] { key }, null, null, null);
			cr.moveToFirst();
			value = cr.getString(0);

		} catch (Exception e) {
			
			
			

		} finally {

			if (cr != null) {
				cr.close();
			}

		}

		return value;

	}

	/**
	 * 判断数据库是否存在
	 * 
	 * @return false or true
	 */
	public boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			String databaseFilename = DATABASE_PATH + dbName;
			checkDB = SQLiteDatabase.openDatabase(databaseFilename, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {

		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}

	public void copyDataBase() throws IOException {
		String databaseFilenames = DATABASE_PATH + dbName;
		File dir = new File(DATABASE_PATH);
		if (!dir.exists())// 判断文件夹是否存在，不存在就新建一个
			dir.mkdir();
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(databaseFilenames);// 得到数据库文件的写入流
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		InputStream is = LoginActivity.this.getResources().openRawResource(R.raw.db);// 得到数据库文件的数据流
		byte[] buffer = new byte[8192];
		int count = 0;
		try {

			while ((count = is.read(buffer)) > 0) {
				os.write(buffer, 0, count);
				os.flush();
			}
		} catch (IOException e) {

		}
		try {
			is.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	public void showprogressbar() {
		AlertDialog AlterD2 = new AlertDialog.Builder(LoginActivity.this).create();
		// 定义提示对话框
		LayoutInflater layoutInflater;
		// 定义布局过滤器
		LinearLayout myLayout;
		// 定义布局
		layoutInflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);
		// 获得系统的布局过滤服务
		myLayout = (LinearLayout) layoutInflater.inflate(R.layout.progressbar1, null);
		// 得到事先设计好的布局
		AlterD2.setTitle(getResources().getString(R.string.app_name));

		// 设置对话框标题
		AlterD2.setIcon(R.drawable.logo);
		// 设置对话框图标
		AlterD2.setMessage(getResources().getString(R.string.loginwait));
		// 设置对话框提示信息
		AlterD2.setView(myLayout);

		// 设置对话框中的View
		AlterD2.show();

	}

	@Override
	public void onResume() {
		super.onResume();
		DataAccess.inLoginactivetiy = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		DataAccess.inLoginactivetiy = false;

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DataAccess.bExit = true;
	}

}
