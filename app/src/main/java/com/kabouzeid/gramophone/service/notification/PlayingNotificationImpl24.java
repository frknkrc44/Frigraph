package com.kabouzeid.gramophone.service.notification;

import static com.kabouzeid.gramophone.service.MusicService.ACTION_QUIT;
import static com.kabouzeid.gramophone.service.MusicService.ACTION_REWIND;
import static com.kabouzeid.gramophone.service.MusicService.ACTION_SKIP;
import static com.kabouzeid.gramophone.service.MusicService.ACTION_TOGGLE_PAUSE;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kabouzeid.gramophone.glide.SongGlideRequest;
import com.kabouzeid.gramophone.glide.palette.BitmapPaletteWrapper;
import com.kabouzeid.gramophone.model.Song;
import com.kabouzeid.gramophone.service.MusicService;
import com.kabouzeid.gramophone.ui.activities.MainActivity;
import com.kabouzeid.gramophone.util.PreferenceUtil;
import com.kabouzeid.gramophone.util.Util;

import org.frknkrc44.frigraph.R;

public class PlayingNotificationImpl24 extends PlayingNotification {

    @Override
    public synchronized void update() {
        stopped = false;

        final Song song = service.getCurrentSong();

        final boolean isPlaying = service.isPlaying();

        final int playButtonResId = isPlaying
                ? R.drawable.ic_pause_white_24dp : R.drawable.ic_play_arrow_white_24dp;

        Intent action = new Intent(service, MainActivity.class);
        action.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent clickIntent = PendingIntent.getActivity(service, 0, action, Util.PENDING_INTENT_FLAGS);

        final ComponentName serviceName = new ComponentName(service, MusicService.class);
        Intent intent = new Intent(MusicService.ACTION_QUIT);
        intent.setComponent(serviceName);
        final PendingIntent deleteIntent = PendingIntent.getService(service, 0, intent, Util.PENDING_INTENT_FLAGS);

        final int bigNotificationImageSize = service.getResources().getDimensionPixelSize(R.dimen.notification_big_image_size);
        service.runOnUiThread(() -> SongGlideRequest.Builder.from(Glide.with(service), song)
                .checkIgnoreMediaStore(service)
                .generatePalette(service).build()
                .into(new SimpleTarget<BitmapPaletteWrapper>(bigNotificationImageSize, bigNotificationImageSize) {
                    @Override
                    public void onResourceReady(BitmapPaletteWrapper resource, GlideAnimation<? super BitmapPaletteWrapper> glideAnimation) {
                        Palette palette = resource.getPalette();
                        update(resource.getBitmap(), palette.getVibrantColor(palette.getMutedColor(Color.TRANSPARENT)));
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        update(null, Color.TRANSPARENT);
                    }

                    void update(Bitmap bitmap, int color) {
                        if (bitmap == null)
                            bitmap = BitmapFactory.decodeResource(service.getResources(), R.drawable.default_album_art);
                        Notification.Action playPauseAction = new Notification.Action(playButtonResId,
                                service.getString(R.string.action_play_pause),
                                retrievePlaybackAction(ACTION_TOGGLE_PAUSE));
                        Notification.Action previousAction = new Notification.Action(R.drawable.ic_skip_previous_white_24dp,
                                service.getString(R.string.action_previous),
                                retrievePlaybackAction(ACTION_REWIND));
                        Notification.Action nextAction = new Notification.Action(R.drawable.ic_skip_next_white_24dp,
                                service.getString(R.string.action_next),
                                retrievePlaybackAction(ACTION_SKIP));
                        Notification.Action closeAction = new Notification.Action(R.drawable.ic_close_white_24dp,
                                service.getString(R.string.notices_close),
                                retrievePlaybackAction(ACTION_QUIT));
                        Notification.Builder builder = new Notification.Builder(service)
                                .setSmallIcon(R.drawable.ic_notification)
                                .setSubText(song.albumName)
                                .setLargeIcon(bitmap)
                                .setContentIntent(clickIntent)
                                .setDeleteIntent(deleteIntent)
                                .setContentTitle(song.title)
                                .setContentText(song.artistName)
                                .setOngoing(isPlaying)
                                .setShowWhen(false)
                                .setVisibility(Notification.VISIBILITY_PUBLIC)
                                .addAction(previousAction)
                                .addAction(playPauseAction)
                                .addAction(nextAction)
                                .addAction(closeAction);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
                        }

                        Notification.Style style = new Notification.MediaStyle()
                                .setMediaSession(service.getMediaSession().getSessionToken())
                                .setShowActionsInCompactView(0, 1, 2);
                        builder.setStyle(style);

                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O && PreferenceUtil.getInstance(service).coloredNotification())
                            builder.setColor(color);

                        if (stopped)
                            return; // notification has been stopped before loading was finished
                        updateNotifyModeAndPostNotification(builder.build());
                    }
                }));
    }

    private PendingIntent retrievePlaybackAction(final String action) {
        final ComponentName serviceName = new ComponentName(service, MusicService.class);
        Intent intent = new Intent(action);
        intent.setComponent(serviceName);
        return PendingIntent.getService(service, 0, intent, Util.PENDING_INTENT_FLAGS);
    }
}
