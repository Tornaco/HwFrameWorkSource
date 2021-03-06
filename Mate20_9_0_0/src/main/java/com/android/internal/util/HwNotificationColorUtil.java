package com.android.internal.util;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.graphics.drawable.VectorDrawable;
import android.util.Log;
import android.util.Pair;
import java.util.WeakHashMap;

public class HwNotificationColorUtil {
    private static final int AVERAGE_BORDER_BETWEEN_SOLID_WHITE_AND_OTHER = 244;
    private static final int INVALID_VARIANCE_VALUE = 0;
    public static final int SMALLICON_ASSORTED_COLOR = 1;
    public static final int SMALLICON_NONE = 0;
    public static final int SMALLICON_SOLID_COLOR = 4;
    public static final int SMALLICON_SOLID_WHITE = 2;
    private static final String TAG = "HwNotificationColorUtil";
    private static final int VALID_ALPHA_VALUE = 40;
    private static final int VALID_PIXEL_NUM = 20;
    private static final int VARIANCE_BORDER_BETWEEN_SOLID_AND_ASSORTED = 25;
    private static HwNotificationColorUtil sInstance;
    private static final Object sLock = new Object();
    private final WeakHashMap<Bitmap, Pair<Integer, Integer>> mHWGrayscaleBitmapCache = new WeakHashMap();
    private final WeakHashMap<Integer, Integer> mHWGrayscaleDrawableCache = new WeakHashMap();

