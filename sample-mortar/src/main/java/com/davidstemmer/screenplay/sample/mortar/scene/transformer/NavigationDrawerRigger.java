package com.davidstemmer.screenplay.sample.mortar.scene.transformer;

import android.app.Application;

import com.davidstemmer.screenplay.sample.mortar.R;
import com.davidstemmer.screenplay.scene.rigger.TweenRigger;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by weefbellington on 10/7/14.
 */
@Singleton
public class NavigationDrawerRigger extends TweenRigger {

    private static final Params params = new Params();

    static {
        params.forwardIn    = R.anim.slide_in_left_drawer;
        params.backIn       = -1;
        params.backOut      = R.anim.slide_out_left_drawer;
        params.forwardOut   = -1;
    }

    @Inject
    public NavigationDrawerRigger(Application context) {
        super(context, params);
    }
}
