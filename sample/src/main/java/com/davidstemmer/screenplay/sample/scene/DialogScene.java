package com.davidstemmer.screenplay.sample.scene;

import android.os.Bundle;
import android.view.View;

import com.davidstemmer.screenplay.SceneState;
import com.davidstemmer.screenplay.flow.Screenplay;
import com.davidstemmer.screenplay.sample.R;
import com.davidstemmer.screenplay.sample.component.DrawerLockingComponent;
import com.davidstemmer.screenplay.sample.scene.transformer.PopupTransformer;
import com.davidstemmer.screenplay.scene.StandardScene;
import com.davidstemmer.screenplay.scene.rigger.ModalRigger;

import javax.inject.Inject;
import javax.inject.Singleton;

import butterknife.ButterKnife;
import butterknife.OnClick;
import flow.Flow;
import flow.Layout;
import mortar.ViewPresenter;

/**
 * Created by weefbellington on 10/2/14.
 */
@Layout(R.layout.dialog_scene)
public class DialogScene extends StandardScene {

    private final PopupTransformer transformer;
    private final ModalRigger rigger;

    @Inject
    public DialogScene(DrawerLockingComponent component, PopupTransformer transformer, ModalRigger rigger) {
        super(component);
        this.transformer = transformer;
        this.rigger = rigger;
    }

    @Override
    public Rigger getRigger() {
        return rigger;
    }

    @Override
    public Transformer getTransformer() {
        return transformer;
    }

    @Singleton
    public static class Presenter extends ViewPresenter<View> {

        @Inject Flow flow;
        @Inject Screenplay screenplay;

        @Override
        protected void onLoad(Bundle savedInstanceState) {
            ButterKnife.inject(this, getView());
            super.onLoad(savedInstanceState);
        }

        @OnClick(R.id.ok) void dismiss() {
            if (screenplay.getScreenState() != SceneState.TRANSITIONING) {
                flow.goBack();
            }
        }
    }
}