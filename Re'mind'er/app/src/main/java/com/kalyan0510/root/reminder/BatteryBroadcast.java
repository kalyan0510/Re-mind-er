package com.kalyan0510.root.reminder;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by root on 13/2/16.
 */
public class BatteryBroadcast extends BroadcastReceiver {

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


    @Override
    public void onReceive(Context context, Intent intent) {

        list = new ArrayList<person>();
        result = new ArrayList<res>();
        loadcontacts(context);
        getCallDetails(context);
        int c=0;
        for(res x:result){
            call(context,x.name,x.H,x.m,++c,x.phone);
        }
        result.clear();
       // loadcontacts();
    }
    public Bitmap getbitmapfromnum(String num,Context context) {
        String contactID = null;

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(num));

        Cursor cursor =
                contentResolver.query(
                        uri,
                        new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID},
                        null,
                        null,
                        null);

        if(cursor!=null) {
            while(cursor.moveToNext()){
                String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
                contactID = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));

            }
            cursor.close();
        }
        Bitmap photo = null;
        if(contactID!=null){


            try {
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));

                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream);

                }else {
                    return null;
                }

                assert inputStream != null;
                inputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return photo;
    }
    public void call(Context c,String name,int h,int m,int id,String num) {
       // Toast.makeText(this,"call", Toast.LENGTH_SHORT).show();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel: "+num));
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, callIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(c)
                .setSmallIcon(R.mipmap.kalyan)
                .setContentTitle(c.getResources().getString(R.string.app_name))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        Bitmap pic = getbitmapfromnum(num,c);
        if(pic!=null)
        notificationBuilder.setLargeIcon(pic);
        else
        notificationBuilder.setLargeIcon( BitmapFactory.decodeResource(Resources.getSystem(), android.R.drawable.list_selector_background));

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        notificationBuilder.setStyle(inboxStyle);
        inboxStyle.setBigContentTitle(name);
        inboxStyle.addLine("been " + h + " hours " + m + " minutes");
        notificationBuilder.setStyle(inboxStyle);

        NotificationManager notificationManager =
                (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        int NOTIFICATION_ID = 100+id;
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
    String loadcontacts(Context context){
        list.clear();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("contacts");
        query.fromLocalDatastore();
        List<ParseObject> ol = null;
        String str = "";
        try {
            ol = query.find();
            for (ParseObject obj : ol) {
                list.add(new person(obj.getString("p"),obj.getString("n"),Integer.parseInt(obj.getString("d") + "")));
                str+=obj.getString("p")+"  "+obj.getString("n")+" "+Integer.parseInt(obj.getString("d"))+"\n";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        catch (NumberFormatException fe){
            Toast.makeText(context, "Sorry, there is some error in database. Need to clear data manually", Toast.LENGTH_LONG).show();
        }
        return str;
    }
    void savecontacts(){
        ArrayList<person> pl = new ArrayList<person>();

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

    private String getCallDetails(Context context) {

        StringBuffer sb = new StringBuffer();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return "Permission Denied";
        }
        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER );
        int type = managedCursor.getColumnIndex( CallLog.Calls.TYPE );
        int date = managedCursor.getColumnIndex( CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
        sb.append("Call Details :");

        while ( managedCursor.moveToNext() ) {
            String phNumber = managedCursor.getString( number );
            String callType = managedCursor.getString( type );
            String callDate = managedCursor.getString( date );
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString( duration );
            String dir = null;
            int dircode = Integer.parseInt( callType );
            sb.append( "\nPhone Number:--- "+phNumber +" \nCall Type:--- "+dir+" \nCall Date:--- "+callDayTime+" \nCall duration in sec :--- "+callDuration );

            Date now = new Date();
            long diff  = now.getTime() - callDayTime.getTime();

            long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diff);
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(diff);


            sb.append("\n Hours: "+diffInHours+" Minutes: "+diffInMinutes%60+"    list size-"+list.size());


            //Here
            person rem=null;
            for(person x:list){
                if(phNumber.contains(x.number)&&Integer.parseInt(callDuration)>15){
                    rem = x;
                    sb.append("\n " + x.name + " REMOVED | REMOVED | REMOVED | REMOVED | REMOVED | REMOVED \n");
                    if(diffInHours>x.hours)
                    result.add(new res(phNumber, x.name, (int) diffInHours, (int) (diffInMinutes % 60)));
                    break;
                }
            }
            if(rem!=null)
                list.remove(rem);

            sb.append("\n----------------------------------");
            if(list.isEmpty())
                break;


        }
        for(person x:list){

                sb.append("\n " + x.name + " REMOVED | REMOVED | REMOVED | REMOVED | REMOVED | REMOVED \n");
                result.add(new res(x.number, x.name, (int)99999, (int) (59 % 60)));


        }

        managedCursor.close();
        return sb.toString()+"\nEnd";
    }
}
