package per.goweii.android.anole;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

import org.jetbrains.annotations.NotNull;

public class FooterBehaviorDependAppBar extends CoordinatorLayout.Behavior<View> {

    public FooterBehaviorDependAppBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(@NotNull CoordinatorLayout parent, @NotNull View child, @NotNull View dependency) {
        return dependency instanceof AppBarLayout;
    }

    private float maxMove = 0;

    @Override
    public boolean onDependentViewChanged(@NotNull CoordinatorLayout parent, @NotNull View child, @NotNull View dependency) {
        float moved = Math.abs(dependency.getTop());
        if (maxMove == 0) maxMove = moved;
        float translationY = (moved / maxMove) * child.getHeight();
        child.setTranslationY(child.getHeight() - translationY);
        return true;
    }
}