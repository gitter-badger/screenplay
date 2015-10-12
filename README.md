
```
=======================================================================================
+++++++++ SCREENPLAY + A minimalist application framework for Android +++++++++++++++++
=======================================================================================
```

###1. What is Screenplay?

Screenplay is a tiny, moderately opinionated Android application framework. It is designed for building apps with a particular kind of architecture: **single-activity**, with **no fragments**, **no dialogs**, and **small classes**.

It is driven by a few core principles:

1. Low complexity: monolithic UI components with [complex lifecycles](https://github.com/xxv/android-lifecycle) are bad and should be avoided.
2. Low friction: objects should be easy to create, and it should be easy to pass data between them.
3. High modularity: applications should be built out of small, reusable parts.

Screenplay makes it possible to run all of the application code in a single Activity, without relying on Fragments. It provides a number of tools for building lean, simple apps:

**A unifying UI abstraction:**
Screenplay applications are built out of objects called Stages. The role of the Stage is the role is similar to an Activity, Dialog or Fragment in another application. Stages are flexible, with support for full-screen and modal display modes. A page in your app may consist of a full-screen Stage and multiple partial-screen stages for displaying dialogs, drawers etc.

**Lightweight objects:**
Unlike Activities, Fragments or Dialogs, each Stage is a POJO (Plain Old Java Object). No factory methods required: there's no need to serialize data into a `Bundle`, or write a `Parcelable` implementation.  Just create `new Stage(...)`, pass it some arguments, and you're good to go. As a result, Screenplay is DI-friendly; [Dagger](https://github.com/square/dagger) is a fun partner.

**View hot swapping:**
Screenplay swaps Views in and out as Scenes are pushed and popped from the backstack. Views are removed from their parent when they are no longer needed to avoid leaking memory.

**Animated scene transitions:**
Screenplay selects animations to play based on the direction of navigation (forward/back) and the state of the scene (incoming/outgoing). Animations can be specified through XML or code.

**Component-oriented architecture:**
Each scene can have zero or more Components, which are notified of scene lifecycle events. Components provide a modular way of attaching behavior to a scene, encouraging code reuse and separation of concerns.

**Separation of display and presentation:**
You don't need to extend any custom `View` subclasses in a Screenplay application. Screenplay's powerful component-oriented architecture makes it easy to separate view presentation from display.

**Plugin support:**
Screenplay includes optional support for [Flow](https://github.com/square/flow) which is provided as a separate module. Flow provides an interface for managing the backstack, including pushing and popping new scenes from the stack, managing the history, etc.

###2. Sample Code

The easiest way to get a feel for Screenplay is to dive into the code. Two sample projects are available:

1. The [simple sample project](https://github.com/weefbellington/screenplay/tree/master/sample-simple) is the recommended place to start. Here you will find a minimally complex Screenplay application which showcases some of its features.
2. The [Dagger 2 sample project](https://github.com/weefbellington/screenplay/tree/master/sample-dagger2) is the same application with a DI-oriented structure, if dependency injecton is your cup of tea.

###3. Stages, Components and Transitions

The Stage is the core of the Screenplay navigation flow. Its responsibilities are very narrow: a Stage's main role is detaching and attaching view from its parent. Stages are lightweight and shouldn't include a lot of code -- it's the Components that do the heavy lifting, applying business logic when a scene pushed or popped from the stack.

#####3.1 Navigating between Stages

Using the Flow plugin, Screenplay can react to navigation events. The following provide a few examples of manipulating the backstack; refer to the [Flow documentation](http://corner.squareup.com/2014/01/mortar-and-flow.html) for futher information.

```java
    flow.set(new DetailStage(data), Flow.Direction.FORWARD); // add a scene
    flow.set(new RootStage(), Flow.Direction.REPLACE);       // add a scene, make it the root
    flow.set(existingStage, Flow.Direction.BACK);            // pop to a scene
    flow.goBack();                                           // go back one scene
```

It is recommended that you use a single Flow instance throughout the application. See Section (4.1) for more details.

#####3.2 The Stage lifecycle 

As previously noted, the Stage has only a few responsibilities: creating a View (`Stage#setUp`), destroying a View (`Stage#tearDown`) and getting the current view (`Stage#getView`).

A Stage's lifecycle is easy to understand. For an incoming Stage, setup happens in three discrete phases:

1. The `Stage` creates its View, which is attached to a parent ViewGroup
2. Scene `Components` are notified of initialization
3. A `Rigger` plays animations between the incoming and outgoing scene.

For an outgoing Stage, teardown is the reverse of setup:

1. The `Rigger` plays an animation between the incoming and outgoing scene
2. The `Components` are notified of teardown
3. The `Stage` removes its View, which is detached from the parent ViewGroup

#####3.3 An example Stage

The following is an example Stage, which extends the `XmlStage` class. It provides a layout ID, which is used to inflate the View. It also provides a `Rigger`, which is used to animate the transition between stages.

```java
public class ExampleStage extends XmlStage {

    private final Rigger rigger = new CrossfadeRigger();;

    public ExampleStage(DrawerPresenter drawer) {
        addComponent(new DrawerLockingComponent(drawer));
    }
    @Override
    public int getLayoutId() {
        return R.layout.example_stage;
    }
    @Override
    public Rigger getRigger() {
        return rigger;
    }
}
```

#####3.4 An example Stage.Component

The example above includes a `DrawerLockingComponent `, which locks the navigation drawer while the dialog is active. Although this particular stage only has a single Component, it is easy to include multiple components in a scene. Ideally, each component should be responsible for a small, well-defined chunk of logic: for example, binding click listeners, making a network call, or creating an animation.

```java
public class DrawerLockingComponent implements Stage.Component {

    private final DrawerPresenter drawer;

    public DrawerLockingComponent(DrawerPresenter drawer) {
        this.drawer = drawer;
    }

    @Override
    public void afterSetUp(Context context, Stage stage, boolean isInitializing) {
        drawer.setLocked(true);
    }

    @Override
    public void beforeTearDown(Context context, Scene scene, boolean isFinishing) {
        drawer.setLocked(false);
    }
}
```

#####3.5 Regular vs. modal Stages

Normally, after a new scene is pushed onto the stack, the old scene's View is detached from its parent View to free up memory.

With a modal Stage, the old Stages's view remains attached to the parent. Multiple Stages may be layered in this way. To make a Stage modal, override `Scene#isModal` to return `true`. Here is an example of a modal Stage:

```java
public class DialogStage extends XmlStage {

    private final Rigger rigger;

    public DialogScene(Context context) {
        this.rigger = new PopupRigger(context);
    }
    @Override
    public boolean isModal() {
        return true;
    }
    @Override
    public int getLayoutId() {
        return R.layout.dialog_stage;
    }
    @Override
    public Rigger getRigger() {
        return transformer;
    }
}
```

#####3.6 Stage animations 

A `Rigger` is responsible for applying animations between scenes. The Rigger receives a `Scene.Transition` object, which contains the data that the Rigger needs to create animations, including the `Screenplay.Direction`, the incoming and outgoing stages, and a `TransitionCallback` that must be called when the transition is complete.

```java
public class HorizontalSlideRigger extends TweenRigger {

    private static final Params params = new Params();

    static {
        params.forwardIn    = R.anim.slide_in_right;
        params.backIn       = R.anim.slide_in_left;
        params.backOut      = R.anim.slide_out_right;
        params.forwardOut   = R.anim.slide_out_left;
    }

    public HorizontalSlideRigger(Application context) {
        super(context, params);
    }
}
```

Screenplay provides two Rigger implementations to extend from: `TweenRigger` and `AnimatorRigger`. TweenRigger uses the [Animation](http://developer.android.com/reference/android/view/animation/Animation.html) class to create a transition, while the AnimatorTransition uses the [Animator](http://developer.android.com/reference/android/animation/Animator.html) class.

###4. Boilerplate

#####4.1 Bootstrapping (Flow)

You only need a little bit of boilerplate to bootstrap a Screenplay application. If you're using the Flow plugin, you'll need to create your main Flow. To ensure that the Flow object survives configuration changes, you can put it in a singleton class, or you can parcel the history object and recreate your Flow with each configuration change. We'll take the former approach here:

```java
public class SampleApplication extends Application {

    private final Flow flow = new Flow(Backstack.single(new HomeStage()));
    private static SampleApplication application;

    public void onCreate() { application = this; }

    public static SampleApplication getInstance()       { return application; }
    public static Flow getFlow()                        { return getInstance().flow; }
}
```

(alternatively to a static object on the Application class, you can use a dependency injection library such as [Dagger](http://square.github.io/dagger/))

In the onCreate() method of your main Activity, create your `ScreenplayDispatcher` object and bind it to your container view. All scenes will be inflated into the container view:

```java
public class MainActivity extends Activity {

    private Flow flow;
    private ScreenplayDispatcher dispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.main);

        flow = SampleApplication.getMainFlow();
        dispatcher = new ScreenplayDispatcher(this, container);
        dispatcher.setUp(flow);
    }
}
```

#####4.2 Handling Activity lifecycle events (Flow)

1. When the Activity is destroyed, you must call `ScreenplayDispatcher#tearDown`. This performs cleanup actions such as calling `Screenplay#tearDownVisibleScenes`, ensuring that your components receive the correct callbacks.
2. Override onBackPressed to route back button to the dispatcher:
```
public class MainActivity extends Activity {

    @Override public void onBackPressed() {
        if (!dispatcher.handleBackPressed()) {
            super.onBackPressed();
        }
    }
}
```

#####4.3 Managing configuration changes

By default, when a configuration change occurs, when `Screenplay#tearDownVisibleScenes` is called, a Scene's view is released and a new one is created. If instead you would like a scene and its view to be retained on configuration changes, override `Stage.teardownOnConfigurationChanges` to return `false`. Keep in mind that setting this to `false` means that the XML for the view will not be reloaded when a configuration change occurs.

###5. Odds and ends

The Screenplay object also exposes a `isTransitioning` method. This is useful for preventing multiple
button presses while two Scenes are in transition:

```java
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Ignore menu click if stage is transitioning
        if (screenplay.isTransitioning()) return true;
        
        switch (item.getItemId()) {
            ...
        }
    }
```

###6. Download

Screenplay is currently available as a beta snapshot. Grab it via Maven:

```xml
<dependency>
    <groupId>com.davidstemmer</groupId>
    <artifactId>screenplay</artifactId>
    <version>0.6.2-SNAPSHOT</version>
    <type>aar</type>
</dependency>
```

or Gradle:

```groovy
compile 'com.davidstemmer:screenplay:0.6.2-SNAPSHOT'
```

For Gradle, you'll have to add the Sonatype OSS snapshot repo to your build script:

```groovy
repositories {
    maven {
        url "https://oss.sonatype.org/content/repositories/snapshots"
    }
}
```

###7. Contributing
TODO

###8. Acknowledgements

Many thanks to the team at Square for their support of the open-source community, without which this
project wouldn't be possible.
