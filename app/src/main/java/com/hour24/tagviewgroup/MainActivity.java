package com.hour24.tagviewgroup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TagView mTagGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTagGroup = (TagView) findViewById(R.id.tag_group);
        mTagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onClick(int position, ModelTag tag) {
                Toast.makeText(MainActivity.this, tag.text, Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<ModelTag> tags = new ArrayList<>();

        tags.add(new ModelTag("초특가"));
        tags.add(new ModelTag("체크인나우"));
        tags.add(new ModelTag("엄청나게 길게 써봅시다"));
        tags.add(new ModelTag("하이후에호호호"));
        tags.add(new ModelTag("여름"));
        tags.add(new ModelTag("봄"));
        tags.add(new ModelTag("하하하하하하"));
        tags.add(new ModelTag("하이후에호호호"));

        mTagGroup.addTags(tags);
    }
}
