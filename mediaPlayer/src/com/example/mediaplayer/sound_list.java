package com.example.mediaplayer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;

import com.example.mediaplayer.MainActivity.changeProgress;
import com.example.mediaplayer.MainActivity.exitPlayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class sound_list extends Activity implements OnItemClickListener {
	public String[] sounds;
	final String ATTRIBUTE_NAME_TEXT = "text";
	ListView lvSimple;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sound_list);
		initialize();
		sounds = getIntent().getStringArrayExtra("soundNames");
		ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
				sounds.length);
		Map<String, Object> m;
		for (int i = 0; i < sounds.length; i++) {
			m = new HashMap<String, Object>();
			m.put(ATTRIBUTE_NAME_TEXT, sounds[i]);
			data.add(m);
		}
		String[] from = { ATTRIBUTE_NAME_TEXT };
		// массив ID View-компонентов, в которые будут вставлять данные
		int[] to = { R.id.name };
		SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.item,
				from, to);
		lvSimple = (ListView) findViewById(R.id.lvSimple);
		lvSimple.setAdapter(sAdapter);
	}

	private void initialize() {
		// TODO Auto-generated method stub
		ListView lvSimple = (ListView) findViewById(R.id.lvSimple);
		lvSimple.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Log.d("Test", "position: " + Integer.toString(position) + "\n Sounds: "
				+ sounds[position]);// TOD
		Intent intent = new Intent();
		intent.putExtra("position", position);
		setResult(RESULT_OK, intent);
		finish();
	}

}
