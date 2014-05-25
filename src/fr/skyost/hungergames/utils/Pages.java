package fr.skyost.hungergames.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import fr.skyost.hungergames.HungerGames;

public class Pages implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private final List<String> pages;
	private String bottomText;
	
	public Pages(final SortedMap<Integer, String> unpaginatedText, final int length) {
		if(unpaginatedText.size() > length) {
			pages = paginate(unpaginatedText, length);
		}
		else {
			final StringBuilder stringBuilder = new StringBuilder();
			for(final String part : unpaginatedText.values()) {
				stringBuilder.append(part);
			}
			pages = Arrays.asList(stringBuilder.toString());
		}
	}
	
	public Pages(final SortedMap<Integer, String> unpaginatedText, final int length, final String bottomText, final int bottomTextLength) {
		this.bottomText = bottomText;
		if(unpaginatedText.size() > length) {
			pages = paginate(unpaginatedText, length - bottomTextLength);
		}
		else {
			final StringBuilder stringBuilder = new StringBuilder();
			int page;
			for(Entry<Integer, String> entry : unpaginatedText.entrySet()) {
				page = entry.getKey() + 1;
				stringBuilder.append(HungerGames.messages.message16.replaceAll("/n/", String.valueOf(page)).replaceAll("/ordinal-suffix/", Utils.getOrdinalSuffix(page)).replaceAll("/player/", entry.getValue()) + "\n");
			}
			pages = Arrays.asList(stringBuilder.toString());
		}
	}
	
	public final List<String> paginate(final SortedMap<Integer, String> unpaginatedText, final int length) {
		final List<String> pages = new ArrayList<String>();
		final StringBuilder stringBuilder = new StringBuilder();
		int i = 0, k = 0, l = 1;
		while(l != (((unpaginatedText.size() % length) == 0) ? unpaginatedText.size() / length : (unpaginatedText.size() / length) + 1)) {
			for(final Entry<Integer, String> e : unpaginatedText.entrySet()) {
				k++;
				final int number = e.getKey() + 1;
				if((((l * length) + i + 1) == k) && (k != ((l * length) + length + 1))) {
					i++;
					stringBuilder.append(HungerGames.messages.message16.replaceAll("/n/", String.valueOf(number)).replaceAll("/ordinal-suffix/", Utils.getOrdinalSuffix(number)).replaceAll("/player/", e.getValue()) + "\n");
				}
			}
			pages.add(stringBuilder.toString());
			stringBuilder.setLength(0);
			i = 0;
			k = 0;
			l++;
		}
		return pages;
	}
	
	public final String getPage(final int page) {
		return pages.get(page - 1) + (bottomText == null ? "" : "\n" + bottomText.replaceAll("/n/", String.valueOf(page)).replaceAll("/total-pages/", String.valueOf(pages.size())));
	}
	
	public final int getTotalPages() {
		return pages.size();
	}
	
	public final void setBottomText(final String bottomText) {
		this.bottomText = bottomText;
	}
	
	public final void saveToFile(final File file) throws IOException {
		final ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
		objectOutputStream.writeObject(this);
		objectOutputStream.flush();
		objectOutputStream.close();
	}
	
}
