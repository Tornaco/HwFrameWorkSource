package android.media;

import android.media.MediaPlayer2.MediaPlayer2EventCallback;
import android.util.Pair;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaPlayer2Impl$EventHandler$a55WUDW_Ad0Vmi1x4yZhQXvPqdc implements Runnable {
    private final /* synthetic */ EventHandler f$0;
    private final /* synthetic */ Pair f$1;
    private final /* synthetic */ DataSourceDesc f$2;

    public /* synthetic */ -$$Lambda$MediaPlayer2Impl$EventHandler$a55WUDW_Ad0Vmi1x4yZhQXvPqdc(EventHandler eventHandler, Pair pair, DataSourceDesc dataSourceDesc) {
        this.f$0 = eventHandler;
        this.f$1 = pair;
        this.f$2 = dataSourceDesc;
    }

    public final void run() {
        ((MediaPlayer2EventCallback) this.f$1.second).onInfo(this.f$0.mMediaPlayer, this.f$2, 100, 0);
    }
}
