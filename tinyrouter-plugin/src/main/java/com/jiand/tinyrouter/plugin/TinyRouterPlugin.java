package com.jiand.tinyrouter.plugin;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.logging.Logger;

/**
 * @author jiand
 */
public class TinyRouterPlugin implements Plugin<Project> {

    Logger logger = Logger.getLogger(TinyRouterPlugin.class.getName());
    @Override
    public void apply(Project target) {
        boolean isApp = target.getPlugins().hasPlugin(AppPlugin.class);
        if (!isApp){
            return;
        }
        //处理app的依赖
        RouterScanner.reset();
        AppExtension extension = target.getExtensions().getByType(AppExtension.class);
        extension.registerTransform(new TinyRouterTransform());
    }
}
