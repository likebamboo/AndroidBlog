package com.likebamboo.osa.android.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.likebamboo.osa.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://github.com/2dxgujun/AndroidTagGroup">参考https://github.com/2dxgujun/AndroidTagGroup</a>
 *
 * @author likebamboo
 * @version 1.0
 * @since 2015-05-18
 */
public class TagGroup extends ViewGroup {

    /**
     * tag 字体大小
     */
    private float mTagTextSize;

    /**
     * 字体背景
     */
    private int mTagBackgroundColor;

    /**
     * 字体背景
     */
    private int mTagBackgroundResId;

    /**
     * 字体颜色
     */
    private int mTagTextColor;

    /**
     * The horizontal tag spacing,
     */
    private int mHorizontalSpacing;

    /**
     * The vertical tag spacing
     */
    private int mVerticalSpacing;

    /**
     * 标签内边距
     */
    private int mHorizontalPadding;

    /**
     * 标签内边距
     */
    private int mVerticalPadding;

    /**
     * Listener used to dispatch tag click event.
     */
    private IOnTagClickListener mOnTagClickListener;

    public TagGroup(Context context) {
        this(context, null);
    }

    public TagGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // Load styled attributes.
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TagGroup, defStyleAttr, R.style.TagGroup);
        try {
            mTagTextColor = a.getColor(R.styleable.TagGroup_tagTextColor, Color.WHITE);
            try {
                mTagBackgroundColor = a.getColor(R.styleable.TagGroup_tagBackground, 0);
            } catch (Exception e) {
                mTagBackgroundResId = a.getResourceId(R.styleable.TagGroup_tagBackground, 0);
            }
            mTagTextSize = a.getDimensionPixelOffset(R.styleable.TagGroup_tagTextSize, (int) dp2px(14));
            mHorizontalSpacing = (int) a.getDimension(R.styleable.TagGroup_horizontalSpacing, dp2px(8));
            mVerticalSpacing = (int) a.getDimension(R.styleable.TagGroup_verticalSpacing, dp2px(4));

            mHorizontalPadding = (int) a.getDimension(R.styleable.TagGroup_horizontalPadding, dp2px(6));
            mVerticalPadding = (int) a.getDimension(R.styleable.TagGroup_verticalPadding, dp2px(1));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }

    }

    public void setTextSize(float textSize) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            TagView tagView = getTagViewAt(i);
            tagView.setTextSize(textSize);
        }
        requestLayout();
    }

    /**
     * @param resId
     */
    public void setTagColor(int resId) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            TagView tagView = getTagViewAt(i);
            tagView.setTextColor(resId);
        }
    }

    /**
     * @param resId
     */
    public void setTagBackgroundResource(int resId) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            TagView tagView = getTagViewAt(i);
            tagView.setBackgroundResource(resId);
        }
    }

    /**
     * @param color
     */
    public void setTagBackgroundColor(int color) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            TagView tagView = getTagViewAt(i);
            tagView.setBackgroundColor(color);
        }
    }

    public int getHorizontalSpacing() {
        return mHorizontalSpacing;
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        mHorizontalSpacing = horizontalSpacing;
        requestLayout();
    }

    public int getVerticalSpacing() {
        return mVerticalSpacing;
    }

    public void setVerticalSpacing(int verticalSpacing) {
        mVerticalSpacing = verticalSpacing;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

        int row = 0; // The row counter.
        int rowWidth = 0; // Calc the current row width.
        int rowMaxHeight = 0; // Calc the max tag height, in current row.

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();

            if (child.getVisibility() != GONE) {
                rowWidth += childWidth;
                if (rowWidth > widthSize) { // Next line.
                    rowWidth = childWidth; // The next row width.
                    height += rowMaxHeight + mVerticalSpacing;
                    rowMaxHeight = childHeight; // The next row max height.
                    row++;
                } else { // This line.
                    rowMaxHeight = Math.max(rowMaxHeight, childHeight);
                }
                rowWidth += mHorizontalSpacing;
            }
        }
        // Account for the last row height.
        height += rowMaxHeight;

        // Account for the padding too.
        height += getPaddingTop() + getPaddingBottom();

        // If the tags grouped in one row, set the width to wrap the tags.
        if (row == 0) {
            width = rowWidth;
            width += getPaddingLeft() + getPaddingRight();
        } else {// If the tags grouped exceed one line, set the width to match the parent.
            width = widthSize;
        }

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width,
                heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int parentLeft = getPaddingLeft();
        final int parentRight = r - l - getPaddingRight();
        final int parentTop = getPaddingTop();
        final int parentBottom = b - t - getPaddingBottom();

        int childLeft = parentLeft;
        int childTop = parentTop;

        int rowMaxHeight = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            if (child.getVisibility() != GONE) {
                if (childLeft + width > parentRight) { // Next line
                    childLeft = parentLeft;
                    childTop += rowMaxHeight + mVerticalSpacing;
                    rowMaxHeight = height;
                } else {
                    rowMaxHeight = Math.max(rowMaxHeight, height);
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height);

                childLeft += width + mHorizontalSpacing;
            }
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.tags = getTags().toArray(new String[]{});
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        setTags(ss.tags);
    }


    /**
     * Returns the tags array in group.
     *
     * @return the tag array
     */
    public List<String> getTags() {
        final int count = getChildCount();
        final List<String> tagList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final TagView tagView = getTagViewAt(i);
            tagList.add(tagView.getText().toString());
        }

        return tagList;
    }

    /**
     * Returns the tag view at the specified position in the group.
     *
     * @param index the position at which to get the tag view from
     * @return the tag view at the specified position or null if the position
     * does not exists within this group
     */
    protected TagView getTagViewAt(int index) {
        return (TagView) getChildAt(index);
    }

    public void setOnTagClickListener(IOnTagClickListener l) {
        mOnTagClickListener = l;
    }

    /**
     * @see #setTags(String...)
     */
    public void setTags(List<String> tagList) {
        setTags(tagList.toArray(new String[]{}));
    }

    /**
     * Set the tag to this group. It will remove all tags first.
     *
     * @param tags the tag list to set
     */
    public void setTags(String... tags) {
        removeAllViews();
        for (final String tag : tags) {
            if (TextUtils.isEmpty(tag)) {
                continue;
            }
            addTag(tag);
        }
    }

    /**
     * 删除标签
     *
     * @param tag
     */
    public void deleteTag(CharSequence tag) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        int index = 0;
        for (; index < getChildCount(); index++) {
            if (getTagViewAt(index).getText().equals(tag)) {
                break;
            }
        }
        if (index < getChildCount()) {
            removeViewAt(index);
        }
    }

    /**
     * 判断是否已经有标签
     *
     * @param tag
     */
    public boolean hasTag(CharSequence tag) {
        if (TextUtils.isEmpty(tag)) {
            return true;
        }
        return getTags().contains(tag + "");
    }

    /**
     * 添加标签
     *
     * @param tag
     */
    public void addTag(CharSequence tag) {
        addTag(getChildCount(), tag);
    }

    /**
     * 添加标签
     *
     * @param index
     * @param tag   the tag to append
     */
    public void addTag(int index, CharSequence tag) {
        // 如果已经有该标签，将标签提到最前面
        if (hasTag(tag)) {
            deleteTag(tag);
            index = 0;
        }
        final TagView tagView = new TagView(getContext());
        tagView.setText(tag);
        tagView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTagTextSize);
        tagView.setTextColor(mTagTextColor);
        if (mTagBackgroundColor != 0) {
            tagView.setBackgroundColor(mTagBackgroundColor);
        } else if (mTagBackgroundResId != 0) {
            tagView.setBackgroundResource(mTagBackgroundResId);
        }

        tagView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof TagView) {
                    TagView v = (TagView) view;
                    if (mOnTagClickListener != null) {
                        mOnTagClickListener.onTagClick(v.getText().toString().trim());
                    }
                }
            }
        });
        tagView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // 长按震动反馈
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                if (view instanceof TagView) {
                    TagView v = (TagView) view;
                    if (mOnTagClickListener != null) {
                        mOnTagClickListener.onTagLongClick(v.getText().toString().trim());
                    }
                    return true;
                }
                return false;
            }
        });

        // 设置标签内边距
        tagView.setPadding(mHorizontalPadding, mVerticalPadding, mHorizontalPadding, mVerticalPadding);
        addView(tagView, index, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    public float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public interface IOnTagClickListener {
        /**
         * @param tag tagName
         */
        void onTagClick(String tag);

        /**
         * tagLongClick
         *
         * @param tag tagName
         */
        void onTagLongClick(String tag);
    }

    /**
     * For {@link TagGroup} save and restore state.
     */
    static class SavedState extends BaseSavedState {
        int tagCount;
        String[] tags;
        int checkedPosition;
        String input;

        public SavedState(Parcel source) {
            super(source);
            tagCount = source.readInt();
            tags = new String[tagCount];
            source.readStringArray(tags);
            checkedPosition = source.readInt();
            input = source.readString();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            tagCount = tags.length;
            dest.writeInt(tagCount);
            dest.writeStringArray(tags);
            dest.writeInt(checkedPosition);
            dest.writeString(input);
        }

        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    /**
     * The tag view
     */
    class TagView extends TextView {
        public TagView(Context context) {
            super(context);
        }

        public TagView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }
    }
}