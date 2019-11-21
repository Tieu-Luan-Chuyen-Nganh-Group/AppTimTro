package app.nhatro.tlcn.nhatroapp_ver2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddNewRoomActivity extends AppCompatActivity {

    private static final int GALLERY_PICK = 1 ;
    private static final int LimitNumerImg = 4;
    private Toolbar mToolbar;
    private Button addNewRoomBtn;
    private EditText editTextAddress, editTextPrice, editTextPhone, editTextDescription;
    private ImageView img1, img2, img3, img4;
    private ImageButton addImageRoom;
    private int ImgCount, currentImgSelect;
    private Uri imgUri;
    private List<Uri>  ImageList = new ArrayList<>();
    String saveCurrentDate, saveCurrentTime, RoomRandomName;

    private StorageReference RoomImagesRef;
    private DatabaseReference RoomRef, UsersRef;
    private FirebaseAuth mAuth;
    private List<Bitmap> bitmaps = new ArrayList<>();
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_room);

        mToolbar = (Toolbar) findViewById(R.id.admin_addNewRoom_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add New Room");

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid().toString();

        RoomImagesRef = FirebaseStorage.getInstance().getReference();
        RoomRef = FirebaseDatabase.getInstance().getReference().child("Rooms");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        addNewRoomBtn = (Button) findViewById(R.id.button_Addroom_Add);
        addImageRoom = (ImageButton) findViewById(R.id.addNewRoom_addImg);
        editTextAddress = (EditText) findViewById(R.id.editText_AddRoom_Address);
        editTextPrice = (EditText) findViewById(R.id.editText_AddRoom_Price);
        editTextPhone = (EditText) findViewById(R.id.editText_AddRoom_Phone);
        editTextDescription = (EditText) findViewById(R.id.editText_AddRoom_Description);

        img1 = (ImageView) findViewById(R.id.AddRoom_img_1);
        img2 = (ImageView) findViewById(R.id.AddRoom_img_2);
        img3 = (ImageView) findViewById(R.id.AddRoom_img_3);
        img4 = (ImageView) findViewById(R.id.AddRoom_img_4);

        addImageRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageList.removeAll(ImageList);
                bitmaps.removeAll(bitmaps);
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, GALLERY_PICK);
            }
        });

        addNewRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveInfoNewRoomToFirebase();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data!=null){
            if (data.getClipData() != null){
                ImgCount = data.getClipData().getItemCount();
                currentImgSelect=0;

                while (currentImgSelect < ImgCount){
                    imgUri = data.getClipData().getItemAt(currentImgSelect).getUri();
                    ImageList.add(imgUri);
                    currentImgSelect++;

                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imgUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        bitmaps.add(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }

                if (currentImgSelect <LimitNumerImg || currentImgSelect > LimitNumerImg ){
                    ImageList.removeAll(ImageList);
                    bitmaps.removeAll(bitmaps);
                    Toast.makeText(AddNewRoomActivity.this, "You must chose " + LimitNumerImg + " image", Toast.LENGTH_SHORT).show();
                }
                else {
                    img1.setImageBitmap(bitmaps.get(0));
                    img2.setImageBitmap(bitmaps.get(1));
                    img3.setImageBitmap(bitmaps.get(2));
                    img4.setImageBitmap(bitmaps.get(3));
                }
            }
            else {
                Toast.makeText(AddNewRoomActivity.this, "You must chose " + LimitNumerImg + " image", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void SaveInfoNewRoomToFirebase() {
        String address = editTextAddress.getText().toString();
        String price = editTextPrice.getText().toString();
        String phone = editTextPhone.getText().toString();
        String description = editTextDescription.getText().toString();

        if (TextUtils.isEmpty(address)){
            Toast.makeText(AddNewRoomActivity.this, "Please write address!", Toast.LENGTH_SHORT).show();
        }
        else{
            if (TextUtils.isEmpty(price)){
                Toast.makeText(AddNewRoomActivity.this, "Please write price!", Toast.LENGTH_SHORT).show();
            }
            else{
                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(AddNewRoomActivity.this, "Please write phone!", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (TextUtils.isEmpty(description)){
                        Toast.makeText(AddNewRoomActivity.this, "Please write description!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (currentImgSelect <LimitNumerImg || currentImgSelect > LimitNumerImg ){
                            ImageList.removeAll(ImageList);
                            bitmaps.removeAll(bitmaps);
                            Toast.makeText(AddNewRoomActivity.this, "You must chose " + LimitNumerImg + " image", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            HashMap hashMap = new HashMap();
                            hashMap.put("Address", address);
                            hashMap.put("Price", price);
                            hashMap.put("Phone", phone);
                            hashMap.put("Description", description);

                            StorageReference fileFolder = RoomImagesRef.child("Room images");
                            RandomID();

                            RoomRef.child(RoomRandomName).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(AddNewRoomActivity.this, "Add new room successfully!", Toast.LENGTH_LONG).show();
                                        SendUserToRoomListActivity();
                                    }
                                    else {
                                        Toast.makeText(AddNewRoomActivity.this, "error occur:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            for (int i=0; i< currentImgSelect; i++){
                                Uri filePath = ImageList.get(i);
                                StorageReference imageName = fileFolder.child("Img" + filePath.getLastPathSegment() + RoomRandomName);

                                imageName.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        final Task <Uri> downloadTask = task.getResult().getMetadata().getReference().getDownloadUrl();
                                        downloadTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String downloadUri = uri.toString();
                                                RoomRef.child(RoomRandomName).child("imageList").push().setValue(downloadUri);
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }
    }

    private void RandomID(){
        // lấy ngày hiện tại
        Calendar calendarForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate = currentDate.format(calendarForDate.getTime());

        // lấy giờ hiện tại
        Calendar calendarForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calendarForDate.getTime());

        //ghép ngày và giờ thành 1 chuỗi để đặt tên cho hình ảnh khi lưu lên Firebase
        RoomRandomName = saveCurrentDate + saveCurrentTime;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home){
            SendUserToRoomListActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToRoomListActivity() {
        Intent roomListIntent = new Intent(AddNewRoomActivity.this, RoomListActivity.class);
        startActivity(roomListIntent);
        finish();
    }
}
