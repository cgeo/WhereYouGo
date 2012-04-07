/*
  * This file is part of WhereYouGo.
  *
  * WhereYouGo is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * WhereYouGo is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with WhereYouGo.  If not, see <http://www.gnu.org/licenses/>.
  *
  * Copyright (C) 2012 Menion <whereyougo@asamm.cz>
  */ 

package menion.android.whereyougo.utils.audio;

import menion.android.whereyougo.R;
import menion.android.whereyougo.utils.A;
import menion.android.whereyougo.utils.Logger;

public class UtilsAudio {

	private static final String TAG = "UtilsAudio";
	
	public static void playBeep(int count) {
		try {
			if (A.getApp() != null)
				new AudioClip(A.getApp(), R.raw.sound_beep_01).play(count);
			else if (A.getMain() != null)
				new AudioClip(A.getMain(), R.raw.sound_beep_01).play(count);
		} catch (Exception e) {
			Logger.e(TAG, "playBeep(" + count + ")", e);
		}
	}
}
