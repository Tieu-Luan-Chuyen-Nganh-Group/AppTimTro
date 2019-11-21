package app.nhatro.tlcn.nhatroapp_ver2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class RoomListActivity extends AppCompatActivity {

    private ImageButton addNewroomBtn;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        mToolbar = (Toolbar) findViewById(R.id.admin_RoomList_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Room List");


        addNewroomBtn = (ImageButton) findViewById(R.id.imgBtn_add_new_room);

        addNewroomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToAddNewRoomActivity();
            }
        });
    }

    private void SendUserToAddNewRoomActivity() {
        Intent addNerRoomActivity = new Intent(RoomListActivity.this, AddNewRoomActivity.class);
        addNerRoomActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(addNerRoomActivity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            SendUserToAdminActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToAdminActivity() {
        Intent adminIntent = new Intent(RoomListActivity.this, AdminActivity.class);
        startActivity(adminIntent);
        finish();
    }
}
