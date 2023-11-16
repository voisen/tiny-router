# TinyRouter
----

> 一个轻量化，便捷的Android模块化开发依赖库，使用可以使各个模块解耦，提高开发效率。


##### [![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0)


#### 最新版本
模块| tinyrouter | tinyrouter-processor |tinyrouter-plugin
---|------------|----------------------|---
版本| 0.0.3      | 0.0.3                |0.0.3


#### 特点

- 轻量化，依赖少，体积小
- 便捷，方便在各个Activity、Fragment之间传递数据
- 解耦，各个模块之间解耦，提高开发效率
- 效率，主键值注入，Actvity回传数据轻松处理
- ...

#### 使用

1. 在项目的`build.gradle`文件中的 `buildscript` 中添加插件
   
```groovy
    buildscript {
        ...

        dependencies {
            classpath 'io.github.voisen:tinyrouter-plugin:lastVersion'
        }
    }

````

2. 在 `App` 中的 `build.gradle` 中应用 `tinyrouter` 插件

````groovy

apply plugin: 'com.android.application'
apply plugin: 'com.jiand.tinyrouter'

android {
    ...
}

````

3. 在其他模块中添加`TinyRouter`注解处理器依赖以及`TinyRouter`

````groovy
dependencies {
   ...
    annotationProcessor 'io.github.voisen:tinyrouter-processor:lastVersion'
    implementation 'io.github.voisen:tinyrouter:lastVersion'
}

````

4. 在`Application`中初始化`TinyRouter`

````java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化TinyRouter
        TinyRouter.getRouter().init(this);
    }
}



````

#### 代码使用

1. 给`Activity`、`Fragment`等添加路由路径

````java

@Route(path = "/activity/main")
public class YourActivity extends AppCompatActivity {
   ...
}

@Route(path = "/fragment/main")
public class YourFragment extends Fragment {
  ...
}

````

2. 在需要传递数据的地方使用`TinyRouter.getRouter().inject(this)`注入主键值，该方法会自动注入带有`@Autowired`注解的字段，并将主键值注入到对应的字段中。

````java
@Route(path = "/activity/main")
public class YourActivity extends AppCompatActivity {

    //表示传递过来的值的key是`time`
    @Autowired(name = "time")
    private String tm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注入主键值
        TinyRouter.getRouter().inject(this);
    }

````

3. 执行跳转或获取到执行路径下的对象

````java
//跳转到指定路径下的Activity
TinyRouter.getRouter().build("/activity/main").navigation();
//or
TinyRouter.getRouter().build("/activity/main").navigation(this);

//获取指定路径下的Activity
TinyRouter.getRouter().build("/test/to-activity")
        .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .withAction(Intent.ACTION_VIEW)
        .withString("time", new Date().toString())
        .greenChannel()//绿色通道， 跳过拦截器
        .navigation(this, new ActivityResultCallback() {
            @Override
            public void onActivityResult(int i, Intent intent) {
                //ToActivity通过setResult返回数据
                //Intent intent = new Intent();
                //intent.putExtra("data", "结束的回调: " + System.currentTimeMillis());
                //setResult(RESULT_OK, intent);
                Log.i(TAG, "onActivityResult: 返回的数据: " + intent);
            }
        });

//获取Fragment
Fragment fragment = (Fragment)TinyRouter.getRouter().build("/test/fragment")
                .navigation();

                
//获取Service
Class<YourService> serviceClass = (Class<YourService>)TinyRouter.getRouter().build("/test/service").navigation();

...


````

#### 拦截器使用

- 实现`TinyRouterInterceptor`接口，并在添加`@Interceptor`注解

````java
//priority 表示优先级， 值越小越优先执行拦截
@Interceptor(priority = 2)
public class Test2Interceptor implements ITinyRouterInterceptor {
    @Override
    public void intercept(TinyRouteMetaInfo routerMetaInfo, TinyRouterInterceptorCallback callback) {
        //不拦截， 继续处理
        callback.doContinue();
        //or 拦截处理
        callback.doInterrupt(throwable);
    }
}

````


#### License

````
Copyright 2023 jiand

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

````

