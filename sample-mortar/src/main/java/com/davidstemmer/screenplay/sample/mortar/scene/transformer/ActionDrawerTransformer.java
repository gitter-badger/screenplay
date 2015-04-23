package com.davidstemmer.screenplay.sample.mortar.scene.transformer;

import android.app.Application;

import com.davidstemmer.screenplay.sample.mortar.R;
import com.davidstemmer.screenplay.scene.transformer.TweenTransformer;

import javax.inject.Inject;

/**
 * Created by weefbellington on 10/7/14.
 */
public class ActionDrawerTransformer extends TweenTransformer {

    private static final Params params = new Params();

    static {
        params.forwardIn    = R.anim.slide_in_up;
        params.backIn       = -1;
        params.backOut      = R.anim.slide_out_down;
        params.forwardOut   = -1;
    }

    @Inject
    public ActionDrawerTransformer(Application context) {
        super(context, params);
    }
}
