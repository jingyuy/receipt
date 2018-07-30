package dui.com.receipt.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class RecognizedView extends View {
    private int viewWidth;
    private int viewHeight;
    private Paint paint = new Paint();

    public RecognizedView(Context context) {
        super(context);
        init();
    }

    public RecognizedView(
            Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecognizedView(
            Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        viewWidth = xNew;
        viewHeight = yNew;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(0.0f, 0.0f, viewWidth, 0, paint);
        canvas.drawLine(viewWidth, 0.0f, viewWidth, viewHeight, paint);
        canvas.drawLine(0.0f, viewHeight, viewWidth, viewHeight, paint);
        canvas.drawLine(0.0f, 0.0f, 0.0f, viewHeight, paint);
    }
}
