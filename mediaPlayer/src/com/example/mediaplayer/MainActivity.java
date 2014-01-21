package com.example.mediaplayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import com.example.mediaplayer.MyMediaPlayer.MyMediaPlayer;
import com.example.mediaplayer.MyMediaPlayer.PlayList;

import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {

	final String TAG = "States";
	final String TAGF = "Files";
	final String TAGD = "Directory";

	Button btnPrev;
	Button btnPP;
	Button btnNext;
	CheckBox random;

	MyMediaPlayer player;

	Timer timerChangeProgress;
	TextView fileName;
	EditText timeToSleep;
	Button sleep;

	boolean isGoToSleep;

	SeekBar progress;
	TextView duration;
	TextView time;

	ImageView image;
	MyMainClickListener MyMainClickListener = new MyMainClickListener();
	final String ATTRIBUTE_NAME_CHECKED = "checked";
	final String ATTRIBUTE_NAME_IMAGE = "image";
	AudioManager am = null;
	OnAudioFocusChangeListener afChangeListener = null;
	@TargetApi(9)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "MainActivity: onCreate()");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initializeActivity();
		ArrayList<Uri> sounds = getFileList();
		initializePlayer(sounds);
		loadSettings();
		startPlayNewSong();
		//testFindAudio();
	}
	private void loadSettings() {
		// Restore preferences
	    SharedPreferences settings = getPreferences(0);
	    int positionOnPlaylist = settings.getInt("position", 0);
	    player.setNewSong(positionOnPlaylist);
	}
	private void saveSettings() {
		SharedPreferences settings = getPreferences(0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.clear();
	    editor.putInt("position", player.getCurrentPosInPlaylist());

	      // Commit the edits!
	    editor.commit();
	}
	public void testFindAudio() {
		ContentResolver contentResolver = getContentResolver();
		Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor cursor = contentResolver.query(uri, null, null, null, null);
		if (cursor == null) {
		    // query failed, handle error.
		} else if (!cursor.moveToFirst()) {
		    // no media on the device
		} else {
		    int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
		    int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
		    do {
		       long thisId = cursor.getLong(idColumn);
		       String thisTitle = cursor.getString(titleColumn);
		       Uri contentUri = ContentUris.withAppendedId(
		               android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId);
		       Log.d("Testt", thisTitle);
		       System.out.println(thisTitle);
		       player.stop();
		       player.reset();
		       player.setSongSource(contentUri);
		       
		       try {
					player.prepare();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		       startPlayNewSong();
		       
		       return;
		       // ...process entry...
		    } while (cursor.moveToNext());
		}
	}

	private void initializePlayer(ArrayList<Uri> sounds) {
		player = null;
		try {
			player = new MyMediaPlayer(MainActivity.this, new PlayList(sounds, "Default PlayList"));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		player.setOnCompletionListener(OCL);
		timerChangeProgress = null;

	}

	private ArrayList<Uri> getFileList() {
		ArrayList<Uri> resultSounds = new ArrayList<Uri>();
		ContentResolver contentResolver = getContentResolver();
		Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor cursor = contentResolver.query(uri, null, null, null, null);
		if (cursor == null) {
		    // query failed, handle error.
		} else if (!cursor.moveToFirst()) {
		    // no media on the device
		} else {
		    int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
		    do {
		       long thisId = cursor.getLong(idColumn);
		       Uri contentUri = ContentUris.withAppendedId(
		               android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId);
		       resultSounds.add(contentUri);
		       // ...process entry...
		    } while (cursor.moveToNext());
		}
		return resultSounds;
	}

	private void initializeActivity() {

		btnPrev = (Button) findViewById(R.id.btnPrev);
		btnPP = (Button) findViewById(R.id.btnPP);
		btnNext = (Button) findViewById(R.id.btnNext);
		btnPrev.setOnClickListener(MyMainClickListener);
		btnPP.setOnClickListener(MyMainClickListener);
		btnNext.setOnClickListener(MyMainClickListener);

		sleep = (Button) findViewById(R.id.sleep);
		sleep.setOnClickListener(MyMainClickListener);
		timeToSleep = (EditText) findViewById(R.id.timeToSleep);
		isGoToSleep = false;

		fileName = (TextView) findViewById(R.id.fileName);
		random = (CheckBox) findViewById(R.id.random);
		random.setOnClickListener(MyMainClickListener);

		duration = (TextView) findViewById(R.id.duration);
		time = (TextView) findViewById(R.id.time);
		progress = (SeekBar) findViewById(R.id.progress);
		progress.setOnSeekBarChangeListener(OSBCL);
		findViewById(R.id.goSoundList).setOnClickListener(MyMainClickListener);

		image = (ImageView) findViewById(R.id.image);
		am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		afChangeListener = new OnAudioFocusChangeListener() {
		    public void onAudioFocusChange(int focusChange) {
		        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
		            // Pause playback
		        	player.pause();
					timerChangeProgress.cancel();
		        	Log.d("Debug", "AUDIOFOCUS_LOSS_TRANSIENT");
		        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
		        	Log.d("Debug", "AudioManager.AUDIOFOCUS_GAIN");
		        	player.start();
					timerChangeProgress = new Timer();
					TimerTask task = new changeProgress();
					timerChangeProgress.schedule(task, 0, 1000);
		        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
		            //am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
		        	Log.d("Debug", "AudioManager.AUDIOFOCUS_LOSS");
		            am.abandonAudioFocus(afChangeListener);
					player.pause();
					timerChangeProgress.cancel();
					
		            // Stop playback
		        }
		    }
		};
		int result = am.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);

		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
		//am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
		// Start playback.
			try {
				PRFLP.init("192.168.1.45-testAppn", "apiKey");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			PRFLP.begin("mongoDB.save");	
			
			//start timer
			
			Log.d("Debug", "Start playback");
		}

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "MainActivity: onRestart()");
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		Log.d("Debug", "MainActivity: onStart()");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "MainActivity: onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "MainActivity: onPause()");
	}

	@Override
	protected void onStop() {
		super.onStop();
		saveSettings();
		Log.d(TAG, "MainActivity: onStop()");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (player.isPlaying())
			player.stop();
		Log.d(TAG, "MainActivity: onDestroy()");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		int position = data.getIntExtra("position", -1);
		
		player.setNewSong(position);
		startPlayNewSong();
		
	}

	private void startPlayNewSong() {
		if (timerChangeProgress != null)
			timerChangeProgress.cancel();
		duration.setText(getTimeString(player.getDuration()));
		time.setText(getTimeString(0));
		player.start();
		startTimer(1000);
		setCover(player.getCurrentSound());
		try {
			setTags();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadOnlyFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAudioFrameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub

	}
	private String getRealPathFromURI(Uri contentUri) {
	    String[] proj = { MediaStore.Images.Media.DATA };
	    CursorLoader loader = new CursorLoader(MainActivity.this, contentUri, proj, null, null, null);
	    Cursor cursor = loader.loadInBackground();
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	private void setCover(Uri curSound) {
		Log.d("Testt", curSound.toString());
		Log.d("Testt", curSound.getPath());
		Log.d("Testt", curSound.getSchemeSpecificPart());
		File directory = new File(getRealPathFromURI(curSound)).getParentFile();
		String[] names = { "cover.jpg", "Cover.jpg" };
		File f = null;
		File tempImage = null;
		Log.d("Debug", directory.getAbsolutePath());
		for (int i = 0; i < names.length; i++)
			if ((f = new File(directory.getAbsolutePath() + "/" + names[i]))
					.exists())
				tempImage = f;
		if(tempImage == null) {
			File[] imagesList = directory.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String filename) {
					// TODO Auto-generated method stub
					return filename.endsWith(".jpg") || filename.endsWith(".png");
				}
			});
			if(imagesList.length > 0)
				tempImage = imagesList[0];
		}
		if (tempImage != null) {
			image.setImageURI(Uri.fromFile(tempImage));
			image.setAdjustViewBounds(true);
			image.setMaxWidth(250);
			image.setMaxHeight(250);
		} else {
			image.setImageResource(android.R.color.transparent);
		}
	}

	private void setTags() throws FileNotFoundException, IOException,
			CannotReadException, TagException, ReadOnlyFileException,
			InvalidAudioFrameException {
		File f = new File(getRealPathFromURI(player.getCurrentSound()));
		AudioFile file2 = AudioFileIO.read(f);
		Tag tag2 = file2.getTag();
		if (tag2 == null) {
			fileName.setText(f.getName());
			return;
		}
		else {
			if ((tag2.getFirst(FieldKey.ARTIST) == null && tag2.getFirst(FieldKey.TITLE) == null) 
					|| (tag2.getFirst(FieldKey.ARTIST) == "" && tag2.getFirst(FieldKey.TITLE) == "")) {
				fileName.setText(f.getName());
				return;
			}
		}
		// AudioHeader = file2.getAudioHeader();
		Log.d("Titles", "Genre: " + tag2.getFirst(FieldKey.GENRE));// TOD
		Log.d("Titles", "Artist: " + tag2.getFirst(FieldKey.ARTIST));
		Log.d("Titles", "Album: " + tag2.getFirst(FieldKey.ALBUM));
		Log.d("Titles", "Year: " + tag2.getFirst(FieldKey.YEAR));
		Log.d("Titles", "Comment: " + tag2.getFirst(FieldKey.COMMENT));
		Log.d("Titles", "Title: " + tag2.getFirst(FieldKey.TITLE));
		Log.d("Titles", "Track: " + tag2.getFirst(FieldKey.TRACK));
		fileName.setText(tag2.getFirst(FieldKey.ARTIST) + " - "
				+ tag2.getFirst(FieldKey.TITLE));
		/*
		 * tag2.getFirst(FieldKey.ARTIST); tag2.getFirst(FieldKey.ALBUM);
		 * tag2.getFirst(FieldKey.TITLE); tag2.getFirst(FieldKey.COMMENT);
		 * tag2.getFirst(FieldKey.YEAR); tag2.getFirst(FieldKey.TRACK);
		 * tag2.getFirst(FieldKey.DISC_NO); tag2.getFirst(FieldKey.COMPOSER);
		 * tag2.getFirst(FieldKey.ARTIST_SORT);
		 */
	}

	public void next() {
		timerChangeProgress.cancel();
		try {
			player.goNext();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			exit();
		}
	}

	public void prev() {
		timerChangeProgress.cancel();
		try {
			player.goPrev();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			exit();
		}
	}

	private void exit() {
		// TODO Auto-generated method stub
		player.end();
	}

	public class exitPlayer extends TimerTask {
		public void run() {
			player.start();
			System.exit(RESULT_OK);
			// RAND.
		}
	}

	public class changeProgress extends TimerTask {
		public void run() {
			runOnUiThread(new Runnable() {
				public void run() {
					if (player.isPlaying()) {
						progress.incrementProgressBy(1);
						time.setText(getTimeString(progress.getProgress() * 1000));
					}
					// stuff that updates ui

				}
			});

			// RAND.
		}

	}

	private void startTimer(int time) {
		progress.setMax((int) Math.ceil(player.getDuration() / 1000));
		progress.setProgress(0);
		timerChangeProgress = new Timer();
		TimerTask task = new changeProgress();
		timerChangeProgress.schedule(task, 0, time);
	}

	private String getTimeString(int time) {
		String str = null;
		time = (int) Math.ceil(time / 1000);
		int h = time / 3600;
		int m = time / 60;
		int s = time % 60;
		if (m > 9 && s > 9)
			str = h + ":" + m + ":" + s;
		else if (s > 9)
			str = h + ":0" + m + ":" + s;
		else
			str = h + ":0" + m + ":0" + s;
		return str;
	}

	OnCompletionListener OCL = new OnCompletionListener() {

		public void onCompletion(MediaPlayer arg0) {
			next();
			startPlayNewSong();
		}

	};
	OnSeekBarChangeListener OSBCL = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if (fromUser) {
				Log.d("Debug", Integer.toString(progress * 1000));
				player.seekTo(progress * 1000);
			}
		}
	};

	public class MyMainClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Log.d("Debug", Integer.toString(v.getId()));
			switch (v.getId()) {
			case R.id.sleep: {
				Integer time = null;
				try {
					time = Integer.parseInt(timeToSleep.getText().toString());
				} catch (NumberFormatException e) {
					return;
				}
				isGoToSleep = true;
				Timer timer = new Timer();
				TimerTask task = new exitPlayer();
				Log.d("time", Integer.toString(time));
				timer.schedule(task, time * 1000);
				if (!player.isPlaying())
					player.start();
				// Next();
			}
			case R.id.random: {
				player.setIsRandom(!player.getIsRandom());
				break;
			}
			case R.id.btnPP: {
				if (player.isPlaying()) {
					player.pause();
					timerChangeProgress.cancel();
				} else {
					player.start();
					timerChangeProgress = new Timer();
					TimerTask task = new changeProgress();
					timerChangeProgress.schedule(task, 0, 1000);
				}
				break;
			}
			case R.id.btnNext: {
				next();
				startPlayNewSong();
				break;
			}
			case R.id.btnPrev: {
				prev();
				startPlayNewSong();
				break;
			}
			case R.id.goSoundList: {
				PlayList pl = player.getPlayList();
				Intent intent = new Intent(MainActivity.this, sound_list.class);
				String[] names = new String[pl.getSize()];
				for (int i = 0; i < pl.getSize(); i++)
					names[i] = new File(getRealPathFromURI(pl.getSound(i))).getName();
				intent.putExtra("soundNames", names);
				startActivityForResult(intent, 0);
				break;
			}
			}

		}

	};
}