package com.davidstemmer.screenplay.sample.mortar.scene;

import android.os.Bundle;
import android.view.View;

import com.davidstemmer.screenplay.SceneState;
import com.davidstemmer.screenplay.Screenplay;
import com.davidstemmer.screenplay.sample.mortar.R;
import com.davidstemmer.screenplay.sample.mortar.component.DrawerLockingComponent;
import com.davidstemmer.screenplay.sample.mortar.scene.transformer.PopupRigger;
import com.davidstemmer.screenplay.stage.XmlStage;

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
public class DialogStage extends XmlStage {

    private final PopupRigger transformer;

    @Inject
    public DialogStage(PopupRigger transformer, DrawerLockingComponent component) {
        this.transformer = transformer;
        addComponents(component);
    }

    @Override
    public boolean isStacking() {
        return true;
    }

    @Override
    public Rigger getRigger() {
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
