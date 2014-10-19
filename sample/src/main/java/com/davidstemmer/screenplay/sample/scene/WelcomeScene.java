package com.davidstemmer.screenplay.sample.scene;

import android.os.Bundle;
import android.view.View;

import com.davidstemmer.screenplay.sample.R;
import com.davidstemmer.screenplay.sample.scene.transformer.NoAnimationTransformer;
import com.davidstemmer.screenplay.stage.StandardScene;
import com.davidstemmer.screenplay.stage.director.PagedDirector;

import javax.inject.Inject;
import javax.inject.Singleton;

import butterknife.ButterKnife;
import flow.Flow;
import flow.Layout;
import mortar.ViewPresenter;

/**
 * Created by weefbellington on 10/2/14.
 */

@Layout(R.layout.welcome)
@Singleton
public class WelcomeScene extends StandardScene {

    private final PagedDirector director;
    private final NoAnimationTransformer transformer;

    @Inject
    public WelcomeScene(PagedDirector director, NoAnimationTransformer transformer) {
        this.director = director;
        this.transformer = transformer;
    }

    @Override
    public Director getDirector() {
        return director;
    }

    @Override
    public Transformer getTransformer() {
        return transformer;
    }

    @Singleton
    public static class Presenter extends ViewPresenter<View> {

        @Inject Flow flow;
        @Inject HomeScene homeScreen;
        
        @Override
        protected void onLoad(Bundle savedInstanceState) {
            super.onLoad(savedInstanceState);
            ButterKnife.inject(this, getView());
        }
    }

}
