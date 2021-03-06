package com.spd.lfrfid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.lflibs.DeviceControl;
import com.android.lflibs.serial_native;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

/**
 * @author xuyan
 */
public class LFRFIDActivity extends Activity implements OnCheckedChangeListener, OnClickListener {
    /** Called when the activity is first created. */
	private DeviceControl DevCtrl;
	private static final String SERIALPORT_PATH = "/dev/ttyMT2";
	private static final int BUFSIZE = 64;
	
	private ToggleButton powerBtn;
	private Button	clearBtn;
	private Button	closeBtn;
	private TextView	contView;
	
	private File device_path;
	private BufferedWriter proc;
	private serial_native NativeDev;
	private Handler handler;
	private ReadThread reader;
//	private long dec_result;
	private int size=0;
	private byte xor_result =0;
	private int count0=0;
	private int count1=0;
	private int count2=0;
	private int count3=0;
	private int count4=0;
	private int count5=0;
	
    @SuppressLint("HandlerLeak")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        powerBtn = (ToggleButton)findViewById(R.id.toggleButton_power);
        powerBtn.setOnCheckedChangeListener(this);
        
        clearBtn = (Button)findViewById(R.id.button_clear);
        clearBtn.setOnClickListener(this);
        
        closeBtn = (Button)findViewById(R.id.button_close);
        closeBtn.setOnClickListener(this);
        
        contView = (TextView)findViewById(R.id.tv_content);
        
        NativeDev = new serial_native();
        if(NativeDev.OpenComPort(SERIALPORT_PATH) < 0)
        {
        	contView.setText(R.string.Status_OpenSerialFail);
        	powerBtn.setEnabled(false);
        	clearBtn.setEnabled(false);
        	return;
        }
        
