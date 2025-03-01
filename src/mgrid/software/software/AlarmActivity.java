package mgrid.software.software;

import java.util.HashMap;
import mgrid.software.software.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmActivity extends Activity implements OnScrollListener,Runnable {
	
	   private ListView listView;  
       private int visibleLastIndex = 0;   //最后的可视项索引  
	   private int visibleItemCount;       // 当前窗口可见项总数  
	   private ListAdapter adapter;  
	   private View loadMoreView;  
	   private Button loadMoreButton;  
	   private boolean bQuery;
	   private int alarmlevel;
	   private int currentpage;
	   private int pagesize;
	   private boolean bPause;
	   private Handler handler;  
	   private TextView alarmcount;
	   private ImageButton alarmlevelimage;
	   private ImageButton alarmlock;
	   private boolean bReflesh;

	 @Override  
     public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        initdate();
      
        setContentView(R.layout.alarmlayout);           
         loadMoreView = getLayoutInflater().inflate(R.layout.alarmloadmore, null);  
         loadMoreButton = (Button) loadMoreView.findViewById(R.id.alarmloadmore);  
         
         alarmlevelimage=(ImageButton)findViewById(R.id.alarmlevel);
   	    alarmlock=(ImageButton)findViewById(R.id.alarmlock);
   	      
         alarmcount=(TextView)findViewById(R.id.alarmcount);
         listView  = (ListView ) findViewById(R.id.active_alarm_lv);         
         listView.addFooterView(loadMoreView);   //设置列表底部视图    
	     initAdapter();         
	     listView.setOnScrollListener(this);     //添加滑动监听  
	     handler =new Handler()
	  	 {
	  	    @Override
	  	   //当有消息发送出来的时候就执行Handler的这个方法
	  	 	  public void handleMessage(Message msg){
	  	    	
	  		 super.handleMessage(msg);	
	  		DataAccess.uialiveevents.clear();
	   		for(int i=0;i<DataAccess.aliveevents.size();i++)
	   		{
	   			DataAccess.uialiveevents.add((HashMap<String, Object>) (DataAccess.aliveevents.get(i).clone()));
	   			
	   		}
	   		alarmcount.setText(Integer.toString(DataAccess.totalalarm));
	   		listView.setVisibility(View.GONE);
	   		((SimpleAdapter)adapter).notifyDataSetChanged();
		
			 listView.setVisibility(View.VISIBLE);    
	   		
	  	    }
	     };
	     alarmlock.setOnClickListener(new OnClickListener() {

			 
		 		public void onClick(View v) {
		 			
		 			currentpage=0;
	 				bReflesh=true;
		 			alarmlock.setImageResource(R.drawable.unlock);
		 			alarmlock.setClickable(false);
		 			}

		 	});
	     alarmlevelimage.setOnClickListener(new OnClickListener() {

			 
	 		public void onClick(View v) {
	 			switch(alarmlevel)
	 			{
	 			case 0:
	 				alarmlevelimage.setImageResource(R.drawable.eventseverity4);
	 				
	 				alarmlevel=4;
	 				currentpage=0;
	 				bReflesh=true;
	 				break;
	 			case 4:
	 				alarmlevelimage.setImageResource(R.drawable.eventseverity3);	 
	 				alarmlevel=3;
	 				currentpage=0;
	 				bReflesh=true;
	 				break;

	 			case 3:
	 				alarmlevelimage.setImageResource(R.drawable.eventseverity2);	 
	 				alarmlevel=2;
	 				currentpage=0;
	 				bReflesh=true;
	 				break;

	 			case 2:
	 				alarmlevelimage.setImageResource(R.drawable.eventseverity1);	
	 				alarmlevel=1;
	 				currentpage=0;
	 				bReflesh=true;
	 				break;

	 			case 1:
	 				alarmlevelimage.setImageResource(R.drawable.eventseverity0);	
	 				alarmlevel=0;
	 				currentpage=0;
	 				bReflesh=true;
	 				break;

	 			
	 			
	 			}
	 			currentpage=0;
 				bReflesh=true;
	 			alarmlock.setImageResource(R.drawable.unlock);
	 			alarmlock.setClickable(false);
	 				}

	 	});
	   }  
	 private void initdate()
	 {
		 bQuery=false;
		  alarmlevel=0;
		  currentpage=0;
		  pagesize=20;
		  bPause=false;
		  bReflesh=false;
		 
	 }
	 private void initAdapter() {  
		    String[] ColumnNames = {"Seq","Name","Position","StartTime","Level"};    
	 	    adapter = new SimpleAdapter(this, DataAccess.uialiveevents, 
	                    R.layout.alarmlistviewitem,  ColumnNames, new int[] { R.id.active_alarm_id,   R.id.active_alarm_descript, R.id.active_alarm_position, R.id.active_alarm_starttime, R.id.active_alarm_level});  
	 	    
	 	    listView.setAdapter(adapter);
	       }  

	  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {  
         this.visibleItemCount = visibleItemCount;  
	        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;  
	     }  
	     /** 
	     * 滑动状态改变时被调用 
     */  
  
    public void onScrollStateChanged(AbsListView view, int scrollState) {  
        int itemsLastIndex = adapter.getCount() - 1;  
        //数据集最后一项的索引  
        int lastIndex = itemsLastIndex + 1;             //加上底部的loadMoreView项  
	       if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {  
	           //如果是自动加载,可以在这里放置异步加载数据的代码  
	        
	        }  
	    }  
	      
  
	    /** 
     * 点击按钮事件 
	     * @param view 
	     */  
	     public void loadMore(View view) {  
	            
	            
	        	 	bQuery=true;
	        		alarmlock.setImageResource(R.drawable.lock);
	        		alarmlock.setClickable(true);
           
	    }  
	     private void loadData() {  
	    	 
	    
	     }  
	     public void onResume()
		    {
	    	  super.onResume();
	    	  try
	    	  {
	        	new Thread(AlarmActivity.this).start();
	    	  }
	    	  catch(Exception e)
	    	  {
	    		  
	    		  String aa= e.getMessage();
	    		  aa=aa;
	    		  
	    	  }
	        
		   	  
		    }
	 
		    protected void onPause()
		    {
		    	super.onPause();
		    	bPause=true;
		    	
		    }
		    
	     public void run()
	     {
	     	try
	     	{    currentpage=0;
	     	    bPause=false;
	     	 	  DataAccess.GetActiveAlarm(DataAccess.stationid,alarmlevel,currentpage,pagesize);	
	     	      handler.sendEmptyMessage(0);
	               while(!bPause)
	                  {
	     		   	    for(int i=0;i<30;i++)
	     		   	    {
	     		   	    	if(bPause)
	     		   	    	{
	     		   	    		return ;
	     		   	    	}
	     		   	    	if(bQuery)
	     		   	    	{
	     		   	    		if(currentpage!=(DataAccess.uialiveevents.size())/pagesize)
	     		   	    		{	     		   	    			
	     		   	    			currentpage=DataAccess.uialiveevents.size()/pagesize;
	     		   	    			DataAccess.GetActiveAlarm(DataAccess.stationid,alarmlevel,currentpage,pagesize);	
	     		   	    			handler.sendEmptyMessage(0);
	     		   	    		}
	     		   	    		//DataAccess.GetActiveAlarm(DataAccess.stationid,alarmlevel,currentpage,pagesize);	
	     		   	    		//handler.sendEmptyMessage(0);
	     		   	    		break;
	     		   	    	}
	     		   	    	if(bReflesh)
	     		   	    	{
	     		   	    	  currentpage=0;
	     		   	    	  DataAccess.GetActiveAlarm(DataAccess.stationid,alarmlevel,currentpage,pagesize);	
	  	     	     	      handler.sendEmptyMessage(0);
	  	     	     	  bReflesh=false;
	     		   	    		
	     		   	    	}
	             	 	   Thread.sleep(1000);	
	     		   	    }
	     		   	    bQuery= false;
	     		   	  if(currentpage==0)
	     		   	  {
	     		 	    DataAccess.GetActiveAlarm(DataAccess.stationid,alarmlevel,currentpage,pagesize);	
	     	     	    handler.sendEmptyMessage(0);
	     		   	   }
	     		   
	                  }
	     	}	         
	 	   catch (InterruptedException e) 
	 		{
	 		  bPause=false;
	 		  bQuery=false;
	 	     	new Thread(AlarmActivity.this).start();
	 			  e.printStackTrace();
	 		   }
	     	
	       }
	     
	     @Override
		    public void onBackPressed() {
		    	 
				   AlertDialog.Builder builder = new Builder(AlarmActivity.this);
				   builder.setTitle("MainTabActivity提示");
				   builder.setMessage("你真的要退出手机监控系统吗？");
				   Builder setPositiveButton = builder.setPositiveButton("是",
				     new android.content.DialogInterface.OnClickListener() {
				   

					public void onClick(DialogInterface dialog, int which) {
						  //Intent i = new Intent(Intent.ACTION_MAIN);
							 
					      //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 
					      //i.addCategory(Intent.CATEGORY_HOME);
					 
					      //startActivity(i);
					      
					      System.exit(0);
					}
				     });
				   builder.setNegativeButton("否",
				     new android.content.DialogInterface.OnClickListener() {
				      public void onClick(DialogInterface dialog, int which) {
				       dialog.dismiss();
				      }
				     });
				   builder.create().show();
				 
				 }


}
