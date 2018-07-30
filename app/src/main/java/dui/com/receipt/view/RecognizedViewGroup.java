package dui.com.receipt.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import dui.com.receipt.db.Block;

public class RecognizedViewGroup extends FrameLayout {
    private int viewWidth;
    private int viewHeight;

    public RecognizedViewGroup(Context context) {
        super(context);
    }

    public RecognizedViewGroup(
            Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecognizedViewGroup(
            Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        viewWidth = xNew;
        viewHeight = yNew;
    }

    public void setBlocks(List<Block> blocks) {
        this.removeAllViews();
        for (Block block : blocks) {
            RecognizedView view = new RecognizedView(getContext());
            addView(view);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = block.right - block.left;
            layoutParams.height = block.bottom - block.top;
            view.setLayoutParams(layoutParams);
            view.setTranslationX(block.left);
            view.setTranslationY(block.top);
        }
    }
}
