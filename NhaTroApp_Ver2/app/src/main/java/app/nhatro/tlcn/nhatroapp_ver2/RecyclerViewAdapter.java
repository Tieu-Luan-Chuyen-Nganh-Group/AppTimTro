package app.nhatro.tlcn.nhatroapp_ver2;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {


    private ArrayList<Post> postResultlist = new ArrayList<Post>();
    private Context mContext;
    private ResultSearchRoomActivity resultSearchRoomActivity = new ResultSearchRoomActivity();


    public RecyclerViewAdapter(Context mContext, ArrayList<Post> postResultlist) {
        this.postResultlist = postResultlist;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_result, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Picasso.with(mContext).load(postResultlist.get(position).profileimage).into(holder.post_profile_image);
        holder.username.setText(postResultlist.get(position).fullname);
        holder.date.setText(postResultlist.get(position).date);
        holder.time.setText(postResultlist.get(position).time);
        holder.description.setText(postResultlist.get(position).description);
        Picasso.with(mContext).load(postResultlist.get(position).postimage).into(holder.img);
        holder.phoneImageButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(postResultlist.get(position).numberphone)) {
                    String dial = "tel:" + postResultlist.get(position).numberphone;
                    mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return postResultlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageButton phoneImageButon, optionsImageButton;
        CircleImageView post_profile_image;
        TextView username, date, time, description;
        RelativeLayout parent_result_post;
        ImageView img;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            phoneImageButon = itemView.findViewById(R.id.post_result_phone_imageButton);
            //optionsImageButton = itemView.findViewById(R.id.post_result_selectOptions_imageButton);
            post_profile_image = itemView.findViewById(R.id.post_result_profile_image);
            username = itemView.findViewById(R.id.post_result_username);
            date = itemView.findViewById(R.id.post_result_date);
            time = itemView.findViewById(R.id.post_result_time);
            description = itemView.findViewById(R.id.post_result_description);
            parent_result_post = itemView.findViewById(R.id.parent_layout_result);
            img = itemView.findViewById(R.id.post_result_image);
        }
    }

}
