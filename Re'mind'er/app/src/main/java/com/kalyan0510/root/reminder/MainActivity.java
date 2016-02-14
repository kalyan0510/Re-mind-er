package com.kalyan0510.root.reminder;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.CallLog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

   /* BroadcastReceiver broadcaast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setSmallIcon(R.mipmap.kalyan);
            mBuilder.setContentTitle("hello GK");
            mBuilder.setContentText("heres ur details");
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(18456, mBuilder.build());
            System.out.println("RECIEVER CALLED\nRECIEVER CALLED\nRECIEVER CALLED\nRECIEVER CALLED\nRECIEVER CALLED\nRECIEVER CALLED\nRECIEVER CALLED\n");
        }
    };*/
    class res{
       String phone;
       String name;
       int H;
       int m;
       res(String p,String n,int h,int M){
           phone=p;
           name=n;
           H=h;
           m=M;
       }
   }
   class person{
       String number;
       String name;
       int hours;
       person(String pn,String nm,int hrs){
           number=pn; name=nm; hours = hrs;
       }
   }

    ArrayList<person> list;
    ArrayList<res> result;
    Chronometer chronometer;
    EditText et_name,et_num,et_inv,et_rem,et_int;
    Context thisc;
    void hidekey(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hidekey();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list= new ArrayList<person>();
        result = new ArrayList<res>();
        //Toast.makeText(MainActivity.this, getwifimac(), Toast.LENGTH_SHORT).show();
        //savecontacts();

        chronometer = (Chronometer)findViewById(R.id.chronometer);
        String str="";
        str+=loadcontacts();
        Button but = (Button)findViewById(R.id.button);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();

                //10 seconds later
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, 10);

                Intent intent = new Intent(getBaseContext(), RecursiveReceiver.class);

                //intent.putExtra("bc", new xcontext(getBaseContext()));
                //intent.putExtra("c",new xcontext(getApplicationContext()));
                PendingIntent pendingIntent =
                        PendingIntent.getBroadcast(getBaseContext(),
                                1, intent, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager alarmManager =
                        (AlarmManager)getSystemService(Context.ALARM_SERVICE);


                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                    {
                        alarmManager.set(AlarmManager.RTC_WAKEUP,
                                cal.getTimeInMillis(), pendingIntent);
                        Toast.makeText(getBaseContext(),
                                "Broadcast Started",
                                Toast.LENGTH_LONG).show();
                    }else{
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                                cal.getTimeInMillis(), pendingIntent);
                        /*Toast.makeText(getBaseContext(),
                                "call alarmManager.setExact()",
                                Toast.LENGTH_LONG).show();*/
                    }
                }


        });
        Button but2 = (Button)findViewById(R.id.button2);
        et_name=(EditText)findViewById(R.id.editText);
        et_num=(EditText)findViewById(R.id.editText2);
        et_inv=(EditText)findViewById(R.id.editText3);
        et_rem = (EditText) findViewById(R.id.editText4);
        et_int=((EditText)findViewById(R.id.editText5));
        et_name.getBackground().mutate().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_num.getText().toString().trim().equals("")||et_inv.getText().toString().trim().equals("")||et_name.getText().toString().trim().equals(""))
                {
                    Toast.makeText(MainActivity.this, "Empty Values", Toast.LENGTH_SHORT).show();
                    return;
                }
                    ParseObject obj = new ParseObject("contacts");
                obj.put("p", et_num.getText().toString().trim());
                et_num.setText("");
                obj.put("n", et_name.getText().toString().trim());
                et_name.setText("");
                obj.put("d", et_inv.getText() + "");
                et_inv.setText("");
                try {
                    obj.pin();
                    Toast.makeText(MainActivity.this, "Saved Successfully " + obj.getString("p") + obj.getString("n") + obj.getInt("d") + obj.getString("d"), Toast.LENGTH_SHORT).show();
                } catch (ParseException e) {
                    Toast.makeText(MainActivity.this, "Parse Error in Saving", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                ((TextView)findViewById(R.id.textView)).setText(loadcontacts());
            }
        });
        ((Button)findViewById(R.id.button3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_rem.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, "Empty fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseQuery<ParseObject> q = ParseQuery.getQuery("contacts");
                int r = 0;
                q.fromLocalDatastore();
                List<ParseObject> ol = null;
                String str = et_rem.getText().toString().trim();
                try {
                    ol = q.find();
                    for (ParseObject obj : ol) {
                        if (obj.getString("n").contains(str) || str.contains(obj.getString("n"))) {
                            obj.unpin();
                            r++;
                        }
                    }
                    Toast.makeText(MainActivity.this, r + " fav contacts removed", Toast.LENGTH_SHORT).show();
                    et_rem.setText("");
                    ((TextView) findViewById(R.id.textView)).setText(loadcontacts());
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });



        ((Button)findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!et_int.getText().toString().trim().equals(""))
                {
                    /*try {

                        ParseQuery<ParseObject> q = ParseQuery.getQuery("interval");
                        q.fromLocalDatastore();
                        ParseObject.unpinAll(q.find());
                        ParseObject po=new ParseObject("interval");
                        po.put("int",Integer.parseInt(et_int.getText().toString().trim()));
                        po.pin();
                        Toast.makeText(MainActivity.this, "Interval successfuly set to "+po.getInt("int")+"secs", Toast.LENGTH_SHORT).show();

                    }
                    catch (NumberFormatException nb){
                        Toast.makeText(MainActivity.this, "Please enter Valid number", Toast.LENGTH_SHORT).show();

                    } catch (ParseException e) {
                        e.printStackTrace();
                        ParseObject ob = new ParseObject("interval");
                        ob. put("int",15);
                        try {
                            ob.pin();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }*/
                    ((MyApplication)getApplication()).setX(Integer.parseInt(et_int.getText().toString().trim()));
                }
                   /* try {
                        ParseQuery<ParseObject> q = ParseQuery.getQuery("interval");
                        q.fromLocalDatastore();
                        ParseObject po= q.getFirst();*/
                        Toast.makeText(MainActivity.this, "Interval "+ ((MyApplication)getApplication()).getX()+"secs", Toast.LENGTH_SHORT).show();
                   /* }catch (ParseException e) {
                        e.printStackTrace();
                        ParseObject ob = new ParseObject("interval");
                        ob. put("int", 15);
                        try {
                            ob.pin();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }*/





            }
        });

        //str+= getCallDetails();
        ((TextView)findViewById(R.id.textView)).setText(str);
        ((TextView)findViewById(R.id.textView)).setMovementMethod(new ScrollingMovementMethod());
       // String x="";
       /* for(res r:result){
            x+=r.name+"  "+r.phone+" "+r.H+" "+r.m+"\n";
        }*/
        //((TextView)findViewById(R.id.textView)).setText(x+"\n"+str);
    }
    String loadcontacts(){
        list.clear();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("contacts");
        query.fromLocalDatastore();
        List<ParseObject> ol = null;
        String str = "";
        try {
            ol = query.find();
            for (ParseObject obj : ol) {
                list.add(new person(obj.getString("p"),obj.getString("n"),Integer.parseInt(obj.getString("d")+"")));
                str+=obj.getString("p")+"  "+obj.getString("n")+" "+Integer.parseInt(obj.getString("d"))+"\n";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        catch (NumberFormatException fe){
            Toast.makeText(MainActivity.this, "Sorry, there is some error in database. Need to clear data manually", Toast.LENGTH_LONG).show();
        }
        return str;
    }
    void savecontacts(){
        ArrayList<person> pl = new ArrayList<person>();
        pl.add(new person("9441110966","Bajje",12));
        pl.add(new person("9490573955","Daddy",12));
        pl.add(new person("9294104084","Baby",18));
        pl.add(new person("9494155150", "Lally", 12));
        pl.add(new person("9441146684", "USHA", 72));

        for(person p: pl){
            ParseObject obj = new ParseObject("contacts");
            obj.put("p",p.number);
            obj.put("n",p.name);
            obj.put("d",p.hours);
            obj.pinInBackground();
        }
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    String getwifimac(){
        WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return wm.getConnectionInfo().getBSSID()+" "+wm.getConnectionInfo().getFrequency()+" "+wm.getConnectionInfo().getLinkSpeed();
        }
        else return wm.getConnectionInfo().getBSSID()+"  "+wm.getConnectionInfo().getLinkSpeed();
    }

    private void getCallDetails() {

        //StringBuffer sb = new StringBuffer();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return ;
        }
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
                 int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER );
                 int type = managedCursor.getColumnIndex( CallLog.Calls.TYPE );
                 int date = managedCursor.getColumnIndex( CallLog.Calls.DATE);
                 int duration = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
                // sb.append("Call Details :");

                 while ( managedCursor.moveToNext() ) {
                     String phNumber = managedCursor.getString( number );
                     String callType = managedCursor.getString( type );
                     String callDate = managedCursor.getString( date );
                     Date callDayTime = new Date(Long.valueOf(callDate));
                     String callDuration = managedCursor.getString( duration );
                     String dir = null;
                     int dircode = Integer.parseInt( callType );
                    // sb.append( "\nPhone Number:--- "+phNumber +" \nCall Type:--- "+dir+" \nCall Date:--- "+callDayTime+" \nCall duration in sec :--- "+callDuration );

                     Date now = new Date();
                     long diff  = now.getTime() - callDayTime.getTime();

                     long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diff);
                     long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                     long diffInHours = TimeUnit.MILLISECONDS.toHours(diff);


                    // sb.append("\n Hours: "+diffInHours+" Minutes: "+diffInMinutes%60+"    list size-"+list.size());


                     //Here
                     person rem=null;
                     for(person x:list){
                         if(phNumber.contains(x.number)&&Integer.parseInt(callDuration)>15){
                             rem=x;
                            // sb.append("\n "+x.name+" REMOVED | REMOVED | REMOVED | REMOVED | REMOVED | REMOVED \n");
                             result.add(new res(phNumber, x.name, (int) diffInHours, (int) (diffInMinutes % 60)));
                             break;
                         }
                     }
                     if(rem!=null)
                         list.remove(rem);

                    // sb.append("\n----------------------------------");
                     if(list.isEmpty())
                         break;


                 }
                 managedCursor.close();
                    return;
             }


}
