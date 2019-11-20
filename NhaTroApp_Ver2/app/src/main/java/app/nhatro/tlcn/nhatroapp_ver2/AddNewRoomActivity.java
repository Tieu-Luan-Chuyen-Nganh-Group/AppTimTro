package app.nhatro.tlcn.nhatroapp_ver2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class AddNewRoomActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_room);


        mToolbar = (Toolbar) findViewById(R.id.admin_addNewRoom_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add New Room");

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
