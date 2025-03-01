package mgrid.software.software;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import mgrid.software.software.R;

public class ReportActivityGroup extends ActivityGroup {
	 public static ActivityGroup reportgroup;
	 public static int activityindex=0;
	 
	 public void onCreate(Bundle savedInstanceState) {  
		       super.onCreate(savedInstanceState); 
		       reportgroup= this;
	    /*    setContentView(R.layout.realtimegrouplayout);  
	
		        container = (LinearLayout) findViewById(R.id.Container);  
		        container.removeAllViews();//
		        Intent intent =null;  
		        intent = new Intent(RealtimeActivityGroup.this, StationsActivity.class);  
		        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        Window subActivity = getLocalActivityManager().startActivity(  
		        	             "subActivity", intent); 
		        container.addView(subActivity.getDecorView(),  
		        		              LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);  
*/             
		   	DataAccess.activities.add(this);
		     }
	   @Override
       public boolean dispatchKeyEvent(KeyEvent event) {
    //	return super.dispatchKeyEvent(  event) ;       
    	//return true;
   // }
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP) {
            	   Intent intent;
		    	   Window w;
		    	   View view1;
		    	    switch(ReportActivityGroup.activityindex)
		    	    { 
		    	    case 0:
		    	    	this.onBackPressed();
 					 break;
 					   /*    case 1:
		    	    	  intent = new Intent(this, ReportGroupActivity.class);  
			    		   intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
						   w=ReportActivityGroup.reportgroup.getLocalActivityManager().startActivity("电站列表", intent);
						   view1=w.getDecorView();
						   ReportActivityGroup.reportgroup.setContentView(view1);   
						   ReportActivityGroup.activityindex=0;
						 break;
		 	    case 2:
		    	        	intent = new Intent(this, ReportListActivity.class);  
			    		 intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
						  w=ReportActivityGroup.reportgroup.getLocalActivityManager().startActivity("电站列表", intent);
						  view1=w.getDecorView();
						  ReportActivityGroup.reportgroup.setContentView(view1);   
						   ReportActivityGroup.activityindex=1;					 
						   break;
		    	    case 3:
		    	    	intent = new Intent(this, ReportListActivity.class);  
			    		 intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
						  w=ReportActivityGroup.reportgroup.getLocalActivityManager().startActivity("电站列表", intent);
						  view1=w.getDecorView();
						  ReportActivityGroup.reportgroup.setContentView(view1);   
						   ReportActivityGroup.activityindex=2;					
							
						 break;
		    	
		    	    }		    		
		    */	 
 					 default:
 						  intent = new Intent(this, ReportExpandableListActivity.class);  
			    		  intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
						  w=ReportActivityGroup.reportgroup.getLocalActivityManager().startActivity("电站列表", intent);
						  view1=w.getDecorView();
						  ReportActivityGroup.reportgroup.setContentView(view1);   
						   ReportActivityGroup.activityindex=0;					
							
						 break;
 						 
		    	    }
            	return true;
                	
            }
                return super.dispatchKeyEvent(event);
         }
	
		    
		      @Override
		     protected void onResume()
		      {
		    	   
		   	   super.onResume();
		    	   Intent intent =null;  
			       intent = new Intent(this, ReportExpandableListActivity.class);  
			       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			       Window w = getLocalActivityManager().startActivity(  
	        	            "ReportExpandableListActivity", intent); 
			       View view=w.getDecorView();
			       reportgroup.setContentView(view); 
			       ReportActivityGroup.activityindex=0;			
			       
		     }
		      
		      @Override
			    public void onBackPressed() {
			    	 
					   AlertDialog.Builder builder = new Builder(ReportActivityGroup.this);
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
