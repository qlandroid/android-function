package com.example.myapplication.bitmap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.R;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BitmapActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    @Bind(R.id.show_content)
    TextView showContent;
    @Bind(R.id.list_view)
    ListView listView;

    private String[] items ={"打开相册"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);
        ButterKnife.bind(this);
        initListView();
    }

    private void initListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private static final int RESULT_LOAD_IMAGE = 0;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                startAndroidImage();
                break;
            case 1:
                break;
        }
    }

    /**
     * 启动系统相册
     */
    private void startAndroidImage() {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }
    private void showImageFile(String imagePath){
        File imageFile =new File(imagePath);

        String content = "点击的图片Path="+imagePath +"点击图片的大小"+ imageFile.length() ;
        File newFile = BitmapFileOptions.scal(Uri.fromFile(imageFile));
        content = content +"-------修改后的图片路径"+newFile.getAbsolutePath()+",修改后的图片大小="+newFile.length();
        showContent.setText(content);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE){
            if (resultCode == Activity.RESULT_OK && data != null){
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                showImageFile(picturePath);

            }
        }
    }
}
