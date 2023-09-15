package com.kabouzeid.gramophone.helper.menu;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.kabouzeid.gramophone.dialogs.AddToPlaylistDialog;
import com.kabouzeid.gramophone.dialogs.DeleteSongsDialog;
import com.kabouzeid.gramophone.helper.MusicPlayerRemote;
import com.kabouzeid.gramophone.model.Song;
import com.kabouzeid.gramophone.util.MusicUtil;

import org.frknkrc44.frigraph.R;

import java.util.List;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class SongsMenuHelper {
    public static boolean handleMenuClick(@NonNull FragmentActivity activity, @NonNull List<Song> songs, int menuItemId) {
        switch (menuItemId) {
            case (R.id.action_play_next):
                MusicPlayerRemote.playNext(songs);
                return true;
            case (R.id.action_add_to_current_playing):
                MusicPlayerRemote.enqueue(songs);
                return true;
            case (R.id.action_add_to_playlist):
                AddToPlaylistDialog.create(songs).show(activity.getSupportFragmentManager(), "ADD_PLAYLIST");
                return true;
            case (R.id.action_delete_from_device):
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    DeleteSongsDialog.create(songs).show(activity.getSupportFragmentManager(), "DELETE_SONGS");
                } else {
                    MusicUtil.deleteTracks(activity, songs);
                }
                return true;
        }
        return false;
    }
}
