package com.example.mediaplayer.MyMediaPlayer;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

public class MyMediaPlayer extends MediaPlayer {
	// private MediaPlayer player;
	private PlayList currentPlayList;
	private int curNumber;
	private boolean isRandom;
	private Context context;

	public MyMediaPlayer(Context recvContext, PlayList startPlayList)
			throws IllegalArgumentException, SecurityException,
			IllegalStateException, IOException {
		curNumber = 0;
		isRandom = false;
		currentPlayList = startPlayList;
		this.context = recvContext;
		Log.d("Debug", curNumber + " " + currentPlayList.getSound(curNumber).toString());
		setDataSource(recvContext, currentPlayList.getSound(curNumber));
		prepare();
		// startPlay();
	}
	public int getCurrentPosInPlaylist() {
		return curNumber;
	}
	public void setIsRandom(boolean f) {
		isRandom = f;
		if (f) {
			Uri tmp = currentPlayList.getSound(curNumber);
			currentPlayList.randomShuffle();
			curNumber = currentPlayList.indexOf(tmp);
		} else {
			Uri tmp = currentPlayList.getSound(curNumber);
			currentPlayList.sort();
			curNumber = currentPlayList.indexOf(tmp);
		}
	}

	public boolean getIsRandom() {
		return isRandom;
	}

	private Uri getNext() {
		if (curNumber == currentPlayList.getSize())
			return null;
		Log.d("Debug", Integer.toString(curNumber));
		return currentPlayList.getSound(++curNumber);
	}

	private Uri getPrev() {
		if (curNumber == 0)
			return null;
		Log.d("Debug", Integer.toString(curNumber));
		return currentPlayList.getSound(--curNumber);
	}

	public void goNext() throws IllegalStateException, IOException {
		stop();
		reset();
		Uri nextFile;
		nextFile = getNext();
		if (nextFile == null)
			end();
		else {
			setDataSource(context, nextFile);
		}
		prepare();
		return;
	}

	public void goPrev() throws IllegalStateException, IOException {
		stop();
		reset();
		Uri prevFile;
		prevFile = getPrev();
		if (prevFile == null)
			end();
		else {
			setDataSource(context, prevFile);
		}
		prepare();
		return;
	}

	public void end() {
		// TODO Auto-generated method stub
		Log.d("Debug", "END!");
		//this.setNewSong(0);
	}

	public Uri getCurrentSound() {
		return currentPlayList.getSound(curNumber);
	}
	public void setSongSource(Uri uri) {
		try {
			setDataSource(context, uri);
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
	}
	public void setNewSong(int position) {
		stop();
		reset();
		curNumber = position;
		Uri f = currentPlayList.getSound(curNumber);
		setSongSource(f);
		try {
			prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopPlay() {
		if (isPlaying()) {
			this.stop();
		}
	}

	public int getPlayListSize() {
		// TODO Auto-generated method stub
		return currentPlayList.getSize();
	}

	public PlayList getPlayList() {
		// TODO Auto-generated method stub
		return currentPlayList;
	}
}
