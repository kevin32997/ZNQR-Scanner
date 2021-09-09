package gov.zambonortedev.znqrscanner.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import gov.zambonortedev.znqrscanner.R;
import gov.zambonortedev.znqrscanner.models.Member;

public class ListViewQPAdapter extends ArrayAdapter<Member> {

    private List<Member> members;
    private Context context;
    private Handler mainHandler;


    public ListViewQPAdapter(@NonNull Context context, List<Member> members) {
        super(context, R.layout.listview_row_layout, R.id.item_fullname, members);
        this.members = members;
        this.context = context;
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void setList(List<Member> list) {
        members.clear();
        members.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        members.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final Member member = members.get(position);

        View row = inflater.inflate(R.layout.listview_row_layout, parent, false);

        TextView fullnameField = row.findViewById(R.id.item_fullname);
        fullnameField.setText(member.getFullname());

        TextView idField = row.findViewById(R.id.item_id);
        idField.setText(member.getId());

        TextView addressField = row.findViewById(R.id.item_address);

        if (!member.getAddress().equals("")) {
            addressField.setText(member.getAddress());
        } else {
            addressField.setText("No data Avalable.");
        }

        TextView gender = row.findViewById(R.id.item_gender);
        if (member.getGender() == 0) {
            gender.setText("MALE");
            gender.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            gender.setText("FEMALE");
            gender.setTextColor(context.getResources().getColor(R.color.colorAccent2));
        }

       // getImageOnline((ImageView) row.findViewById(R.id.item_img), 100);

        row.findViewById(R.id.item_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openViewDialog(member);
            }
        });

        // Get image online
        return row;
    }

    /* https://scontent.fmnl4-1.fna.fbcdn.net/v/t1.0-9/s960x960/78684658_2623363114444355_406769811368968192_o.jpg?_nc_cat=110&_nc_sid=85a577&_nc_ohc=KCgauCPEfrwAX_7BdNa&_nc_ht=scontent.fmnl4-1.fna&_nc_tp=7&oh=b5a308767e6eb97af10be2137bddded7&oe=5EA3B88E */

    private void getImageOnline(final ImageView imageView, final int size) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is = (InputStream) new URL("https://scontent.fmnl4-1.fna.fbcdn.net/v/t1.0-9/s960x960/78684658_2623363114444355_406769811368968192_o.jpg?_nc_cat=110&_nc_sid=85a577&_nc_ohc=KCgauCPEfrwAX_7BdNa&_nc_ht=scontent.fmnl4-1.fna&_nc_tp=7&oh=b5a308767e6eb97af10be2137bddded7&oe=5EA3B88E").getContent();
                    //final Drawable d = Drawable.createFromStream(is, "src name");
                    final Bitmap bitmap = BitmapFactory.decodeStream(is);

                    //img.setImageBitmap(Helper.decodeImageFile());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, size, size, false));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Open Dialog for View

    private void openViewDialog(Member member) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_view_member);

        // Loads Image Online
       // getImageOnline((ImageView) dialog.findViewById(R.id.view_img), 250);

        // ID
        TextView id = dialog.findViewById(R.id.view_id);
        id.setText(member.getId());

        // Firstname
        TextView fullname = dialog.findViewById(R.id.settings_server_address);
        fullname.setText(member.getFullname());

        // Bdate
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        Date birthDate = null;
        try {
            birthDate = dateFormat.parse(member.getBdate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TextView bdate = dialog.findViewById(R.id.view_bdate);
        if(birthDate!=null){
            bdate.setText(member.getBdate());
        }else{
            bdate.setText("N/A");
        }

        // Age
        TextView age = dialog.findViewById(R.id.view_age);
        if(birthDate!=null) {
            Calendar calendar = Calendar.getInstance();
            int year_now = calendar.get(Calendar.YEAR);
            calendar.setTime(birthDate);
            int year_bdate = calendar.get(Calendar.YEAR);
            age.setText(""+(year_now-year_bdate));
        }else{
            age.setText("N/A");
        }

        // Gender
        TextView gender=dialog.findViewById(R.id.view_gender);
        if (member.getGender() == 0) {
            gender.setText("MALE");
        } else {
            gender.setText("FEMALE");
        }

        // Civil Status
        TextView cStatus = dialog.findViewById(R.id.view_cstatus);
        switch (member.getCivilStatus()) {
            case "0":
                //single
                cStatus.setText("single");
                break;
            case "1":
                //married
                cStatus.setText("married");
                break;
            case "2":
                //widowed
                cStatus.setText("widowed");
                break;
            case "3":
                // live-in
                cStatus.setText("live-in");
                break;
        }

        // Address
        TextView address = dialog.findViewById(R.id.view_address);
        if (!member.getAddress().equals("")) {
            address.setText(member.getAddress());
        } else {
            address.setText("No data Avalable.");
        }

        // Button Close
        dialog.findViewById(R.id.view_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