    public static HwNotificationColorUtil getInstance(Context context) {
        HwNotificationColorUtil hwNotificationColorUtil;
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new HwNotificationColorUtil();
            }
            hwNotificationColorUtil = sInstance;
        }
        return hwNotificationColorUtil;
    }

    public int getSmallIconColorType(Context context, Icon icon) {
        if (icon == null) {
            return 0;
        }
        switch (icon.getType()) {
            case 1:
                return getSmallIconColorType(icon.getBitmap());
            case 2:
                return getSmallIconColorType(context, icon.getResId());
            default:
                return 0;
        }
    }

    /* JADX WARNING: Missing block: B:14:0x002a, code skipped:
            r1 = r10.getGenerationId();
            r2 = compressPicture(r10);
            r3 = getBitmapAvgColor(r2);
     */
    /* JADX WARNING: Missing block: B:15:0x003c, code skipped:
            if (android.graphics.Color.red(r3) < 244) goto L_0x0064;
     */
    /* JADX WARNING: Missing block: B:17:0x0042, code skipped:
            if (android.graphics.Color.green(r3) < 244) goto L_0x0064;
     */
    /* JADX WARNING: Missing block: B:19:0x0048, code skipped:
            if (android.graphics.Color.blue(r3) < 244) goto L_0x0064;
     */
    /* JADX WARNING: Missing block: B:20:0x004a, code skipped:
            r0 = sLock;
     */
    /* JADX WARNING: Missing block: B:21:0x004c, code skipped:
            monitor-enter(r0);
     */
    /* JADX WARNING: Missing block: B:23:?, code skipped:
            r9.mHWGrayscaleBitmapCache.put(r10, android.util.Pair.create(java.lang.Integer.valueOf(2), java.lang.Integer.valueOf(r1)));
     */
    /* JADX WARNING: Missing block: B:24:0x005f, code skipped:
            monitor-exit(r0);
     */
    /* JADX WARNING: Missing block: B:25:0x0060, code skipped:
            return 2;
     */
    /* JADX WARNING: Missing block: B:30:0x006c, code skipped:
            if (getColorVariance(r2, r3) >= 25.0f) goto L_0x0088;
     */
    /* JADX WARNING: Missing block: B:31:0x006e, code skipped:
            r0 = sLock;
     */
    /* JADX WARNING: Missing block: B:32:0x0070, code skipped:
            monitor-enter(r0);
     */
    /* JADX WARNING: Missing block: B:34:?, code skipped:
            r9.mHWGrayscaleBitmapCache.put(r10, android.util.Pair.create(java.lang.Integer.valueOf(4), java.lang.Integer.valueOf(r1)));
     */
    /* JADX WARNING: Missing block: B:35:0x0083, code skipped:
            monitor-exit(r0);
     */
    /* JADX WARNING: Missing block: B:36:0x0084, code skipped:
            return 4;
     */
    /* JADX WARNING: Missing block: B:40:0x0088, code skipped:
            r5 = sLock;
     */
    /* JADX WARNING: Missing block: B:41:0x008a, code skipped:
            monitor-enter(r5);
     */
    /* JADX WARNING: Missing block: B:43:?, code skipped:
            r9.mHWGrayscaleBitmapCache.put(r10, android.util.Pair.create(java.lang.Integer.valueOf(1), java.lang.Integer.valueOf(r1)));
     */
    /* JADX WARNING: Missing block: B:44:0x009d, code skipped:
            monitor-exit(r5);
     */
    /* JADX WARNING: Missing block: B:45:0x009e, code skipped:
            return 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getSmallIconColorType(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        synchronized (sLock) {
            Pair<Integer, Integer> cached = (Pair) this.mHWGrayscaleBitmapCache.get(bitmap);
            if (cached == null || ((Integer) cached.second).intValue() != bitmap.getGenerationId()) {
            } else {
                int intValue = ((Integer) cached.first).intValue();
                return intValue;
            }
        }
    }

    private int getSmallIconColorType(Context context, int drawableResId) {
        if (drawableResId <= 0) {
            return 0;
        }
        try {
            synchronized (sLock) {
                Integer cached = (Integer) this.mHWGrayscaleDrawableCache.get(Integer.valueOf(drawableResId));
                if (cached != null) {
                    int intValue = cached.intValue();
                    return intValue;
                }
                int result = getSmallIconColorType(context.getDrawable(drawableResId));
                synchronized (sLock) {
                    this.mHWGrayscaleDrawableCache.put(Integer.valueOf(drawableResId), Integer.valueOf(result));
                }
                return result;
            }
        } catch (NotFoundException e) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Drawable not found: ");
            stringBuilder.append(drawableResId);
            Log.e(str, stringBuilder.toString());
            return 0;
        }
    }

    private int getSmallIconColorType(Drawable d) {
        if (d == null) {
            return 0;
        }
        if (d instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) d;
            if (bd.getBitmap() == null) {
                return 0;
            }
            return getSmallIconColorType(bd.getBitmap());
        } else if (d instanceof VectorDrawable) {
            return getSmallIconColorType(drawableToBitamp(d));
        } else {
            if (d instanceof AnimationDrawable) {
                AnimationDrawable ad = (AnimationDrawable) d;
                if (ad.getNumberOfFrames() <= 0) {
                    return 0;
                }
                return getSmallIconColorType(ad.getFrame(0));
            } else if ((d instanceof LevelListDrawable) || (d instanceof LayerDrawable)) {
                return getSmallIconColorType(drawableToBitamp(d));
            } else {
                return 0;
            }
        }
    }

    private Bitmap compressPicture(Bitmap bmp) {
        int hr = bmp.getHeight();
        int wr = bmp.getWidth();
        if (wr <= 64 && hr <= 64) {
            return bmp;
        }
        Bitmap compactBitmap = Bitmap.createBitmap(64, 64, Config.ARGB_8888);
        Canvas compactBitmapCanvas = new Canvas(compactBitmap);
        Paint compactBitmapPaint = new Paint(1);
        compactBitmapPaint.setFilterBitmap(true);
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setScale(64.0f / ((float) wr), 64.0f / ((float) hr), 0.0f, 0.0f);
        compactBitmapCanvas.drawColor(0, Mode.SRC);
        compactBitmapCanvas.drawBitmap(bmp, matrix, compactBitmapPaint);
        return compactBitmap;
    }

    private static Bitmap drawableToBitamp(Drawable drawable) {
        Config config;
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        if (drawable.getOpacity() != -1) {
            config = Config.ARGB_8888;
        } else {
            config = Config.RGB_565;
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    private int getBitmapAvgColor(Bitmap bmp) {
        int hr = bmp.getHeight();
        int wr = bmp.getWidth();
        int[] mBufPixel = new int[(wr * hr)];
        bmp.getPixels(mBufPixel, 0, wr, 0, 0, wr, hr);
        int sumb = 0;
        int sumg = 0;
        int sumr = 0;
        int num = 0;
        int y = 0;
        while (y < hr) {
            int sumb2 = sumb;
            sumb = sumg;
            sumg = sumr;
            sumr = num;
            for (num = 0; num < wr; num++) {
                int a = Color.alpha(mBufPixel[(y * wr) + num]);
                int r = Color.red(mBufPixel[(y * wr) + num]);
                int g = Color.green(mBufPixel[(y * wr) + num]);
                int b = Color.blue(mBufPixel[(y * wr) + num]);
                if (a > 40) {
                    sumg += r;
                    sumb += g;
                    sumb2 += b;
                    sumr++;
                }
            }
            y++;
            num = sumr;
            sumr = sumg;
            sumg = sumb;
            sumb = sumb2;
        }
        if (num <= 20) {
            return -1;
        }
        return (((sumr / num) << 16) | ((sumg / num) << 8)) | (sumb / num);
    }

    private float getColorVariance(Bitmap bmp, int avg) {
        int hr = bmp.getHeight();
        int wr = bmp.getWidth();
        int[] mBufPixel = new int[(wr * hr)];
        bmp.getPixels(mBufPixel, 0, wr, 0, 0, wr, hr);
        int avgr = Color.red(avg);
        int avgg = Color.green(avg);
        int avgb = Color.blue(avg);
        float v = 0.0f;
        int num = 0;
        int y = 0;
        while (y < hr) {
            float v2 = v;
            int num2 = num;
            for (num = 0; num < wr; num++) {
                if (Color.alpha(mBufPixel[(y * wr) + num]) > 40) {
                    int r = Color.red(mBufPixel[(y * wr) + num]);
                    int g = Color.green(mBufPixel[(y * wr) + num]);
                    int b = Color.blue(mBufPixel[(y * wr) + num]);
                    v2 += (float) ((((r - avgr) * (r - avgr)) + ((g - avgg) * (g - avgg))) + ((b - avgb) * (b - avgb)));
                    num2++;
                }
            }
            y++;
            num = num2;
            v = v2;
        }
        if (num <= 20) {
            return 0.0f;
        }
        return v / ((float) num);
    }
}
