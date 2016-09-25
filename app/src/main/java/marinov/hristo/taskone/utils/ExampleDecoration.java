package marinov.hristo.taskone.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import marinov.hristo.taskone.R;

/**
 * @author HristoMarinov (christo_marinov@abv.bg).
 */
public class ExampleDecoration extends RecyclerView.ItemDecoration {

    private float scale;
    private Bitmap bitmap;
    private Context mContext;
    private Paint backgroundPaint, explicitPaint;
    private final static int offsetLeft = 35;

    public ExampleDecoration(Context context) {
        this.mContext = context;

        // For the calculations for the sizes. Depends from the screen
        scale = mContext.getResources().getDisplayMetrics().density;

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        explicitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        backgroundPaint.setColor(ContextCompat.getColor(mContext, R.color.spotify_black_dark));
        explicitPaint.setColor(ContextCompat.getColor(mContext, R.color.gray_light));

        backgroundPaint.setStyle(Paint.Style.FILL);
        explicitPaint.setStyle(Paint.Style.FILL);

        bitmap = BitmapFactory.decodeResource(
                mContext.getResources(),
                R.mipmap.ic_download);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        for (int i = 0; i < parent.getChildCount(); i++) {
            final View child = parent.getChildAt(i);
            int childPosition = parent.getChildAdapterPosition(child);

            // Draw background
            c.drawRect(
                    layoutManager.getDecoratedLeft(child),
                    layoutManager.getDecoratedTop(child),
                    layoutManager.getDecoratedRight(child),
                    layoutManager.getDecoratedBottom(child),
                    backgroundPaint);

            // Define what will add - icon or text
            if (childPosition % 2 == 0) {
                RectF rectBitmap = new RectF();
                rectBitmap.set(
                        layoutManager.getDecoratedLeft(child) + scale * offsetLeft,
                        layoutManager.getDecoratedTop(child) + scale * 43,
                        scale * 55,
                        layoutManager.getDecoratedBottom(child) - scale * 17
                );

                // Draw the image
                c.drawBitmap(bitmap,
                        null,
                        rectBitmap,
                        backgroundPaint);
            } else {
                RectF rectText = new RectF();
                rectText.set(
                        layoutManager.getDecoratedLeft(child) + scale * offsetLeft,
                        layoutManager.getDecoratedTop(child) + scale * 46,
                        scale * 78,
                        layoutManager.getDecoratedBottom(child) - scale * 18
                );

                // Draw first the background for the text
                c.drawRoundRect(rectText, 5, 5, explicitPaint);

                Paint paint = new Paint();
                paint.setColor(Color.BLACK);
                paint.setTextSize(scale * 9);
                paint.setFakeBoldText(true);

                // Draw the text
                c.drawText(mContext.getString(R.string.explicit),
                        layoutManager.getDecoratedLeft(child) + scale * 37,
                        layoutManager.getDecoratedBottom(child) - scale * 22,
                        paint
                );
            }
        }
    }
}
