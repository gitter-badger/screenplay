package com.davidstemmer.current.sample.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.davidstemmer.current.sample.screen.HomeScreen;

import javax.inject.Inject;

import mortar.Mortar;

/**
 * Created by weefbellington on 10/2/14.
 */
public class HomeView extends LinearLayout{

    @Inject HomeScreen.Presenter presenter;

    public HomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Mortar.inject(context, this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.takeView(this);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }
}
