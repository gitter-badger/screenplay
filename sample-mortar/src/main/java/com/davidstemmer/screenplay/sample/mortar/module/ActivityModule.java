package com.davidstemmer.screenplay.sample.mortar.module;

import com.davidstemmer.screenplay.ImmutableStage;
import com.davidstemmer.screenplay.MutableStage;
import com.davidstemmer.screenplay.Stage;
import com.davidstemmer.screenplay.flow.Screenplay;
import com.davidstemmer.screenplay.sample.mortar.MainActivity;
import com.davidstemmer.screenplay.sample.mortar.presenter.NavigationMenuPresenter;
import com.davidstemmer.screenplay.sample.mortar.scene.DialogScene;
import com.davidstemmer.screenplay.sample.mortar.scene.PagedScene1;
import com.davidstemmer.screenplay.sample.mortar.scene.PagedScene2;
import com.davidstemmer.screenplay.sample.mortar.scene.PagedScene3;
import com.davidstemmer.screenplay.sample.mortar.scene.StackedScene;
import com.davidstemmer.screenplay.sample.mortar.scene.WelcomeScene;
import com.davidstemmer.screenplay.sample.mortar.view.DialogSceneView;
import com.davidstemmer.screenplay.sample.mortar.view.ModalSceneView;
import com.davidstemmer.screenplay.sample.mortar.view.NavigationMenuView;
import com.davidstemmer.screenplay.sample.mortar.view.PagedView1;
import com.davidstemmer.screenplay.sample.mortar.view.PagedView2;
import com.davidstemmer.screenplay.sample.mortar.view.WelcomeView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import flow.Backstack;
import flow.Flow;

/**
 * Created by weefbellington on 10/2/14.
 */
@Module(addsTo = ApplicationModule.class,
        injects = {
                DialogScene.class,
                DialogScene.Presenter.class,
                DialogSceneView.class,
                StackedScene.class,
                StackedScene.Presenter.class,
                ModalSceneView.class,
                MainActivity.class,
                NavigationMenuPresenter.class,
                NavigationMenuView.class,
                NavigationMenuView.class,
                PagedScene1.class,
                PagedScene1.Presenter.class,
                PagedView1.class,
                PagedScene2.class,
                PagedScene2.Presenter.class,
                PagedView2.class,
                PagedScene3.class,
                WelcomeScene.class,
                WelcomeScene.Presenter.class,
                WelcomeView.class
        })
public class ActivityModule {

    private ImmutableStage stage;


    @Provides @Singleton
    MutableStage mainStage() {
        return new MutableStage();
    }

    @Provides @Singleton
    Stage stageInterface(MutableStage mainStage) {
        return mainStage;
    }


    @Provides @Singleton
    Screenplay screenplay(Stage stage) {
        return new Screenplay(stage);
    }

    @Provides @Singleton
    Flow flow(WelcomeScene welcomeScene, Screenplay screenplay) {
        return new Flow(Backstack.single(welcomeScene), screenplay);
    }
}