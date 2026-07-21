package com.tamin.taminhamrah.utils.stepperView.internal;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @hide
 */
public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

	private final int verticalSpaceHeight;

	public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
		this.verticalSpaceHeight = verticalSpaceHeight;
	}

	@Override
	public void getItemOffsets(Rect outRect, @NonNull View view,
							   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
		outRect.bottom = verticalSpaceHeight;
	}

}