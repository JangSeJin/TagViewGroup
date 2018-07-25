package com.hour24.tagviewgroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TagView extends RelativeLayout {

    public static final float DEFAULT_LINE_MARGIN = 0;
    public static final float DEFAULT_TAG_MARGIN = 0;
    public static final float DEFAULT_TAG_TEXT_PADDING_LEFT = 0;
    public static final float DEFAULT_TAG_TEXT_PADDING_TOP = 0;
    public static final float DEFAULT_TAG_TEXT_PADDING_RIGHT = 0;
    public static final float DEFAULT_TAG_TEXT_PADDING_BOTTOM = 0;

    public static final float LAYOUT_WIDTH_OFFSET = 2;

    /**
     * tag list
     */
    private List<ModelTag> mTags = new ArrayList<>();

    /**
     * System Service
     */
    private LayoutInflater mInflater;
    private ViewTreeObserver mViewTreeObserver;

    /**
     * listeners
     */
    private OnTagClickListener mClickListener;

    /**
     * view size param
     */
    private int mWidth;

    /**
     * layout initialize flag
     */
    private boolean mInitialized = false;

    /**
     * custom layout param
     */
    private int lineMargin;
    private int tagMargin;
    private int textPaddingLeft;
    private int textPaddingRight;
    private int textPaddingTop;
    private int textPaddingBottom;


    /**
     * constructor
     *
     * @param ctx
     */
    public TagView(Context ctx) {
        super(ctx, null);
        initialize(ctx, null, 0);
    }

    /**
     * constructor
     *
     * @param ctx
     * @param attrs
     */
    public TagView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        initialize(ctx, attrs, 0);
    }

    /**
     * constructor
     *
     * @param ctx
     * @param attrs
     * @param defStyle
     */
    public TagView(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
        initialize(ctx, attrs, defStyle);
    }

    /**
     * initalize instance
     *
     * @param ctx
     * @param attrs
     * @param defStyle
     */
    private void initialize(Context ctx, AttributeSet attrs, int defStyle) {
        mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewTreeObserver = getViewTreeObserver();
        mViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mInitialized) {
                    mInitialized = true;
                    drawTags();
                }
            }
        });

        // get AttributeSet
        TypedArray typeArray = ctx.obtainStyledAttributes(attrs, R.styleable.TagView, defStyle, defStyle);
        this.lineMargin = (int) typeArray.getDimension(R.styleable.TagView_lineMargin, dipToPx(this.getContext(), DEFAULT_LINE_MARGIN));
        this.tagMargin = (int) typeArray.getDimension(R.styleable.TagView_tagMargin, dipToPx(this.getContext(), DEFAULT_TAG_MARGIN));
        this.textPaddingLeft = (int) typeArray.getDimension(R.styleable.TagView_textPaddingLeft, dipToPx(this.getContext(), DEFAULT_TAG_TEXT_PADDING_LEFT));
        this.textPaddingRight = (int) typeArray.getDimension(R.styleable.TagView_textPaddingRight, dipToPx(this.getContext(), DEFAULT_TAG_TEXT_PADDING_RIGHT));
        this.textPaddingTop = (int) typeArray.getDimension(R.styleable.TagView_textPaddingTop, dipToPx(this.getContext(), DEFAULT_TAG_TEXT_PADDING_TOP));
        this.textPaddingBottom = (int) typeArray.getDimension(R.styleable.TagView_textPaddingBottom, dipToPx(this.getContext(), DEFAULT_TAG_TEXT_PADDING_BOTTOM));
        typeArray.recycle();
    }

    /**
     * onSizeChanged
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        if (width <= 0)
            return;
        mWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTags();
    }

    /**
     * tag draw
     */
    private void drawTags() {

        if (!mInitialized) {
            return;
        }

        // clear all tag
        removeAllViews();

        // layout padding left & layout padding right
        float total = getPaddingLeft() + getPaddingRight();

        int listIndex = 1;// List Index
        int indexBottom = 1;// The Tag to add below
        int indexHeader = 1;// The header tag of this line
        ModelTag tagPre = null;
        for (ModelTag item : mTags) {
            final int position = listIndex - 1;
            final ModelTag tag = item;

            // inflate tag layout
            View tagLayout = mInflater.inflate(R.layout.tagview_item, null);
            tagLayout.setId(listIndex);

            // tag text
            TextView tagView = (TextView) tagLayout.findViewById(R.id.tv_tag_item_contain);
            tagView.setText(tag.text);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tagView.getLayoutParams();
            params.setMargins(textPaddingLeft, textPaddingTop, textPaddingRight, textPaddingBottom);
            tagView.setLayoutParams(params);

            tagLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onClick(position, tag);
                    }
                }
            });

            // calculate　of tag layout width
            float tagWidth = tagView.getPaint().measureText(tag.text) + textPaddingLeft + textPaddingRight;
            // tagView padding (left & right)

            LayoutParams tagParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            //add margin of each line
            tagParams.bottomMargin = lineMargin;

            if (mWidth <= total + tagWidth + dipToPx(this.getContext(), LAYOUT_WIDTH_OFFSET)) {
                //need to add in new line
                if (tagPre != null) tagParams.addRule(RelativeLayout.BELOW, indexBottom);
                // initialize total param (layout padding left & layout padding right)
                total = getPaddingLeft() + getPaddingRight();
                indexBottom = listIndex;
                indexHeader = listIndex;
            } else {
                //no need to new line
                tagParams.addRule(RelativeLayout.ALIGN_TOP, indexHeader);
                //not header of the line
                if (listIndex != indexHeader) {
                    tagParams.addRule(RelativeLayout.RIGHT_OF, listIndex - 1);
                    tagParams.leftMargin = tagMargin;
                    total += tagMargin;
                }
            }
            total += tagWidth;
            addView(tagLayout, tagParams);
            tagPre = tag;
            listIndex++;

        }

    }

    /**
     * @param tag
     */
    public void addTag(ModelTag tag) {
        mTags.add(tag);
        drawTags();
    }

    public void addTags(List<ModelTag> tags) {
        if (tags == null) return;
        mTags = new ArrayList<>();
        if (tags.isEmpty())
            drawTags();
        for (ModelTag item : tags) {
            mTags.add(item);
        }
        drawTags();
    }

    public void addTags(String[] tags) {
        if (tags == null) return;
        for (String item : tags) {
            ModelTag tag = new ModelTag(item);
            mTags.add(tag);
        }
        drawTags();
    }


    /**
     * get tag list
     *
     * @return mTags TagObject List
     */
    public List<ModelTag> getTags() {
        return mTags;
    }

    /**
     * remove tag
     *
     * @param position
     */
    public void remove(int position) {
        if (position < mTags.size()) {
            mTags.remove(position);
            drawTags();
        }
    }

    /**
     * remove all views
     */
    public void removeAll() {
        mTags.clear(); //clear all of tags
        removeAllViews();
    }

    public int getLineMargin() {
        return lineMargin;
    }

    public void setLineMargin(float lineMargin) {
        this.lineMargin = dipToPx(getContext(), lineMargin);
    }

    public int getTagMargin() {
        return tagMargin;
    }

    public void setTagMargin(float tagMargin) {
        this.tagMargin = dipToPx(getContext(), tagMargin);
    }

    public int getTextPaddingLeft() {
        return textPaddingLeft;
    }

    public void setTextPaddingLeft(float textPaddingLeft) {
        this.textPaddingLeft = dipToPx(getContext(), textPaddingLeft);
    }

    public int getTextPaddingRight() {
        return textPaddingRight;
    }

    public void setTextPaddingRight(float textPaddingRight) {
        this.textPaddingRight = dipToPx(getContext(), textPaddingRight);
    }

    public int getTextPaddingTop() {
        return textPaddingTop;
    }

    public void setTextPaddingTop(float textPaddingTop) {
        this.textPaddingTop = dipToPx(getContext(), textPaddingTop);
    }


    public void setOnTagClickListener(OnTagClickListener listener) {
        this.mClickListener = listener;
    }

    /**
     * Listeners
     */
    public interface OnTagClickListener {
        void onClick(int position, ModelTag tag);
    }

    /**
     * Utils
     */
    public int dipToPx(Context c,float dipValue) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
