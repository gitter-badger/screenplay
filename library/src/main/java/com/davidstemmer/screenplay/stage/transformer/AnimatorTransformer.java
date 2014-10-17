package com.davidstemmer.screenplay.stage.transformer;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;

import com.davidstemmer.screenplay.SceneCut;
import com.davidstemmer.screenplay.flow.Screenplay;
import com.davidstemmer.screenplay.stage.Scene;

import java.util.ArrayList;
import java.util.List;

import flow.Flow;

/**
 * Created by weefbellington on 10/16/14.
 */
public class AnimatorTransformer implements Scene.Transformer{

    private final Params params;
    private final Context context;

    public static class Params {
        public Params() {}
        public int forwardIn;
        public int forwardOut;
        public int backIn;
        public int backOut;
    }

    public AnimatorTransformer(Context context, Params params) {
        this.context = context;
        this.params = params;
    }

    @Override
    public void applyAnimations(SceneCut cut, Screenplay screenplay) {

        int out = cut.direction == Flow.Direction.FORWARD ? params.forwardOut : params.backOut;
        int in = cut.direction == Flow.Direction.FORWARD ? params.forwardIn : params.backIn;

        AnimatorSet animSet = new AnimatorSet();
        List<Animator> animators = new ArrayList<Animator>();
        if (out != -1 && cut.previousScene != null) {
            Animator animator = AnimatorInflater.loadAnimator(context, out);
            animator.setTarget(cut.previousScene);
            animators.add(animator);
        }
        if (in != -1 && cut.nextScene != null) {
            Animator animator = AnimatorInflater.loadAnimator(context, in);
            animator.setTarget(cut.nextScene);
            animators.add(animator);
        }
        animSet.addListener(new Listener(screenplay, cut));
        animSet.playTogether(animators);
    }

    private class Listener implements Animator.AnimatorListener {

        private final Screenplay screenplay;
        private final SceneCut cut;

        private Listener(Screenplay screenplay, SceneCut cut) {
            this.screenplay = screenplay;
            this.cut = cut;
        }

        @Override
        public void onAnimationStart(Animator animation) {}

        @Override
        public void onAnimationEnd(Animator animation) {
            screenplay.endCut(cut);
        }

        @Override
        public void onAnimationCancel(Animator animation) {}

        @Override
        public void onAnimationRepeat(Animator animation) {}
    }

}