        try {
        	DevCtrl = new DeviceControl("/sys/class/misc/mtgpio/pin");
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
        	contView.setText(R.string.Status_OpenDevFileFail);
        	powerBtn.setEnabled(false);
        	clearBtn.setEnabled(false);
        	new AlertDialog.Builder(this).setTitle(R.string.DIA_ALERT).setMessage(R.string.DEV_OPEN_ERR).setPositiveButton(R.string.DIA_CHECK, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					finish();
				}
			}).show();
        	return;
		}
        handler = new Handler() {
        	@Override
        	public void handleMessage(Message msg) {
        		super.handleMessage(msg);
        		if(msg.what == 1)
        		{
        		  byte[] buf = (byte[]) msg.obj;
        		  if(buf.length==30)
           		   {
        			    for(int a=1; a<27;a++)
        			    {
        			    	xor_result^= buf[a];
        			    }  
        			    String cnt =  new String(buf);
        			    String[] serial_number= new String[30];
        			    serial_number[9] = cnt.substring(1,2);
        			    serial_number[8] = cnt.substring(2,3);
        			    serial_number[7] = cnt.substring(3,4);
        			    serial_number[6] = cnt.substring(4,5);
        			    serial_number[5] = cnt.substring(5,6);
        			    serial_number[4] = cnt.substring(6,7);
        			    serial_number[3] = cnt.substring(7,8);
        			    serial_number[2] = cnt.substring(8,9);
        			    serial_number[1] = cnt.substring(9,10);
        			    serial_number[0] = cnt.substring(10,11);        			    
        			    String reverse = serial_number[0]+serial_number[1]+serial_number[2]+serial_number[3]+serial_number[4]+serial_number[5]+serial_number[6]+serial_number[7]+serial_number[8]+serial_number[9];       			    
        			    long dec_first= Long.parseLong(reverse,16);
        			    String string=Long.toString(dec_first); 
       			        size = string.length();       			     			    		
       			        switch (size)
        			    { 		 
        			    case 1 :// if (xor_result==buf[27])
                                  	  {		
           			        serial_number[10] = cnt.substring(14,15);
                            serial_number[11] = cnt.substring(13,14);
                            serial_number[12] = cnt.substring(12,13);
                            serial_number[13] = cnt.substring(11,12);
       		                String country_code = serial_number[11]+serial_number[12]+serial_number[13];
    			    		long dec_result=Long.parseLong(country_code,16);
    			    		String second_dec=Long.toString(dec_result);
				  			    		  String combine = second_dec + "0" + "0" + "0" + "0" + "0" + "0"+ "0" + "0" + "0" + "0" + "0" +string ; 
				  			    		  contView.setTextSize(30);
						    		      contView.append(combine); 
						    		      contView.append("\n");
						    		   //   break;
	                                  } 
        			   break;			    		        			    	
        			    case 2 : // if (xor_result==buf[27])
			                          {	
           			        serial_number[10] = cnt.substring(14,15);
                            serial_number[11] = cnt.substring(13,14);
                            serial_number[12] = cnt.substring(12,13);
                            serial_number[13] = cnt.substring(11,12);
       		                String country_code = serial_number[11]+serial_number[12]+serial_number[13];
    			    		long dec_result=Long.parseLong(country_code,16);
    			    		String second_dec=Long.toString(dec_result);
				  			    		  String combine = second_dec + "0" + "0" + "0" + "0" + "0" + "0"+ "0" + "0" + "0" + "0" +string ; 
				  			    		  contView.setTextSize(30);
						    		      contView.append(combine); 
						    		      contView.append("\n");
						    		   //   break;
			                          } 
        			   break;
        			    case 3: // if (xor_result==buf[27])
						              {	
           			        serial_number[10] = cnt.substring(14,15);
                            serial_number[11] = cnt.substring(13,14);
                            serial_number[12] = cnt.substring(12,13);
                            serial_number[13] = cnt.substring(11,12);
       		                String country_code = serial_number[11]+serial_number[12]+serial_number[13];
    			    		long dec_result=Long.parseLong(country_code,16);
    			    		String second_dec=Long.toString(dec_result);
					  			    	  String combine = second_dec+ "0" + "0" + "0" + "0" + "0" + "0"+ "0" + "0" + "0" +string ;
					  			    	  contView.setTextSize(30);
							    		  contView.append(combine);  
							    		  contView.append("\n");
							    		 // break;
			                          }
        			    break; 
        			    case 4:   //if (xor_result==buf[27])
			                          {		
           			        serial_number[10] = cnt.substring(14,15);
                            serial_number[11] = cnt.substring(13,14);
                            serial_number[12] = cnt.substring(12,13);
                            serial_number[13] = cnt.substring(11,12);
       		                String country_code = serial_number[11]+serial_number[12]+serial_number[13];
    			    		long dec_result=Long.parseLong(country_code,16);
    			    		String second_dec=Long.toString(dec_result);
					  			    	  String combine = second_dec+ "0" + "0" + "0" + "0" + "0" + "0"+ "0" + "0" +string ;  
					  			   	      contView.setTextSize(30);
							    		  contView.append(combine); 
							    		  contView.append("\n");
							    		//  break;
	                                  }
        			    break; 
        			    case 5:  // if (xor_result==buf[27])
			                          {		
           			        serial_number[10] = cnt.substring(14,15);
                            serial_number[11] = cnt.substring(13,14);
                            serial_number[12] = cnt.substring(12,13);
                            serial_number[13] = cnt.substring(11,12);
       		                String country_code = serial_number[11]+serial_number[12]+serial_number[13];
    			    		long dec_result=Long.parseLong(country_code,16);
    			    		String second_dec=Long.toString(dec_result);
					  			    	  String combine = second_dec+ "0" + "0" + "0" + "0" + "0" + "0"+ "0" +string ;
					  			    	  contView.setTextSize(30);
							    		  contView.append(combine); 
							    		  contView.append("\n");
							    		//  break;
	                                  }
        			    break;
        			     case 6:  // if (xor_result==buf[27])
			                          {		
            			        serial_number[10] = cnt.substring(14,15);
                                serial_number[11] = cnt.substring(13,14);
                                serial_number[12] = cnt.substring(12,13);
                                serial_number[13] = cnt.substring(11,12);
           		                String country_code = serial_number[11]+serial_number[12]+serial_number[13];
        			    		long dec_result=Long.parseLong(country_code,16);
        			    		String second_dec=Long.toString(dec_result);
					  			    	  String combine = second_dec+ "0" + "0" + "0" + "0" + "0" + "0" +string ;
					  			    	  contView.setTextSize(30);
							    		  contView.append(combine); 
							    		  contView.append("\n");
							    		//  break;
	                                  }
        			     break;
        			    case 7:   // if (xor_result==buf[27])
			                         {		
           			        serial_number[10] = cnt.substring(14,15);
                            serial_number[11] = cnt.substring(13,14);
                            serial_number[12] = cnt.substring(12,13);
                            serial_number[13] = cnt.substring(11,12);
       		                String country_code = serial_number[11]+serial_number[12]+serial_number[13];
    			    		long dec_result=Long.parseLong(country_code,16);
    			    		String second_dec=Long.toString(dec_result);
					  			    	  String combine = second_dec+ "0" + "0" + "0" + "0" + "0" +string ;
					  			    	  contView.setTextSize(30);
							    		  contView.append(combine); 
							    		  contView.append("\n");
							    		 // break;
	                                  } 
        			    break;
        			    case 8:   // if (xor_result==buf[27])
			                          {		
           			        serial_number[10] = cnt.substring(14,15);
                            serial_number[11] = cnt.substring(13,14);
                            serial_number[12] = cnt.substring(12,13);
                            serial_number[13] = cnt.substring(11,12);
       		                String country_code = serial_number[11]+serial_number[12]+serial_number[13];
    			    		long dec_result=Long.parseLong(country_code,16);
    			    		String second_dec=Long.toString(dec_result);
					  			    	  String combine = second_dec+ "0" + "0" + "0" + "0" +string ; 
					  			    	  contView.setTextSize(30);
							    		  contView.append(combine); 
							    		  contView.append("\n");
							    		//  break;
	                                  } 
        			    break;
        			    case 9:   // if (xor_result==buf[27])
			                         {		
           			        serial_number[10] = cnt.substring(14,15);
                            serial_number[11] = cnt.substring(13,14);
                            serial_number[12] = cnt.substring(12,13);
                            serial_number[13] = cnt.substring(11,12);
       		                String country_code = serial_number[11]+serial_number[12]+serial_number[13];
    			    		long dec_result=Long.parseLong(country_code,16);
    			    		String second_dec=Long.toString(dec_result);
					  			    	  String combine = second_dec+ "0" + "0" + "0"+string ;
					  			    	  contView.setTextSize(30);
							    		  contView.append(combine); 
							    		  contView.append("\n");
							    		//  break;
	                                 } 
        			    break;
        			    case 10:  // if (xor_result==buf[27])
			                          {		
           			        serial_number[10] = cnt.substring(14,15);
                            serial_number[11] = cnt.substring(13,14);
                            serial_number[12] = cnt.substring(12,13);
                            serial_number[13] = cnt.substring(11,12);
       		                String country_code = serial_number[11]+serial_number[12]+serial_number[13];
    			    		long dec_result=Long.parseLong(country_code,16);
    			    		String second_dec=Long.toString(dec_result);
					  			    	  String combine = second_dec+ "0" + "0" +string ; 
					  			    	  contView.setTextSize(30);
							    		  contView.append(combine);
							    		  contView.append("\n");
							    	//	  break;
	                                 } 
        			    break;
        			    case 11:   //if (xor_result==buf[27])
					                  {		
           			        serial_number[10] = cnt.substring(14,15);
                            serial_number[11] = cnt.substring(13,14);
                            serial_number[12] = cnt.substring(12,13);
                            serial_number[13] = cnt.substring(11,12);
       		                String country_code = serial_number[11]+serial_number[12]+serial_number[13];
    			    		long dec_result=Long.parseLong(country_code,16);
    			    		String second_dec=Long.toString(dec_result);
					  			    	  String combine = second_dec + "0" + string ;  
					  			    	  contView.setTextSize(30);
							    		  contView.append(combine); 
							    		  contView.append("\n"); 
							    	//	  break;
			                          }
        			    break;	
        			    case 12:   // if (xor_result==buf[27])
						                  {	
           			        serial_number[10] = cnt.substring(14,15);
                            serial_number[11] = cnt.substring(13,14);
                            serial_number[12] = cnt.substring(12,13);
                            serial_number[13] = cnt.substring(11,12);
       		                String country_code = serial_number[11]+serial_number[12]+serial_number[13];
    			    		long dec_result=Long.parseLong(country_code,16);
    			    		String second_dec=Long.toString(dec_result);
						  			    	  String combine = second_dec + string ; 
						  			    	  contView.setTextSize(30);
								    		  contView.append(combine); 
								    		  contView.append("\n"); 
								    		//  break;
				                          }	
        			    break;
        			    	default:
        			    		break;
        			    } 
        			}
           		  
       		   else{
						     String cnt =  new String(buf);
						     count0=Integer.parseInt(cnt.substring(1, 3),16);       			  
						     count1=Integer.parseInt(cnt.substring(3, 5),16);
						     count2=Integer.parseInt(cnt.substring(5, 7),16);
						     count3=Integer.parseInt(cnt.substring(7, 9),16);
						     count4=Integer.parseInt(cnt.substring(9, 11),16);        			     
						     count5=count0^count1^count2^count3^count4;
						     byte[] b= new byte[4];
							 b[0] = (byte) (count5 & 0xff );
							 if(b[0]==buf[11])
						     {
							 contView.setTextSize(30);
						     contView.append(cnt.substring(1,cnt.length()-2));
						     contView.append("\n"); 						     
						     }
        		         }    				
        	    }
        	}
        };       
    }
    
    @Override
    public void onDestroy() {
    	if(powerBtn.isChecked())
    	{
    		try {
    			reader.interrupt();
    			DevCtrl.PowerOffDevice();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	NativeDev.CloseComPort();
		super.onDestroy();
    }

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// TODO Auto-generated method stub
		if(arg1)
		{
			try {
				DevCtrl.PowerOnDevice();
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				NativeDev.ClearBuffer();
				reader = new ReadThread();
				reader.start();
//				contView.setText(" status is " + powerBtn.isChecked());
			} catch (IOException e) {
				contView.setText(R.string.Status_ManipulateFail);
			}
		}
		else
		{
			try {
    			reader.interrupt();
				try {
					Thread.sleep(3);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				DevCtrl.PowerOffDevice();
//				contView.setText(" status is " + powerBtn.isChecked());
        	} catch (IOException e) {
        		contView.setText(R.string.Status_ManipulateFail);
        	}
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0 == clearBtn)
		{
			contView.setText("");
		}
		else if(arg0 == closeBtn)
		{
			finish();
		}
	}
	
	class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			Log.d("lfrfid", "thread start");
			while(!isInterrupted()) {
				byte[] buf = NativeDev.ReadPort(BUFSIZE);					
				if(buf != null)
				{
					Message msg = new Message();
/*					for(byte a: buf)
					{
						Log.d("lfrfid", String.format("%02x", a));
					}*/
					
					if(buf.length >= 2)
					{	
						size=0;
						msg.what = 1;
						msg.obj = buf;					
						handler.sendMessage(msg);		
					}
				}
			}
			Log.d("lfrfid", "thread stop");
		}
	}
}