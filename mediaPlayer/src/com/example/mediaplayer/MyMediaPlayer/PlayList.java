package com.example.mediaplayer.MyMediaPlayer;

import java.util.ArrayList;
import java.util.Collections;

import android.net.Uri;

public class PlayList {
	private ArrayList<Uri> sounds;
	private String name;
	public PlayList(ArrayList<Uri> Sounds, String Name) {
		sounds = Sounds;
		name = Name;
		sort();
	}
	public Uri getSound(int index) {
		return sounds.get(index);
	}
	public void randomShuffle() {
		Collections.shuffle(sounds);		
	}
	public int indexOf(Uri f) {
		return sounds.indexOf(f);
	}
	public void sort() {
		Collections.sort(sounds);
		
	}
	public int getSize() {
		// TODO Auto-generated method stub
		return sounds.size();
	}
	public String getName() {
		return name;
	}
}
